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
import com.example.notify.services.TTSService;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Set;

public class NotifyReciever extends BroadcastReceiver {
    TextToSpeech textToSpeech;
    SharedPreferences settingSharedPreferences;
    @Override
    public void onReceive(Context context, Intent intent) {
       // settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        FirebaseDatabase.getInstance().getReference().child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("Notify receiver called");

        if(intent.hasExtra("name")){
            Log.d("reciever notification",intent.getStringExtra("name"));
        }
       // spellNotification("new Notification",context);
        Log.d("reciever ","called");
        Intent intent1 = new Intent(context, TTSService.class);
        intent1.putExtra("name",intent.getStringExtra("name"));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        context.startService(intent1);





        // SpeakMessageService.enqueueWork(context,SpeakMessageService.class,1,intent);

    }


}
