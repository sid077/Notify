package com.craft.notify.recievers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.craft.notify.NotificationServiceList;

import static android.content.Context.ALARM_SERVICE;

public class TimeReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, NotificationServiceList.class));
        }else{
            context.startService(new Intent(context, NotificationServiceList.class));
        }
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        Intent i = new Intent(context, RemoveServiceReciever.class);
//        i.setAction("TIME_STOP_SERVICE_RECEIVER");
//        long time = intent.getExtras().getLong("time");
//        PendingIntent pendingIntent = PendingIntent.getService(context,PendingIntent.FLAG_UPDATE_CURRENT,i,0);
//        alarmManager.set(AlarmManager.RTC_WAKEUP,time,pendingIntent);

    }
}
