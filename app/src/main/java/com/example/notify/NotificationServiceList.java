package com.example.notify;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.notify.models.AppListPojo;
import com.example.notify.models.VoicePojoRoom;
import com.example.notify.recievers.NotifyReciever;
import com.example.notify.repositories.AppDatabase;
import com.example.notify.services.TTSService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

public class NotificationServiceList extends NotificationListenerService {

    private TextToSpeech textToSpeech;
    private boolean isSpoke = false;
    private SharedPreferences sharedPreferences;

    private List<AppListPojo> appListPojoList;
    Stack<StatusBarNotification> sbnStack = new Stack<>();
    AppDatabase database;
    private List<AppListPojo> appListPojos;
    private boolean ispackageTobeSpelled;
    private boolean isCallBackFromRemoved;
    private SharedPreferences settingSharedPreferences;
    private String cat;
    private ApplicationInfo applicationInfo;
    private String name;
    private Boolean ispermittedCat;
    private String catString;
    private String text;
    private int sbnId;
    public static String channelID;

    private List<AppListPojo> appListPojoList1;
    private DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    private VoicePojoRoom voicePojoRoom;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        String CHANNEL_ID = "my_channel_01";
       NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
        }


        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build();
        channelID = notification.getChannelId();
        startForeground(1, notification);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
//        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        screenStateReciever = new screenStateReciever();
//        registerReceiver(new screenStateReciever(),intentFilter);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,getPackageName()+"RoomDb").build();
                appListPojoList= database.appListDBDao().getAllApps();


            }
        });
        thread.start();


    }

    @Override
    public void onListenerConnected() {
        Log.d("Notification listener", "connected");
        super.onListenerConnected();
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
       // super.onNotificationRemoved(sbn);
        isCallBackFromRemoved = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        voicePojoRoom = database.voiceDao().getVoices().get(0);
//
//                    }
//                    catch (IndexOutOfBoundsException e){
//                        e.printStackTrace();
//                        voicePojoRoom = new VoicePojoRoom();
//
//                    }
//                }
//            }).start();
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference();
            reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("on notif posted called");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        try {
            if(isSleepTime()){
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        try {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (settingSharedPreferences.getBoolean("isScreenOffEnabled", true)&&
                    powerManager.isInteractive()){
                   return;
                }


        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
        if(Objects.requireNonNull(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE)).toString().equalsIgnoreCase("you")){
            return;
        }



        cat = "notDefined";
        if(sbn.getNotification().category!=null)
       cat = sbn.getNotification().category;
        ispermittedCat = false;

        switch (cat){
           case Notification.CATEGORY_MESSAGE:
               catString = "message";
               ispermittedCat = true;
               break;
            case Notification.CATEGORY_ERROR:
                catString = "error notification";
                ispermittedCat = true;
            case  Notification.CATEGORY_EMAIL:
                text = "you have a new email";
                ispermittedCat = true;
                break;
            case Notification.CATEGORY_EVENT:
                text = "I guess you have a new Event, checkout!";
                ispermittedCat = true;
                break;
            case Notification.CATEGORY_NAVIGATION:
                text = "I guess you are traveling, Be Safe!, Happy Journey!";
                ispermittedCat = true;
                break;
            case Notification.CATEGORY_PROGRESS:
                text = "You Have a Progress Update, checkout!";
                ispermittedCat = true;
            case Notification.CATEGORY_REMINDER:
                text = "You have a new Remainder notification, checkOut";
                ispermittedCat = true;
            case Notification.CATEGORY_TRANSPORT:
            case  Notification.CATEGORY_SERVICE:
            case  Notification.CATEGORY_SOCIAL:
            case  Notification.CATEGORY_STATUS:
            case Notification.CATEGORY_PROMO:
            case Notification.CATEGORY_ALARM:
            case Notification.CATEGORY_RECOMMENDATION:

                ispermittedCat = true;
       }
//       if(!ispermittedCat){
//           return;
//       }
        if (settingSharedPreferences.getBoolean("isSelectedAppsEnabled", true)) {
            for(AppListPojo appListPojo: appListPojoList) {
                if (appListPojo.getPackageName().equals(sbn.getPackageName())) {
                    if (appListPojo.getState().intValue() != 1) {
                        return;
                    }
//                    sbnId = appListPojo.getId();
                    break;


                }
            }
        }
        sbnId = sbn.getUid();

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), TTSService.class);
        intent.setAction("NOTIFY_RECIEVER");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        PackageManager packageManager = getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(sbn.getPackageName(), 0);
            if (catString != null) {
                text = "you have a new " + catString + " on " + packageManager.getApplicationLabel(applicationInfo);

            } else if (text == null) {
                text = "You have a new Notification on " + packageManager.getApplicationLabel(applicationInfo);
            }
            intent.putExtra("name", text);
           // intent.putExtra("voice", (Serializable) voicePojoRoom);
         //   Type listType = new TypeToken<Voice>(){}.getType();
//           String stringVoice = new Gson().toJson(voicePojoRoom.getVoice(),listType);
           //intent.putExtra("voiceString", stringVoice);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),sbnId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,10,pendingIntent);
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if(settingSharedPreferences.getString("spokenPackage","none").equals(sbn.getPackageName())){
                Log.d("notificationservicelist","notification spoken already");

                return;
            }
            else{

                startService(intent);
                settingSharedPreferences.edit().putString("spokenPackage",sbn.getPackageName()).apply();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    settingSharedPreferences.edit().putString("spokenPackage","none").apply();
                }
            },10000);
            try {
                reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("conditions match, alarm manager called");

            }catch (Exception e){e.printStackTrace();
            }

        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();

        }

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                List<AppListPojo> appListPojoList = database.appListDBDao().getAllApps();
//
//                    Handler handler = new Handler(getMainLooper());
//                    Runnable runnable = new Runnable() {
//                        @Override
//                        public void run() {
//
//                            PackageManager packageManager = getPackageManager();
//                            try {
//                                applicationInfo = packageManager.getApplicationInfo(sbn.getPackageName(),0);
//                                if(catString!=null){
//                                    text = "you have a new "+catString+" on "+ packageManager.getApplicationLabel(applicationInfo);
//
//                                }
//                                else if(text==null){
//                                    text = "You have a new Notification on "+ packageManager.getApplicationLabel(applicationInfo);
//                                }
//                                intent.putExtra("name",text);
//                                //PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),sbnId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//                              if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("spokenPackage","nopackage").equals(sbn.getPackageName())){
//                                  spellNotification(text,getApplicationContext());
//                                  PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("spokenPackage",sbn.getPackageName());
//                              }
//
//                            } catch (PackageManager.NameNotFoundException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    };
//                    handler.post(runnable);
//
//                }
//
//        });
//        thread.start();





