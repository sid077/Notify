package com.example.notify.models;

import android.speech.tts.Voice;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface VoiceDao {
    @Query("select * from VoicePojoRoom")
    List<VoicePojoRoom> getVoices();

    @Insert(onConflict = REPLACE )
    void insertFirstVoice(VoicePojoRoom voicePojos);
    @Query("delete from VoicePojoRoom")
    void deleteAll();


}
