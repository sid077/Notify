package com.example.notify;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.Display;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.hardware.display.DisplayManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.room.Room;

import com.example.notify.models.AppListPojo;
import com.example.notify.recievers.NotifyReciever;
import com.example.notify.recievers.screenStateReciever;
import com.example.notify.repositories.AppDatabase;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private com.example.notify.recievers.screenStateReciever screenStateReciever;


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
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateReciever = new screenStateReciever();
        registerReceiver(new screenStateReciever(),intentFilter);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database = Room.databaseBuilder(getApplicationContext(),AppDatabase.class,getPackageName()+"RoomDb").build();

            }
        });
        thread.start();
//            NotificationReviever reviever = new NotificationReviever();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.example.notify.NOTIFYME");
//            registerReceiver(reviever,intentFilter);

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

        try {
            if(isSleepTime()){
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        try {

            if (settingSharedPreferences.getBoolean("isScreenOffEnabled", true)&&
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("screenState","off").equals("on")){
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

//
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<AppListPojo> appListPojoList = database.appListDBDao().getAllApps();
                for(AppListPojo appListPojo: appListPojoList) {
                    if (appListPojo.getPackageName().toString().equals(sbn.getPackageName())) {
                        if (settingSharedPreferences.getBoolean("isSelectedAppsEnabled", true)) {
                            if (appListPojo.getState().intValue() != 1) {
                                return;
                            }
                        }
                            sbnId = appListPojo.getId();
                            break;


                    }
                }

                    Handler handler = new Handler(getMainLooper());
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Intent intent = new Intent(getApplicationContext(), NotifyReciever.class);
                            intent.setAction("NOTIFY_RECIEVER");
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            PackageManager packageManager = getPackageManager();
                            try {
                                applicationInfo = packageManager.getApplicationInfo(sbn.getPackageName(),0);
                                if(catString!=null){
                                    text = "you have a new "+catString+" on "+ packageManager.getApplicationLabel(applicationInfo);

                                }
                                else if(text==null){
                                    text = "You have a new Notification on "+ packageManager.getApplicationLabel(applicationInfo);
                                }
                                intent.putExtra("name",text);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),sbnId,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP,0,pendingIntent);

                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    handler.post(runnable);

                }

        });
        thread.start();





//
//        if(isCallBackFromRemoved){
//            return;
//        }
//        ispackageTobeSpelled = false;
//        sbnStack.push(sbn);
//        if(ispackageTobeSpelled){
//            return;
//        }
//        if(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString().equalsIgnoreCase("you")){
//            return;
//        }
//        settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        if(!settingSharedPreferences.getBoolean("isScreenOffEnabled",false)){
//            Log.d("isScreenOfEnabled", String.valueOf(false));
//            return;
//        }
//        if(!settingSharedPreferences.getBoolean("isSelectedAppsEnabled",true)){
//            ispackageTobeSpelled=true;
//        }
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if(database!=null){
//                    appListPojos = database.appListDBDao().getAllApps(); for(AppListPojo a: appListPojos){
//                        if(sbn.getPackageName().equals(a.getPackageName())&&!ispackageTobeSpelled&&a.getState()!=null&&a.getState()==1) {
//                            ispackageTobeSpelled = true;
//
//                        }
//                        Handler handler = new Handler(getApplicationContext().getMainLooper());
//
//                        Runnable runnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                SharedPreferences sharedPreferences  = getSharedPreferences("MySpref", MODE_PRIVATE);
//
//                                sharedPreferences.edit().putString(sbn.getPackageName(), sbn.getKey()).apply();
//                                if(!ispackageTobeSpelled){
//                                    Log.d("spelled?",String.valueOf(ispackageTobeSpelled));
//                                    return;
//                                }
//
//                                if(sbn.getKey().equals(sharedPreferences.getString(sbn.getPackageName(),"0"))) {
//                                    if (sbn.getPackageName().equals("com.whatsapp")) {
//                                        handleWhatsappNotification(sbn);
//                                        return;
//
//                                    } else {
//                                        try {
//                                            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(sbn.getPackageName(), 0);
//                                            spellNotification("You have a new notification, I think that's from "+getPackageManager().getApplicationLabel(applicationInfo).toString());
//
//                                        } catch (PackageManager.NameNotFoundException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }
//
//                            }nces
//                        };
//                        handler.post(runnable);
//                    }
//                }
//
//            }
//        });
//        thread.start();
//

       int x=0;
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("called handler","this time");
//            }
//        },2000);
    }

    private boolean isSleepTime() throws ParseException {
        settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleWhatsappNotification(StatusBarNotification sbn) {

        String s = sbn.getNotification().category;
        if(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString().contains("backup")||sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString().contains("checking")){
            return;
        }


            if (sbn.getNotification().extras!=null&sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString().equalsIgnoreCase("whatsapp")){
                String text =  "You Have "+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT)+" on Whatsapp !";
                spellNotification( text);
            }
//           else if(sbn.getNotification().category==null||sbn.getNotification().tickerText==null){
//                return;
//            }
            else {

                String string = "You Have a message from "+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE)+" on Whatsapp !";
                spellNotification(string);
            }
        }
//        if(!sbn.isGroup()){
//            String text  = "You have a new Whatsapp message from"+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE);
//            spellNotification(text);
//
//
//        }
//        else {

        //}

//fr-fr-x-vlf#male_3-local,
    private void spellNotification(final String text) {

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                String  voiceName =  settingSharedPreferences.getString("announcerName","en-us-x-sfg#male_2-local");

                Set<Voice> voices1 =textToSpeech.getVoices();
              Voice voice  = textToSpeech.getVoice();
              if(voices1!=null) {

                  for (Voice v : voices1) {
                      if (v.getName().equals(voiceName)) ;
                      voice = v;
                      break;
                  }

                  textToSpeech.setVoice(voice);

              }

                textToSpeech.speak(text,TextToSpeech.QUEUE_ADD,null);


            }
        });
    }

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
        unregisterReceiver(screenStateReciever);
    }
}
