package com.craft.notify.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.craft.notify.services.TTSService;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class NotifyReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseDatabase.getInstance().getReference().child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("Notify receiver called");

        if(intent.hasExtra("name")){
            Log.d("reciever notification",intent.getStringExtra("name"));
        }

        Log.d("reciever ","called");
        Intent intent1 = new Intent(context, TTSService.class);
        intent1.putExtra("name",intent.getStringExtra("name"));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        context.startService(intent1);






    }


}
