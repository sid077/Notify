package com.example.notify.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.example.notify.services.SpeakMessageService;

import java.util.Set;

public class NotifyReciever extends BroadcastReceiver {
    TextToSpeech textToSpeech;
    SharedPreferences settingSharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
       // settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(intent.hasExtra("name")){
            Log.d("reciever notification",intent.getStringExtra("name"));
        }
       // spellNotification("new Notification",context);
        Log.d("reciever ","called");
        SpeakMessageService.enqueueWork(context,SpeakMessageService.class,1,intent);

    }

}
