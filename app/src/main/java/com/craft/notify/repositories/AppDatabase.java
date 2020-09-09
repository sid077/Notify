package com.craft.notify.repositories;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.craft.notify.models.AppListDBDao;
import com.craft.notify.models.AppListPojo;



@Database(entities ={AppListPojo.class},version = 1,exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract AppListDBDao appListDBDao();

}
