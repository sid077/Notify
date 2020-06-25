package com.example.notify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.preference.PreferenceManager;
import androidx.transition.Transition;

import com.example.notify.models.VoicePojoRoom;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class SpeakMessageService extends JobIntentService implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {
    TextToSpeech textToSpeech;
    SharedPreferences settingSharedPreferences ;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;

    private Voice voice;
    private String text;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String voiceString  = intent.getStringExtra("voiceString");
        Type type = new TypeToken<Voice>(){}.getType();
        voice =new Gson().fromJson(voiceString,Voice.class);
        text = intent.getStringExtra("name");
        textToSpeech = new TextToSpeech(getApplicationContext(),this);


        spellNotification(intent.getStringExtra("name"),getApplicationContext());

    }
    private void spellNotification(final String text, Context context) {
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("speak notifications called");

        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "notify:tts");
        wakeLock.acquire(300000);
//        textToSpeech = new TextToSpeech(context, status -> {

//            String  voiceName =  settingSharedPreferences.getString("announcerName","en-us-x-sfg#male_2-local");
//
//
//            Set<Voice> voices1 =textToSpeech.getVoices();
//            Voice voice  = textToSpeech.getVoice();
//            if(voices1!=null) {
//
//                for (Voice v : voices1) {
//                    if (v.getName().equals(voiceName)) {
//                        voice = v;
//                        break;
//                    }
//                }
//
//                textToSpeech.setVoice(voice);
//
//            }
//            if(voice!=null)
//            textToSpeech.setVoice(voice);
//            if (settingSharedPreferences != null) {
//                float speed = settingSharedPreferences.getInt("speed", 50);
//                speed = (speed/100)*2;
//
//
//                textToSpeech.setSpeechRate(speed);
//            }
//            if(settingSharedPreferences.getBoolean("translator",false)){
//
//                TranslatorOptions translatorOptions = new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.fromLanguageTag(voice.getLocale().getLanguage())).build();
//                Translator translator = Translation.getClient(translatorOptions);
//                DownloadConditions conditions = new DownloadConditions.Builder().build();
//                translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
//                            @Override
//                            public void onSuccess(String s) {
//                                textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);
//                                reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("spoken");
//
//                            }
//                        })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Log.d("translation failed",e.getMessage());
//
//                                    }
//                                });
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e.printStackTrace();
//                    }
//                });
//
//            }else {
//                textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
//                reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("spoken");

          //  }



//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("service unbind");

        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("rewind");

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("TaskRemoved");
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

    }

    @Override
    public void onInit(int status) {
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue(status);
        textToSpeech.speak(text,TextToSpeech.QUEUE_ADD,null);




    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {

    }
}
