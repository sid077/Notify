package com.example.notify.repositories;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.notify.models.AppListDBDao;
import com.example.notify.models.AppListPojo;
import com.example.notify.models.VoiceDao;
import com.example.notify.models.VoicePojoRoom;


@Database(entities ={AppListPojo.class, VoicePojoRoom.class},version = 1,exportSchema = false)

public abstract class AppDatabase extends RoomDatabase {
    public abstract AppListDBDao appListDBDao();
    public  abstract VoiceDao voiceDao();
}
