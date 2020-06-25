package com.example.notify.activities;

import androidx.annotation.NonNull;
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

import android.animation.Animator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
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
import android.widget.TextView;

import com.example.notify.NotificationServiceList;
import com.example.notify.R;
import com.example.notify.adapters.RecyclerAdapterAppList;
import com.example.notify.listeners.RecyclerItemClickListener;
import com.example.notify.models.AppListPojo;
import com.example.notify.repositories.AppDatabase;
import com.example.notify.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;

;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerViewAppList ;
    MainViewModel viewModel ;
    private AppDatabase database;
    MainActivity activity = this;
    CardView cardViewToggle;
    TextView textViewToggle;
    float x;
    float y;
    private boolean isColorSecondary;
  //  private Switch switchToogleMain;
    private FloatingActionButton fabToogle;
    private ConstraintLayout constraintLayoutMainCard;
    private Toolbar toolbar;

    @Override
    protected void onResume() {
        super.onResume();
        TransitionDrawable transitionDrawable = (TransitionDrawable) ((View)constraintLayoutMainCard).getBackground();

        if (isMyServiceRunning(NotificationServiceList.class)) {

            transitionDrawable.startTransition(1000);

            textViewToggle.setTextColor(getColor(R.color.primaryTextColor));
            textViewToggle.setText(getString(R.string.toggle_main_on));
            // cardViewToggle.setCardBackgroundColor(getResources().getColor(R.color.secondaryColor));


        }
        else {

        transitionDrawable.resetTransition();
               // transitionDrawable.reverseTransition(1000);



          // transitionDrawable.reverseTransition(1000);
           textViewToggle.setText(R.string.toggle_main_off);
           // switchToogleMain.setChecked(false);
            //cardViewToggle.setCardBackgroundColor(getResources().getColor(R.color.primaryColor));
         //  textViewToggle.setTextColor(getResources().getColor(R.color.primaryTextColor));
           textViewToggle.setTextColor(getColor(R.color.secondaryTextColor));
        }
//        switchToogleMain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                int startRad =0;
//                Intent intent = new Intent(MainActivity.this, NotificationServiceList.class);
//                int endRad = (int )Math.hypot(buttonView.getWidth(),buttonView.getHeight());
//                Animator animator = ViewAnimationUtils.createCircularReveal(buttonView, (int)x, (int)y,startRad,endRad);
//
//                animator.setDuration(750).addListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
//                animator.start();
//
//            }
//        });



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(getApplicationContext());
        Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.bottom_up);
        ViewGroup hiddenPanel = findViewById(R.id.contraintMain);

        bottomUp.setDuration(1000);
        hiddenPanel.startAnimation(bottomUp);


        hiddenPanel.setVisibility(View.VISIBLE);
        toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitleTextColor(getColor(R.color.whiteColor));
        setSupportActionBar(toolbar);

        recyclerViewAppList = findViewById(R.id.recyclerViewAppList);
        cardViewToggle = findViewById(R.id.cardViewToggle);
        textViewToggle = findViewById(R.id.textViewToggle);
