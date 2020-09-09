package com.craft.notify.models;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AppListDBDao {
    @Query("select * from AppListPojo" )
    List<AppListPojo>getAllApps();
    @Update
    void updateApp(AppListPojo appListPojo);
    @Insert
    void insertAllApps(List<AppListPojo> appListPojos);
}
