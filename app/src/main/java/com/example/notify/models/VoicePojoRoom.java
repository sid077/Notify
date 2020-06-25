package com.example.notify.models;

import android.speech.tts.Voice;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.notify.repositories.Converter;

import java.io.Serializable;

@Entity

public class VoicePojoRoom  implements Serializable {
    public VoicePojoRoom() {
        this.isLocked =false;
    }
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isLocked() {
        return isLocked;
    }




    @ColumnInfo()
            @TypeConverters(Converter.class)
    Voice  voice;

    @TypeConverters(Converter.class)
    public Voice getVoice() {
        return voice;
    }

    @TypeConverters(Converter.class)
    public void setVoice(Voice voice) {
        this.voice = voice;
    }
    @Ignore
    public boolean getLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
    @ColumnInfo
    boolean isLocked;
}
