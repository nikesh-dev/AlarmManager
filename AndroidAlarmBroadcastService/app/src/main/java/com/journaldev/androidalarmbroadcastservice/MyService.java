package com.journaldev.androidalarmbroadcastservice;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.journaldev.overlaymanager.FloatingLayout;
import com.journaldev.overlaymanager.NotificationListener;
import com.journaldev.smsmanager.MessageListener;
import com.journaldev.smsmanager.MessageReceiver;
import com.journaldev.utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MyService extends Service implements MessageListener, NotificationListener {

    private static final String TAG = MyService.class.getSimpleName();
    public MyService() {
        super();
    }
    public static boolean isOn = false;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    MediaPlayer mp = null;
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Log.d(TAG, "onHandleIntent");
//        Intent alarmIntent = new Intent(this, MyBroadCastReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
//       // alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 0, 10000, pendingIntent);
//    }

    @Override
    public void onDestroy()
    {
        isOn=false;
        Toast.makeText(getApplicationContext(), "service destoryed", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"Service is destroyed");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(),
                "Application Is Running in Background", Toast.LENGTH_SHORT)
                .show();

        Log.d(TAG,"Service instantiated");
        isOn = true;
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, MyBroadCastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        MyBroadCastReceiver.bindListener(this);
        MessageReceiver.bindListener(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(this.getBaseContext(),
//                "Application Is Running in Background", Toast.LENGTH_LONG)
//                .show();
        startAlarm();
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




    private void store(String key,String value){
        SharedPreferences settings = getSharedPreferences("myprefs", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value); // Commit editings editor.commit();
        editor.apply();
        if(editor.commit()) {
            Log.d(TAG, "Stored " + value + " in " + key);
        }else{
            Log.d(TAG,"Failed to store data");
        }

    }

    private SharedPreferences getSharedPreferences(String prefs) {
        return getApplicationContext().getSharedPreferences(prefs, Activity.MODE_PRIVATE);
    }

    private String recover(String key){
        SharedPreferences settings = getSharedPreferences("myprefs", Activity.MODE_PRIVATE);
        return settings.getString(key, "No name defined");
    }

    private Calendar preAlarmCheck() throws ParseException {
        String s_date=recover("date");
        Log.d(TAG,"Date stored is "+s_date);
        //SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd");
        //Date EndDate=format.parse(s_date);
        Date EndDate=Utils.convertStringToDate(s_date);
        Log.d(TAG,"Parsed date is "+EndDate.toString());
        Log.d(TAG,"Current Date is "+(new Date()).toString());
        if((new Date()).compareTo(EndDate)<0){
            Calendar calendar = Calendar.getInstance ();
            int hour=Integer.parseInt(recover("hour"));
            int minute=Integer.parseInt(recover("minute"));
            Log.d(TAG,"Setting hour value is "+hour);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            //calendar.set(Calendar.HOUR, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            Long currentTime = System.currentTimeMillis();
            if (calendar.getTime().getTime() < currentTime) {
                calendar.add(Calendar.DATE, 1);
            }
            Log.d(TAG,"Next alaram time is "+calendar.getTime().toString());
            return calendar;
        }else{
            Log.d(TAG,"ENd date already passed");
            cancelAlarm();
            return null;
        }
    }

    private void startAlarm(Calendar calendar) throws ParseException {
            Long currentTime = System.currentTimeMillis();
            long startScheduler = calendar.getTime().getTime() - currentTime;
        Calendar cal = Calendar.getInstance() ;
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.SECOND, 0);

           // alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis()+startScheduler,24*60*60*1000,pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,  calendar.getTime().getTime(),10*60*1000, pendingIntent);
            Log.d(this.getClass().getSimpleName(), calendar.getTime().toString()+" pluse 10 minyes "+(calendar.getTimeInMillis()));
        }else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), 10 * 60 * 1000, pendingIntent);
        }
            FloatingLayout.stopRepeat=false;
            Toast.makeText(getApplicationContext(), "Alarm setup done", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"alram sceduled success after "+startScheduler);
    }

    private void cancelAlarm() {
        FloatingLayout.stopRepeat=true;
       // Toast.makeText(getApplicationContext(), "Today's Alarm Cancelled", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"alram canceled");
    }

    private void exitAlarm() {
        FloatingLayout.stopRepeat=true;
        alarmManager.cancel(pendingIntent);
        Toast.makeText(getApplicationContext(), "Alarm exited", Toast.LENGTH_SHORT).show();
        Log.d(TAG,"alram canceled forever");
    }

    @Override
    public void alarmReceived(String message) {
      //  managerOfSound();
        initializeView();
    }

    private void initializeView() {
        FloatingLayout.stopRepeat=false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
            Intent intent = new Intent(getApplicationContext(),
                    FloatingLayout.class);
            startForegroundService(intent);
        } else {
            startService(new Intent(this, FloatingLayout.class));
        }
        //startService(new Intent(this, FloatingLayout.class));
        //finish();
    }

    protected void managerOfSound() {
        Log.d(TAG,"Playing music");
//        if (mp != null) {
//            mp.reset();
//            mp.release();
//        }

        mp = MediaPlayer.create(this, R.raw.sound);
        mp.start();
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                //code
                mp.release();
            }
        });
    }


    @Override
    public void messageReceived(String message) {
        //Toast.makeText(this, "New Message Received: " + message, Toast.LENGTH_SHORT).show();
        Log.d(this.getPackageName(),"Message received! Processing message");
        parseSms(message);
    }

    private void startAlarm(){
        try {
            Calendar alarmTime = preAlarmCheck();
            if (alarmTime != null) {
                startAlarm(alarmTime);
            }
        }catch(Exception e){
            Log.d(TAG,e.getMessage());
            e.printStackTrace();
        }
    }
    private void parseSms(String message){
        try {
            String[] details=message.split(" ");
            if(details!=null && !details.equals("")){
                switch (details[0]){
                    case "START":

                        String time=details[1];
                        String[] hourMin=time.split(":");
                        int hour= Integer.parseInt(hourMin[0]);
                        int minute=Integer.parseInt(hourMin[1]);
                        int days=Integer.parseInt(details[2]);
                        store("hour",hour+"");
                        store("minute",minute+"");
                        Calendar cal=Calendar.getInstance(TimeZone.getTimeZone("IST"));
                        cal.add(Calendar.DAY_OF_YEAR,days);
                        store("date", Utils.convertCalendarToString(cal));
                        cancelAlarm();
                        startAlarm();
                        Log.d(TAG,"started alarm");
                        break;
                    case "EXIT":
                        exitAlarm();
                    case "END":
                        cancelAlarm();
                        break;

                    default:
                        Toast.makeText(this, "Invalid message passed " + message, Toast.LENGTH_SHORT).show();
                }
            }
        }catch(Exception e){
            Log.d(TAG,"invalid time passed");
            Toast.makeText(this, "Invalid instruction format received", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
