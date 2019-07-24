package com.journaldev.utils;

import android.util.Log;

import com.journaldev.androidalarmbroadcastservice.MyService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    private static final String TAG = MyService.class.getSimpleName();

    public static String convertCalendarToString(Calendar cal){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatted = format1.format(cal.getTime());
        Log.d(TAG,"End date is "+formatted);
        return formatted;
    }

    public static Date convertStringToDate(String s_date) throws ParseException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date=format1.parse(s_date);
        Log.d(TAG,"converted date is "+date.toString());
        return date;
    }

}