//
//
    }

    private boolean isSleepTime() throws ParseException {
        String from = settingSharedPreferences.getString("timeFrom","10:00 PM");
        String to = settingSharedPreferences.getString("timeTo","07:00 AM");

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date dateFrom ;
       dateFrom =  dateFormat.parse(from);
       Date dateTo = dateFormat.parse(to);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970,0,1);
        calendar.set(Calendar.DAY_OF_WEEK,5);

       Date isDate =   calendar.getTime();
       isDate.setDate(1);

       if(dateFrom.getTime()<isDate.getTime()&&dateTo.getTime()>isDate.getTime()){
           return true;
       }
       return false;
    }

//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private void handleWhatsappNotification(StatusBarNotification sbn) {
//
//        String s = sbn.getNotification().category;
//        if(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString().contains("backup")||sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString().contains("checking")){
//            return;
//        }
//
//
//            if (sbn.getNotification().extras!=null&sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString().equalsIgnoreCase("whatsapp")){
//                String text =  "You Have "+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)+" on Whatsapp !";
//                spellNotification( text,getApplicationContext());
//            }
////           else if(sbn.getNotification().category==null||sbn.getNotification().tickerText==null){
////                return;
////            }
//            else {
//
//                String string = "You Have a message from "+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE)+" on Whatsapp !";
//                spellNotification(string,getApplicationContext());
//            }
//        }
//        if(!sbn.isGroup()){
//            String text  = "You have a new Whatsapp message from"+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE);
//            spellNotification(text);
//
//
//        }
//        else {

        //}

//fr-fr-x-vlf#male_3-local,
//private void spellNotification(final String text) {
//
//    textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
//
//        String  voiceName =  settingSharedPreferences.getString("announcerName","en-us-x-sfg#male_2-local");
//
//
//        Set<Voice> voices1 =textToSpeech.getVoices();
//        Voice voice  = textToSpeech.getVoice();
//        if(voices1!=null) {
//
//            for (Voice v : voices1) {
//                if (v.getName().equals(voiceName)) {
//                    voice = v;
//                    break;
//                }
//            }
//
//            textToSpeech.setVoice(voice);
//
//        }
//        if (settingSharedPreferences != null) {
//            float speed = settingSharedPreferences.getInt("speed", 50);
//            speed = (speed/100)*2;
//
//
//            textToSpeech.setSpeechRate(speed);
//        }
//        if(settingSharedPreferences.getBoolean("translator",false)){
//
//            TranslatorOptions translatorOptions = new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.fromLanguageTag(voice.getLocale().getLanguage())).build();
//            Translator translator = Translation.getClient(translatorOptions);
//            DownloadConditions conditions = new DownloadConditions.Builder().build();
//            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    translator.translate(text).addOnSuccessListener(new OnSuccessListener<String>() {
//                        @Override
//                        public void onSuccess(String s) {
//                            textToSpeech.speak(s,TextToSpeech.QUEUE_ADD,null);
//
//                        }
//                    })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d("translation failed",e.getMessage());
//
//                                }
//                            });
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    e.printStackTrace();
//                }
//            });
//
//        }else
//
//            textToSpeech.speak(text,TextToSpeech.QUEUE_ADD,null);
//
//
//    });
//}

    private boolean updateSharedpref(StatusBarNotification sbn,Map<String,String> map) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification activeStatusBarNotification[] = notificationManager.getActiveNotifications();
        for(StatusBarNotification s: activeStatusBarNotification){
            if(s.getKey().equals(sbn.getKey())){


                return true;
            }
        }
        return false;


    }

    @Override
    public void onNotificationPosted(final StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);




    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            requestRebind(new ComponentName(getApplicationContext(),NotificationServiceList.class));
        }

        return START_STICKY;
    }

}
