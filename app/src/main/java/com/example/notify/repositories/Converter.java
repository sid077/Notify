package com.example.notify.repositories;

import android.speech.tts.Voice;

import androidx.room.TypeConverter;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    @TypeConverter
    public static String fromVoice(Voice value){
        if(value==null){
            return null;
        }

        Type listType = new TypeToken<Voice>(){}.getType();
        Gson gson = new Gson();
        String cvt = gson.toJson(value,Voice.class);


        return cvt;
    }
    @TypeConverter
    public static Voice toVoice(String value){
        if(value==null){
            return null ;
        }
        Gson gson = new Gson();
       Type type = new TypeToken<Voice>(){}.getType();
       Voice  voiceList = gson.fromJson(value,Voice.class);
       return  voiceList;
    }
}
