package com.example.notify.models;

import android.content.pm.ActivityInfo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AppListPojo {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    int id;


    @ColumnInfo
    String packageName;
    @ColumnInfo
    int state;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Integer getState() {
        return state;
    }



    public AppListPojo(String packageName) {
        this.packageName = packageName;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public void setState(Integer state) {
        this.state = state;
    }

}
