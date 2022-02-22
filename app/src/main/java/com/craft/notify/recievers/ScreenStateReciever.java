package com.craft.notify.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.preference.PreferenceManager;

public class ScreenStateReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
       if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("screenState","off").apply();
            Log.d("ScreenState reciever",Intent.ACTION_SCREEN_OFF);
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("screenState","on").apply();
            Log.d("ScreenState reciever",Intent.ACTION_SCREEN_ON);
              }
    }
}
