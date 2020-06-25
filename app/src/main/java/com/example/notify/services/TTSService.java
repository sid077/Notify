package com.example.notify.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.example.notify.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import static android.media.AudioRecord.SUCCESS;

public class TTSService extends Service implements TextToSpeech.OnInitListener,TextToSpeech.OnUtteranceCompletedListener {
    TextToSpeech textToSpeech;
    private String string;
    FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    public boolean isspoken,isinit;
    private MediaPlayer mediaPlayer;
    private IBinder mBinder = new ServiceBinder();
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
        reference = database.getReference();
      textToSpeech = new TextToSpeech(this,this);
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("oncreate tts service");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!intent.hasExtra("name")){
            return START_NOT_STICKY;
        }
        string = intent.getStringExtra("name");
     //   firebaseDatabase =FirebaseDatabase.getInstance();
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("onstart Command tts service");
        if(isinit&&textToSpeech!=null&&string!=null){
            textToSpeech.speak(string,TextToSpeech.QUEUE_FLUSH,null);
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
        textToSpeech.speak(string,TextToSpeech.QUEUE_ADD,null);
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue(status);
       int result= textToSpeech.setLanguage(Locale.US);
        if(status==SUCCESS){
            if(result != TextToSpeech.LANG_NOT_SUPPORTED&&result!=TextToSpeech.LANG_MISSING_DATA&&string!=null){
                textToSpeech.speak(string,TextToSpeech.QUEUE_FLUSH,null);
                isinit =true;
            }
        }

    }

    @Override
    public void onDestroy() {
        Log.d("onDestroyTTS service","called");
        reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("tts service destroyed tts service");
//        if(mediaPlayer!=null){
//            mediaPlayer.stop();
//            mediaPlayer.release();
//        }
       // PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("spokenPackage","none");
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


}
