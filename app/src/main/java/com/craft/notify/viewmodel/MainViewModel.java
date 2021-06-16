package com.craft.notify.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.craft.notify.models.AppListPojo;
import com.craft.notify.repositories.AppDatabase;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
  private   Context context;
  private final MutableLiveData<List<AppListPojo>> listMutableLiveData = new MutableLiveData<>();
    public MainViewModel(@NonNull Application application) {
        super(application);
        this. context = application.getApplicationContext();
    }

    public AppDatabase getDatabaseInstance(){
        AppDatabase database = Room.databaseBuilder(context,AppDatabase.class,context.getPackageName()+"RoomDb").build();
        return database;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public MutableLiveData<List<AppListPojo>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    public void setListMutableLiveData(List<AppListPojo> appListPojos) {
        this.listMutableLiveData.postValue(appListPojos);
    }
}
