package com.journaldev.androidalarmbroadcastservice;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.journaldev.overlaymanager.FloatingLayout;
import com.journaldev.overlaymanager.NotificationListener;
import com.journaldev.smsmanager.MessageListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MyBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = MyBroadCastReceiver.class.getSimpleName();
    Context context;
    private static NotificationListener nListener;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.d(TAG,"BOOT COMPLETED intent received");
            Intent serviceIntent = new Intent(context, MyService.class);
            Log.d(TAG,"Starting MyService");
            context.startService(serviceIntent);
        } else {
            //initializeView();
            Toast.makeText(context.getApplicationContext(), "Alarm Manager just ran", Toast.LENGTH_LONG).show();
            Log.d(this.getClass().getSimpleName(),"alarm executed "+System.currentTimeMillis() +(60*1000));
            nListener.alarmReceived("true");
        }

    }

    public static void bindListener(NotificationListener listener){

        nListener = listener;
    }

}
