package com.craft.notify.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.craft.notify.recievers.RemoveServiceReciever;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

import static android.media.AudioRecord.SUCCESS;

public class TTSService extends Service implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {
    TextToSpeech textToSpeech;
    private String string;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    public boolean isspoken,isinit;
    private MediaPlayer mediaPlayer;
    private SharedPreferences settingSharedPreferences;
    private IBinder mBinder = new ServiceBinder();
    private Voice selectedVoice;

    public class ServiceBinder extends Binder {
       TTSService getService()
        {
            return TTSService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        settingSharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        reference = database.getReference();
      textToSpeech = new TextToSpeech(this,this);
      //  reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("oncreate tts service");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
       // Intent i = new Intent(this, RemoveServiceReciever.class);
       // i.setAction("TIME_RECEIVER");

//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),12,i,PendingIntent.FLAG_CANCEL_CURRENT);
//        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,18*3600*1000,pendingIntent);

        if(!intent.hasExtra("name")){
            return START_NOT_STICKY;
        }
        if(settingSharedPreferences !=null){
            settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }
        string = intent.getStringExtra("name");
     //   firebaseDatabase =FirebaseDatabase.getInstance();
    //    reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("onstart Command tts service");
        if(isinit&&textToSpeech!=null&&string!=null){
            textToSpeech.speak(string,TextToSpeech.QUEUE_ADD,null);
      //      reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("spoken but not from on init");

        }

//        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pacman_death);
//        try {
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        new  Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                stopSelf(startId);
            }
        }, 10*1000);

        return START_NOT_STICKY;

    }

    @Override
    public void onInit(int status) {
        AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            if(textToSpeech.isSpeaking()){
                textToSpeech.stop();
            }
            return;
        }
//        textToSpeech.speak(string,TextToSpeech.QUEUE_ADD,null);
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                String  voiceName =  settingSharedPreferences.getString("announcerName","en-us-x-sfg#male_2-local");
//
//
                Set<Voice> voices1 =textToSpeech.getVoices();
                selectedVoice = textToSpeech.getVoice();
                if(voices1!=null) {

                    for (Voice v : voices1) {
                        if (v.getName().equals(voiceName)) {
                            selectedVoice = v;
                            break;
                        }
                    }



                }

                new Handler(getMainLooper()).post(new Runnable() {


                    @Override
                    public void run() {
                        textToSpeech.setVoice(selectedVoice);
                    //    reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue(status);
                        int result= textToSpeech.setLanguage(Locale.US);
                        if(status==SUCCESS){
                            if(settingSharedPreferences!=null){
                                float speed = settingSharedPreferences.getInt("speed",50);
                                speed = (speed/100)*2;
                                textToSpeech.setSpeechRate(speed);
                            }


                            if(result != TextToSpeech.LANG_NOT_SUPPORTED&&result!=TextToSpeech.LANG_MISSING_DATA&&string!=null){
                                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("translator",false)){
                                    FirebaseTranslatorOptions translatorOptions = new FirebaseTranslatorOptions.Builder()
                                            .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                            .setTargetLanguage(FirebaseTranslateLanguage.languageForLanguageCode(selectedVoice.getLocale().getLanguage()))
                                            .build();
                                    FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
                                            .getTranslator(translatorOptions);
                                    translator.translate(string).addOnFailureListener(e -> textToSpeech.speak(string,TextToSpeech.QUEUE_ADD,null))
                                            .addOnSuccessListener(s -> {

                                                textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);

                                            });


                                }
                                else {
                                    textToSpeech.speak(string, TextToSpeech.QUEUE_ADD, null);
                                }
                                isinit =true;
                            }
                        }
                    }
                });
            }
        });
        thread.start();


    }

    @Override
    public void onDestroy() {
        Log.d("onDestroyTTS service","called");
     //   reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("tts service destroyed tts service");
//        if(mediaPlayer!=null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
    //    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("spokenPackage","none");
        if(textToSpeech!=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
      //  onDestroy();
    }
    public static boolean hasInternetConnection(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.
                getSystemService(Context.CONNECTIVITY_SERVICE);

        final Network network = connectivityManager.getActiveNetwork();
        final NetworkCapabilities capabilities = connectivityManager
                .getNetworkCapabilities(network);

        return capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }


}
