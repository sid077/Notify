package com.example.notify.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.preference.PreferenceManager;
import androidx.transition.Transition;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.Set;

public class SpeakMessageService extends JobIntentService {
    TextToSpeech textToSpeech;
    SharedPreferences settingSharedPreferences ;
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        spellNotification(intent.getStringExtra("name"),getApplicationContext());

    }
    private void spellNotification(final String text, Context context) {

        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                String  voiceName =  settingSharedPreferences.getString("announcerName","en-us-x-sfg#male_2-local");


                Set<Voice> voices1 =textToSpeech.getVoices();
                Voice voice  = textToSpeech.getVoice();
                if(voices1!=null) {

                    for (Voice v : voices1) {
                        if (v.getName().equals(voiceName)) {
                            voice = v;
                            break;
                        }
                    }

                    textToSpeech.setVoice(voice);

                }
                if (settingSharedPreferences != null) {
                    float speed = settingSharedPreferences.getInt("speed", 50);
                    speed = (speed/100)*2;


                    textToSpeech.setSpeechRate(speed);
                }
                if(settingSharedPreferences.getBoolean("translator",false)){

                    TranslatorOptions translatorOptions = new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.fromLanguageTag(voice.getLocale().getLanguage())).build();
                    Translator translator = Translation.getClient(translatorOptions);
                    DownloadConditions conditions = new DownloadConditions.Builder().build();
                    translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("translation failed",e.getMessage());

                                        }
                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });

                }else
                textToSpeech.speak(text,TextToSpeech.QUEUE_ADD,null);


            }
        });
    }
}
