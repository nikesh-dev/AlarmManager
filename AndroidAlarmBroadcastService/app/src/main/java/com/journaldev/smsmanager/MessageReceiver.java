package com.journaldev.smsmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.journaldev.smsmanager.MessageListener;

public class MessageReceiver extends BroadcastReceiver {

    private static MessageListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(), "message received");
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get(
                "pdus");

        for(int i=0; i<pdus.length; i++){
            SmsMessage smsMessage = SmsMessage.
                    createFromPdu((byte[]) pdus[i]);
            String message =
                    "Sender : " + smsMessage.getDisplayOriginatingAddress()
                            +
                            "Time in millisecond: " + smsMessage.getTimestampMillis()
                            +
                            "Message: " + smsMessage.getMessageBody();
            message=smsMessage.getMessageBody();
            Log.d(this.getClass().getSimpleName(), message+" "+ smsMessage.getDisplayOriginatingAddress());
            if(smsMessage.getDisplayOriginatingAddress().equals("+918050566392")||
                    smsMessage.getDisplayOriginatingAddress().equals("+919611777036")||
                    smsMessage.getDisplayOriginatingAddress().equals("+919108459376")||
                    smsMessage.getDisplayOriginatingAddress().equals("+919940196586")) {
                mListener.messageReceived(message );
            }
        }
    }

    public static void bindListener(MessageListener listener){

        mListener = listener;
    }
}

