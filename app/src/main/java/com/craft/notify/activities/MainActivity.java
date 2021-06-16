package com.craft.notify.activities;

import android.animation.Animator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.craft.notify.NotificationServiceList;
import com.craft.notify.R;
import com.craft.notify.adapters.RecyclerAdapterAppList;
import com.craft.notify.listeners.RecyclerItemClickListener;
import com.craft.notify.models.AppListPojo;
import com.craft.notify.repositories.AppDatabase;
import com.craft.notify.services.JobUtil;
import com.craft.notify.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewAppList;
    MainViewModel viewModel;
    MainActivity activity = this;
    CardView cardViewToggle;
    TextView textViewToggle;
    float x;
    float y;
    String uid;
    private AppDatabase database;
    private boolean isColorSecondary;
    //  private Switch switchToogleMain;
    private FloatingActionButton fabToogle;
    private ConstraintLayout constraintLayoutMainCard;
    private Toolbar toolbar;
    private ImageButton imageButtonMenu;
    private TextView textViewTitle;
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseAuth firebaseAuth;

    public FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    @Override
    protected void onResume() {
        super.onResume();
        TransitionDrawable transitionDrawable = (TransitionDrawable) constraintLayoutMainCard.getBackground();


        if (isMyServiceRunning(NotificationServiceList.class)) {

            transitionDrawable.startTransition(1000);
            toolbar.setTitleTextColor(Color.WHITE);
            textViewTitle.setTextColor(Color.WHITE);
            toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_white_24dp));
            textViewToggle.setTextColor(getColor(R.color.primaryTextColor));
            toolbar.setBackgroundColor(getColor(R.color.primaryColor));
            imageButtonMenu.setImageDrawable(getDrawable(R.drawable.ic_more_vert_white_24dp));
            textViewToggle.setText(getString(R.string.toggle_main_on));
            // cardViewToggle.setCardBackgroundColor(getResources().getColor(R.color.secondaryColor));


        } else {

            transitionDrawable.resetTransition();

            toolbar.setBackgroundColor(getColor(R.color.cardColor));
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
                toolbar.setTitleTextColor(Color.BLACK);
                toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_black_24dp));
                textViewTitle.setTextColor(Color.BLACK);
                imageButtonMenu.setImageDrawable(getDrawable(R.drawable.ic_more_vert_black_24dp));

            } else {
                toolbar.setTitleTextColor(Color.WHITE);
                textViewTitle.setTextColor(Color.WHITE);
                toolbar.setOverflowIcon(getDrawable(R.drawable.ic_more_vert_white_24dp));
                imageButtonMenu.setImageDrawable(getDrawable(R.drawable.ic_more_vert_white_24dp));


            }


            textViewToggle.setText(R.string.toggle_main_off);

            textViewToggle.setTextColor(getColor(R.color.secondaryTextColor));
        }



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser curentUser = firebaseAuth.getCurrentUser();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getApplicationContext());
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAuth = FirebaseAuth.getInstance();
        JobUtil.scheduleJob(getApplicationContext());
        Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_up);
        ViewGroup hiddenPanel = findViewById(R.id.contraintMain);

        bottomUp.setDuration(750);
        hiddenPanel.startAnimation(bottomUp);


        hiddenPanel.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(getColor(R.color.whiteColor));
        setSupportActionBar(toolbar);

        recyclerViewAppList = findViewById(R.id.recyclerViewAppList);
        cardViewToggle = findViewById(R.id.cardViewToggle);
        textViewToggle = findViewById(R.id.textViewToggle);
        imageButtonMenu = findViewById(R.id.imageButtonMenu);
        textViewTitle = findViewById(R.id.textViewTitle);

        constraintLayoutMainCard = findViewById(R.id.constraintLayout2);
        fabToogle = findViewById(R.id.floatingActionButtonToogle);
//        if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isNewUser",false)){
//        addCoachMark();
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this, NotificationServiceList.class));
        }
