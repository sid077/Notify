package com.craft.notify;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
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
import android.util.TimeUtils;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import androidx.room.Insert;
import androidx.room.Room;

import com.craft.notify.models.AppListPojo;
import com.craft.notify.recievers.RemoveServiceReciever;
import com.craft.notify.recievers.TimeReciever;
import com.craft.notify.repositories.AppDatabase;
import com.craft.notify.services.TTSService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.spec.ECField;
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
    private final boolean isSpoke = false;
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
    private Date dateTo;
    private Date dateFrom;

    @Override
    public void onCreate() {
        super.onCreate();

        String CHANNEL_ID = "notify_channel_01";
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,
                    "Notification listener service",
                    NotificationManager.IMPORTANCE_DEFAULT);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }


        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Notify is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("Tap for more information or to stop the app.").build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelID = notification.getChannelId();
        }
        startForeground(1, notification);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, getPackageName() + "RoomDb").build();
                appListPojoList = database.appListDBDao().getAllApps();


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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean toSpeak = false;
                if (settingSharedPreferences.getBoolean("isSelectedAppsEnabled", true)) {
                    for (int i = 0; i < appListPojoList.size(); i++) {
                        if (appListPojoList.get(i).getPackageName().equals(sbn.getPackageName())) {
                            AppListPojo appListPojo = appListPojoList.get(i);
                            if (appListPojo.getPackageName().equals(sbn.getPackageName())) {
                                if (appListPojo.getState().intValue() == 1) {
                                    toSpeak = true;
                                    break;
                                }


                            }

                        }

                    }
                    if (!toSpeak) {
                        return;
                    }

                }
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), TTSService.class);
                        intent.setAction("NOTIFY_RECIEVER");
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        PackageManager packageManager = getPackageManager();
                        try {
                            applicationInfo = packageManager.getApplicationInfo(sbn.getPackageName(), 0);
                            String appName = packageManager.getApplicationLabel(applicationInfo).toString();

                            if (catString != null) {
                                appName = "\"" + appName + "\"";
                                text = "you have a new " + catString + " on " + appName;

                            } else if (text == null) {
                                text = "You have a new Notification on " + appName;
                            }
                            intent.putExtra("name", text);

                            if (settingSharedPreferences.getString("spokenPackage", "none").equals(sbn.getPackageName())) {
                                Log.d("notificationservicelist", "notification spoken already");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        settingSharedPreferences.edit().putString("spokenPackage", "none").apply();
                                    }
                                }, 10000);

                                return;
                            } else {
                                try {
                                    //    reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("conditions match,"+text);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                startService(intent);
                                settingSharedPreferences.edit().putString("spokenPackage", sbn.getPackageName()).apply();
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    settingSharedPreferences.edit().putString("spokenPackage", "none").apply();
                                }
                            }, 10000);


                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();

                        }
                    }
                });
            }
        });
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            reference = firebaseDatabase.getReference();
            //  reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+Calendar.getInstance().get(Calendar.MINUTE))+Calendar.getInstance().get(Calendar.SECOND)).setValue("on notif posted called");
        } catch (Exception e) {
            e.printStackTrace();
        }
        settingSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


//        try {
//            if (isSleepTime()) {
//
//                return;
//            }
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return;
//        }
        try {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (settingSharedPreferences.getBoolean("isScreenOffEnabled", true) &&
                    powerManager.isInteractive()) {
                return;
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try{
            if (Objects.requireNonNull(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE)).toString().equalsIgnoreCase("you")) {
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }



        cat = "notDefined";
        if (sbn.getNotification().category != null)
            cat = sbn.getNotification().category;
        ispermittedCat = false;

        switch (cat) {
            case Notification.CATEGORY_MESSAGE:
                catString = "message";
                ispermittedCat = true;
                break;
            case Notification.CATEGORY_ERROR:
                catString = "error notification";
                ispermittedCat = true;
            case Notification.CATEGORY_EMAIL:
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
            case Notification.CATEGORY_SERVICE:
            case Notification.CATEGORY_SOCIAL:
            case Notification.CATEGORY_STATUS:
            case Notification.CATEGORY_PROMO:
            case Notification.CATEGORY_ALARM:
            case Notification.CATEGORY_RECOMMENDATION:

                ispermittedCat = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            sbnId = sbn.getUid();
        }
        thread.start();



    }

    private boolean isSleepTime() throws ParseException {
        String from = settingSharedPreferences.getString("timeFrom", "10:00 PM");
        String to = settingSharedPreferences.getString("timeTo", "07:00 AM");
        boolean isdif = false;
        if (from.charAt(6) == 'P' && to.charAt(6) == 'A') {
            isdif = true;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        dateFrom = dateFormat.parse(from);
        dateTo = dateFormat.parse(to);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1);
        calendar.set(Calendar.DAY_OF_WEEK, 5);

        Date isDate = calendar.getTime();
        isDate.setDate(1);
        long timeTo = dateTo.getTime();
        if (isdif) {
            dateTo.setDate(2);
            timeTo = dateTo.getTime();
        }
        long is = isDate.getTime();


        return dateFrom.getTime() < isDate.getTime() && timeTo > isDate.getTime();
    }

    private long getEndTime() {


        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1);
        calendar.set(Calendar.DAY_OF_WEEK, 5);

        Date d = calendar.getTime();
        return dateTo.getTime() - d.getTime();
    }

    private long getStartTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 0, 1);
        calendar.set(Calendar.DAY_OF_WEEK, 5);

        Date d = calendar.getTime();
        return dateFrom.getTime() - d.getTime();
    }


//

    private boolean updateSharedpref(StatusBarNotification sbn, Map<String, String> map) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] activeStatusBarNotification = notificationManager.getActiveNotifications();
        for (StatusBarNotification s : activeStatusBarNotification) {
            if (s.getKey().equals(sbn.getKey())) {


                return true;
            }
        }
        return false;


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
    public void onTaskRemoved(Intent rootIntent) {
        try {
            reference.child(String.valueOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + Calendar.getInstance().get(Calendar.MINUTE)) + Calendar.getInstance().get(Calendar.SECOND)).setValue("ontaskremoved  notification service list called");

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("NotificationService", "onStartCommmand");


        return START_NOT_STICKY;
    }

    @Override
    public void onListenerDisconnected() {

        super.onListenerDisconnected();
    }
}