//        switchToogleMain = findViewById(R.id.switchToogleMain);
//        switchToogleMain.setTextOn(getString(R.string.toggle_main_on));
//        switchToogleMain.setTextOff(getString(R.string.toggle_main_off));
        constraintLayoutMainCard = findViewById(R.id.constraintLayout2);
        fabToogle = findViewById(R.id.floatingActionButtonToogle);
        if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("isNewUser",false)){
        addCoachMark();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(MainActivity.this,NotificationServiceList.class));
        }


        cardViewToggle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                x= event.getX();
                y = event.getY();
                return false;
            }
        });


        RecyclerAdapterAppList adapterAppList = new RecyclerAdapterAppList(new ArrayList<AppListPojo>(),activity);



        viewModel =new  ViewModelProvider(this).get(MainViewModel.class);
        //observer
        viewModel.getListMutableLiveData().observe(this, new Observer<List<AppListPojo>>() {
            @Override
            public void onChanged(List<AppListPojo> appListPojos) {
                RecyclerAdapterAppList adapterAppList = new RecyclerAdapterAppList(appListPojos,activity);
                recyclerViewAppList.setAdapter(adapterAppList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerViewAppList.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewAppList.getContext(),linearLayoutManager.getOrientation());
                recyclerViewAppList.addItemDecoration(dividerItemDecoration);
                //recyclerViewAppList.getAdapter().notifyDataSetChanged();
               // recyclerViewAppList.getRecycledViewPool().setMaxRecycledViews(TYPE_CAROUSEL,0);
            }
        });

        recyclerViewAppList.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(),recyclerViewAppList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int posititon) {
                Log.d("rv","singleTap");
               SwitchMaterial aSwitch= recyclerViewAppList.getChildViewHolder(view).itemView.findViewById(R.id.switch1);
                if(aSwitch.isChecked()){
                    aSwitch.setChecked(false);
                    ((RecyclerAdapterAppList)recyclerViewAppList.getAdapter()).getAppListPojos().get(posititon).setState(0);

                }
                else {
                    aSwitch.setChecked(true);
                    ((RecyclerAdapterAppList)recyclerViewAppList.getAdapter()).getAppListPojos().get(posititon).setState(1);


                }

                        updateAppInAppList(((RecyclerAdapterAppList)recyclerViewAppList.getAdapter()).getAppListPojos().get(posititon));



            }

            @Override
            public void onLongItemClick(View view, int position) {
                Log.d("rv","LongTap");
            }
        }));
        View.OnClickListener listener = v -> fabToogle.animate().rotation(360f*4).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent1 = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//                        intent1.putExtra(Settings.EXTRA_APP_PACKAGE,"com.example.notify");
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            intent1.putExtra(Settings.EXTRA_CHANNEL_ID,NotificationServiceList.channelID);
//                        }
                if(isMyServiceRunning(NotificationServiceList.class)){
                    stopService(new Intent(getApplicationContext(),NotificationServiceList.class));
                }
                startActivity(intent1);

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
//        final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
//            @Override
//            public boolean onDown(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public void onShowPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onSingleTapUp(MotionEvent e) {
//                return false;
//            }
//
//            @Override
//            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//                return false;
//            }
//
//            @Override
//            public void onLongPress(MotionEvent e) {
//
//            }
//
//            @Override
//            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                return false;
//            }
//        });
//        recyclerViewAppList.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//                View child =recyclerViewAppList.findChildViewUnder(e.getX(),e.getY());
//                if(child!=null&&gestureDetector.onTouchEvent(e)) {
//                    int pos = recyclerViewAppList.getChildLayoutPosition(child);
//                    RecyclerAdapterAppList adapterAppList = (RecyclerAdapterAppList) rv.getAdapter();
//                    updateAppInAppList(adapterAppList.getAppListPojos().get(pos));
//                    Switch sw = child.findViewById(R.id.switch1);
//
//                }
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
//
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });


        //store app list
        new Thread(new Runnable() {
            @Override
            public void run() {
                database = viewModel.getDatabaseInstance();
              try {
                  if(database.appListDBDao().getAllApps().size()==0)
                      throw new NullPointerException();
                  viewModel.setListMutableLiveData(database.appListDBDao().getAllApps());


              }
              catch (Exception e){
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




//        Intent intent = new Intent(this, NotificationServiceList.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        }
//        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> pkgAppsList = getPackageManager().queryIntentActivities( mainIntent, 0);
//        List<AppListPojo> appListPojoList = new ArrayList<>();
//        for(int i=0;i<pkgAppsList.size();i++){
//           appListPojoList.add(new AppListPojo(pkgAppsList.get(i).activityInfo));
//        }

    }

    private void addCoachMark() {
       Intent intent = new Intent(this,SliderActivity.class);
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
               }
               catch (IllegalStateException e){
                   e.printStackTrace();
               }

            }
        }).start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(database!=null)
            database.close();
    }

    public void updateAppInAppList(final AppListPojo appListPojo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase appDatabase;
                if(database!=null){
                    appDatabase = database;
                }
                else {
                    appDatabase = viewModel.getDatabaseInstance();
                }
                if(appDatabase!=null){
                    appDatabase.appListDBDao().updateApp(appListPojo);
                }
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menuBtnSettings){
            Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.menuBtnHelp){
            Intent intent = new Intent(getApplicationContext(),SliderActivity.class);
            intent.putExtra("isFromHelp",true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_appbar_menu,menu);
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
//    private boolean stopRunningService(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                stopService(new Intent(getComponentName(x)));
//                return true;
//            }
//        }
//        return false;
//    }

}