//        startService(new Intent(this, NotificationCollectorMonitorService.class));


        cardViewToggle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getX();
                y = event.getY();
                return false;
            }
        });


        RecyclerAdapterAppList adapterAppList = new RecyclerAdapterAppList(new ArrayList<AppListPojo>(), activity);


        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //observer
        viewModel.getListMutableLiveData().observe(this, new Observer<List<AppListPojo>>() {
            @Override
            public void onChanged(List<AppListPojo> appListPojos) {
                RecyclerAdapterAppList adapterAppList = new RecyclerAdapterAppList(appListPojos, activity);
                recyclerViewAppList.setAdapter(adapterAppList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewAppList.setLayoutManager(linearLayoutManager);
                int[] ATTRS = new int[]{android.R.attr.listDivider};

                TypedArray a = obtainStyledAttributes(ATTRS);
                Drawable divider = a.getDrawable(0);
                int inset = 8;
                InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, inset, 0);
                a.recycle();

                DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
                itemDecoration.setDrawable(insetDivider);
                recyclerViewAppList.addItemDecoration(itemDecoration);
                //recyclerViewAppList.getAdapter().notifyDataSetChanged();
                // recyclerViewAppList.getRecycledViewPool().setMaxRecycledViews(TYPE_CAROUSEL,0);
            }
        });

        recyclerViewAppList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerViewAppList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int posititon) {
                Log.d("rv", "singleTap");
                SwitchMaterial aSwitch = recyclerViewAppList.getChildViewHolder(view).itemView.findViewById(R.id.switch1);
                if (aSwitch.isChecked()) {
                    aSwitch.setChecked(false);
                    ((RecyclerAdapterAppList) recyclerViewAppList.getAdapter()).getAppListPojos().get(posititon).setState(0);

                } else {
                    aSwitch.setChecked(true);
                    ((RecyclerAdapterAppList) recyclerViewAppList.getAdapter()).getAppListPojos().get(posititon).setState(1);


                }

                updateAppInAppList(((RecyclerAdapterAppList) recyclerViewAppList.getAdapter()).getAppListPojos().get(posititon));


            }

            @Override
            public void onLongItemClick(View view, int position) {
                Log.d("rv", "LongTap");
            }
        }));
        View.OnClickListener listener = v -> fabToogle.animate().rotation(360f * 4).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, uid);

                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Intent intent1 = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//
                if (isMyServiceRunning(NotificationServiceList.class)) {
                    stopService(new Intent(getApplicationContext(), NotificationServiceList.class));
                }
                startActivity(intent1);
                if (!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isNewUser", false)) {
                    addCoachMark();
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("isNewUser", true).apply();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
        cardViewToggle.setOnClickListener(listener);
        fabToogle.setOnClickListener(listener);
//
        imageButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOptionsMenu();
            }
        });


        //store app list
        new Thread(new Runnable() {
            @Override
            public void run() {
                database = viewModel.getDatabaseInstance();
                try {
                    if (database.appListDBDao().getAllApps().size() == 0)
                        throw new NullPointerException();
                    viewModel.setListMutableLiveData(database.appListDBDao().getAllApps());


                } catch (Exception e) {
                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
                    List<AppListPojo> appListPojoList = new ArrayList<>();
                    for (int i = 0; i < pkgAppsList.size(); i++) {
                        appListPojoList.add(new AppListPojo(pkgAppsList.get(i).activityInfo.packageName));
                    }
                    database.appListDBDao().insertAllApps(appListPojoList);
                    viewModel.setListMutableLiveData(database.appListDBDao().getAllApps());

                }


            }
        }).start();


        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            uid = task.getResult().getUser().getUid();

                            Log.d("Anonymous", "signed in successfully");
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, task.getResult().getUser().getUid());
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                        } else {
                            Log.d("Anonymous", "sign in  failed");

                        }
                    }
                });

//
    }

    private void addCoachMark() {
        Intent intent = new Intent(this, SliderActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    viewModel.setListMutableLiveData(database.appListDBDao().getAllApps());
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null)
            database.close();
    }

    public void updateAppInAppList(final AppListPojo appListPojo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase;
                if (database != null) {
                    appDatabase = database;
                } else {
                    appDatabase = viewModel.getDatabaseInstance();
                }
                if (appDatabase != null) {
                    appDatabase.appListDBDao().updateApp(appListPojo);
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuBtnSettings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.menuBtnHelp) {
            Intent intent = new Intent(getApplicationContext(), SliderActivity.class);
            intent.putExtra("isFromHelp", true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_appbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
//            } else if (requestCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) {
//                checkForUpdate();
//            }
        }
    }

//    private void checkForUpdate() {
//
//        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
//
//
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//
//                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
//
//                try {
//                    appUpdateManager.startUpdateFlowForResult(
//
//                            appUpdateInfo,
//
//                            AppUpdateType.IMMEDIATE,
//
//                            this,
//
//                            12);
//                } catch (IntentSender.SendIntentException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }
}
