package  com.journaldev.overlaymanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.journaldev.androidalarmbroadcastservice.R;

public class FloatingLayout extends Service {

	public static boolean stopRepeat=false;
	private WindowManager windowmanager;
	private View floatingview;
	public String Name = "";
	MediaPlayer mp = null;

	public FloatingLayout() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(this.getClass().getSimpleName(),"float called");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
			startMyOwnForeground();
		else
			startForeground(1, new Notification());

		floatingview = LayoutInflater.from(this).inflate(
				R.layout.floatinglayout, null);

		//getCallDetails(MainActivity.CallerNumber);
		//getAllSms(MainActivity.CallerNumber);
		TextView t = (TextView) floatingview.findViewById(R.id.name);
		t.setText("Reminder");
		int LAYOUT_FLAG=1;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
		} else {
			LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
		}
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				LAYOUT_FLAG,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT; // Initially view will be
														// added to top-left
														// corner
		params.x = 0;
		params.y = 100;

		windowmanager = (WindowManager) getSystemService(WINDOW_SERVICE);
		windowmanager.addView(floatingview, params);

		final View collapsedView = floatingview
				.findViewById(R.id.collapse_view);
		final View expandedView = floatingview
				.findViewById(R.id.expanded_container);
		expandedView.setVisibility(View.VISIBLE);
		// Set the close button
//		ImageView closeButtonCollapsed = (ImageView) floatingview
//				.findViewById(R.id.close_btn);
//		closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				stopSelf();
//			}
//		});

//		TextView closeButtonExpanded = (TextView) floatingview
//				.findViewById(R.id.close_box);
		final Button closeButton=(Button)floatingview.findViewById(R.id.button2);
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//
//				// hide your button here
//				try {
//					Toast.makeText(getApplicationContext(),
//							"button enbled", Toast.LENGTH_SHORT)
//							.show();
//					//closeButton.setVisibility(View.VISIBLE);
//				}catch(Exception e){
//					e.printStackTrace();
//				}catch(Error e){
//					e.printStackTrace();
//				}
//			}
//		}, 5000);
		closeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//stopPlaying();
				stopSelf();
			}
		});
//		closeButtonCollapsed.bringToFront();
//		closeButtonExpanded.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//
//				stopSelf();
//			}
//		});

		floatingview.findViewById(R.id.root_container).setOnTouchListener(
				new View.OnTouchListener() {
					private int initialX;
					private int initialY;
					private float initialTouchX;
					private float initialTouchY;

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:

							initialX = params.x;
							initialY = params.y;

							initialTouchX = event.getRawX();
							initialTouchY = event.getRawY();
							return true;
						case MotionEvent.ACTION_UP:
							int Xdiff = (int) (event.getRawX() - initialTouchX);
							int Ydiff = (int) (event.getRawY() - initialTouchY);

							if (Xdiff < 10 && Ydiff < 10) {
								if (isViewCollapsed()) {
									collapsedView.setVisibility(View.GONE);
									expandedView.setVisibility(View.VISIBLE);
								}
							}
							return true;
						case MotionEvent.ACTION_MOVE:
							params.x = initialX
									+ (int) (event.getRawX() - initialTouchX);
							params.y = initialY
									+ (int) (event.getRawY() - initialTouchY);

							windowmanager
									.updateViewLayout(floatingview, params);
							return true;
						}
						return false;
					}
				});
	}

	private boolean isViewCollapsed() {
		return floatingview == null
				|| floatingview.findViewById(R.id.collapse_view)
						.getVisibility() == View.VISIBLE;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (floatingview != null) {
			windowmanager.removeView(floatingview);
		}
		Log.d(this.getClass().getSimpleName(),"float service destroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(this.getClass().getSimpleName(),"float on start called "+stopRepeat);
		if(!stopRepeat){
			Log.d(this.getClass().getSimpleName(),"Alamr is setting");
			Calendar calendar = Calendar.getInstance() ;
			Log.d(this.getClass().getSimpleName()," truthr="+(System.currentTimeMillis()-calendar.getTimeInMillis()));
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.MINUTE,5);
			//calendar.add(Calendar.SECOND,45);
			//calendar.set(Calendar.SECOND, 0);
			Log.d(this.getClass().getSimpleName(),"calendar is "+calendar.getTime().toString());
			AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
//			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//				Intent i=new Intent(this, FloatingLayout.class);
//				//PendingIntent pi=PendingIntent.getBroadcast(this, 0, i, 0);
//				Intent i2=new Intent(this, FloatingLayout.class);
//				PendingIntent pi2=PendingIntent.getActivity(this, 0, i2, 0);
//
//				AlarmManager.AlarmClockInfo ac=
//						new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis() + (2 * 60 * 1000),
//								pi2);
//
//				alarm.setAlarmClock(ac, pi2);
//				//alarm.setAlarmClock (AlarmManager.RTC_WAKEUP, new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis() + (2 * 60 * 1000), PendingIntent.getService(this, 0, new Intent(this, FloatingLayout.class), 0)));
//				Log.d(this.getClass().getSimpleName(), calendar.getTime().toString()+" plus 2 mintes");
//			}
//			else
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
				alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),((5*60*1000)), PendingIntent.getService(this, 0, new Intent(this, FloatingLayout.class), PendingIntent.FLAG_CANCEL_CURRENT));
			}else{
				alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),  PendingIntent.getService(this, 0, new Intent(this, FloatingLayout.class), PendingIntent.FLAG_CANCEL_CURRENT));
			}
			//	Log.d(this.getClass().getSimpleName(),"nect alarm is "+(calendar.getTimeInMillis() +" "+calendar.getTime().toString()));
			//alarm.set(AlarmManager.RTC, System.currentTimeMillis()+(2*60*1000),
			//		PendingIntent.getService(this, 0, new Intent(this, FloatingLayout.class), 0));
			managerOfSound();
		}else{
			Log.d(this.getClass().getSimpleName(),"alarm is nnot settign next time");
		}
		return START_STICKY;
	}

	protected void managerOfSound() {
		//Log.d(TAG,"Playing music");
//        if (mp != null) {
//            mp.reset();
//            mp.release();
//        }
		Log.d(FloatingLayout.class.getSimpleName(),"Music called");
		mp = MediaPlayer.create(this, R.raw.sound);
		mp.start();
		mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				//code
				mp.reset();
				mp.release();
				Log.d(this.getClass().getSimpleName(),"played relased");
			}
		});
	}

	private void stopPlaying() {
		if (mp != null) {
			try {
				if (mp.isPlaying()) {
					mp.stop();
				}
			} catch (Error e) {
				e.printStackTrace();
			}
		}
	}

	private void startMyOwnForeground(){
		String NOTIFICATION_CHANNEL_ID = "com.journaldev.overlaymanager";
		String channelName = "My Background Service";
		NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
		chan.setLightColor(Color.BLUE);
		chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		assert manager != null;
		manager.createNotificationChannel(chan);

		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
		Notification notification = notificationBuilder.setOngoing(true)
				.setSmallIcon(R.drawable.googleicon)
				.setContentTitle("App is running in background")
				.setPriority(NotificationManager.IMPORTANCE_MIN)
				.setCategory(Notification.CATEGORY_SERVICE)
				.build();
		startForeground(2, notification);
	}
}
