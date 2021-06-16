package com.craft.notify.recievers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.craft.notify.NotificationServiceList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class RemoveServiceReciever extends BroadcastReceiver {
    Date dateFrom,dateTo;
    @Override
    public void onReceive(Context context, Intent intent) {

        context.stopService(new Intent(context, NotificationServiceList.class));
        Log.d("RemoteServiceReciever","onRecieve");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent intent1 = new Intent(context, TimeReciever.class);
        intent.setAction("TIME_RECEIVER");
        try {
            isSleepTime(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getService(context,PendingIntent.FLAG_UPDATE_CURRENT,intent,0);
        intent1.putExtra("time",getStartTime());
       // alarmManager.set(AlarmManager.RTC_WAKEUP,getEndTime(),pendingIntent);


    }
    private boolean isSleepTime(Context context) throws ParseException {
        String from = PreferenceManager.getDefaultSharedPreferences(context).getString("timeFrom","10:00 PM");
        String to = PreferenceManager.getDefaultSharedPreferences(context).getString("timeTo","07:00 AM");

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        dateFrom =  dateFormat.parse(from);
        dateTo = dateFormat.parse(to);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970,0,1);
        calendar.set(Calendar.DAY_OF_WEEK,5);

        Date isDate =   calendar.getTime();
        isDate.setDate(1);

        return dateFrom.getTime() < isDate.getTime() && dateTo.getTime() > isDate.getTime();
    }
    private long getStartTime(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(1970,0,1);
        calendar.set(Calendar.DAY_OF_WEEK,5);

        Date d =   calendar.getTime();
        return dateFrom.getTime()-d.getTime();
    }
    private long getEndTime(){


        Calendar calendar = Calendar.getInstance();
        calendar.set(1970,0,1);
        calendar.set(Calendar.DAY_OF_WEEK,5);

        Date d =   calendar.getTime();
        return dateTo.getTime()- d.getTime();
    }
}
