package com.journaldev.androidalarmbroadcastservice;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import androidx.annotation.NonNull;

import com.journaldev.overlaymanager.FloatingLayout;
import com.journaldev.overlaymanager.NotificationListener;
import com.journaldev.smsmanager.MessageListener;
import com.journaldev.smsmanager.MessageReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    MediaPlayer mp        = null;
    Button btnStartAlarm, btnCancelAlarm;
    private final int REQUEST_PERMISSION_PHONE_STATE=1;
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    private static final String TAG = MainActivity.class.getSimpleName();
    public Intent intentService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getListOfInstalledApps();
        //intentService = new Intent(this, MyService.class);
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SYSTEM_ALERT_WINDOW)){
                showExplanation("Permission Needed", "Rationale", Manifest.permission.SYSTEM_ALERT_WINDOW, REQUEST_PERMISSION_PHONE_STATE);
            } else {
                requestPermission(android.Manifest.permission.SYSTEM_ALERT_WINDOW, 225);
            }
        }
        intentService = new Intent(getApplicationContext(),MyService.class);
       // intentService.setAction("com.journaldev.androidalarmbroadcastservice.MyService");

        if (!MyService.isOn) {
            getApplicationContext().startService(intentService);
        }
        finish();
//        Toast.makeText(this,"started main activity" , Toast.LENGTH_SHORT).show();
//        btnStartAlarm = findViewById(R.id.btnStartAlarm);
//        btnCancelAlarm = findViewById(R.id.btnCancelAlarm);
//
//        btnStartAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //startAlarm();
//            }
//        });
//
//        btnCancelAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //cancelAlarm();
//            }
//        });
    }

    private void checkRunTimePermission() {
        String[] permissionArrays = new String[] {
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissionArrays, 11111);
        }
    }


    public void getListOfInstalledApps(){
        final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }}

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this,
                new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onPermissionsGranted(int requestCode,  List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode,  List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this,"On activity result", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
