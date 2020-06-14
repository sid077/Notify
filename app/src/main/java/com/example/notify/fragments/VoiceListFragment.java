package com.example.notify.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.DialogPreference;
import androidx.preference.ListPreference;
import androidx.preference.ListPreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.notify.R;
import com.example.notify.adapters.LanguagesAdapter;
import com.example.notify.models.VoicePojo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class VoiceListFragment extends PreferenceFragmentCompat{

    private ListView listView;
    private TextToSpeech textToSpeech;


//        @Override
//    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
//        super.onPrepareDialogBuilder(builder);
//
//    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.list_voices,container,false);
        listView = v.findViewById(R.id.listViewVoices);
        return v;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    @Override
    public void onStart() {
        super.onStart();
        textToSpeech = new TextToSpeech(getActivity().getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                Set<Voice> voices1 =textToSpeech.getVoices();
                final String name []= new String[voices1.size()];
                final ArrayList<Voice> voiceArrayList = new ArrayList<>();
                int i=0;
                ArrayList <VoicePojo> voicePojos = new ArrayList<>();
             //   Collections.addAll(voices1);
               for(Voice v:voices1){
                   name[i] = v.getLocale().getDisplayName();
                   voiceArrayList.add(v);
                   VoicePojo voicePojo = new VoicePojo();
                   voicePojo.setVoice(v);
                   voicePojos.add(voicePojo);
                   i++;
               }


                ArrayAdapter<String>arrayAdapter = new ArrayAdapter<>(getActivity().getApplication(),android.R.layout.simple_list_item_1,name);
                LanguagesAdapter languagesAdapter = new LanguagesAdapter(getActivity().getApplicationContext(),R.layout.voice_list,voicePojos);
                listView.setAdapter(languagesAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        textToSpeech.setVoice(voiceArrayList.get(position));
                        textToSpeech.speak("Hey There, I'm your notification announcer",TextToSpeech.QUEUE_ADD,null);
                        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("announcerName",voiceArrayList.get(position).getName()).apply();
                        int x=0;
                        ImageView imageViewLock =view.findViewById(R.id.imageViewLock);
                        voicePojos.get(position).setLocked(!voicePojos.get(position).getLocked());
                        if(voicePojos.get(position).getLocked()){
                            imageViewLock.setImageResource(R.drawable.ic_lock_black_24dp);
                        }
                        else {
                            imageViewLock.setImageResource(R.drawable.ic_lock_open_black_24dp);
                        }

                    }
                });


            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(textToSpeech!=null)
        textToSpeech.shutdown();
    }
}

