package com.craft.notify.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.craft.notify.R;
import com.craft.notify.activities.SettingsActivity;
import com.craft.notify.adapters.LanguagesAdapter;
import com.craft.notify.models.VoicePojo;
import com.craft.notify.viewmodel.MainViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
//import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
//import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
//import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

public class VoiceListFragment extends PreferenceFragmentCompat{

    private ListView listView;
    private TextToSpeech textToSpeech;
    private Voice selectedVoice ;
    MainViewModel viewModel;
    SettingsActivity settingsActivity;

    //        @Override
//    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
//        super.onPrepareDialogBuilder(builder);
//
//    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.list_voices,container,false);
        listView = v.findViewById(R.id.listViewVoices);
        viewModel  = new ViewModelProvider(this).get(MainViewModel.class);
        settingsActivity = (SettingsActivity) getActivity();
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


             //   Collections.addAll(voices1);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Set<Voice> voices1 =textToSpeech.getVoices();
                        final String[] name = new String[voices1.size()];
                        final ArrayList<Voice> voiceArrayList = new ArrayList<>();
                        int i=0;
                        ArrayList <VoicePojo> voicePojos = new ArrayList<>();
                        Stack<VoicePojo> voicePojoStack  = new Stack<>();
                        Stack<Voice> voiceStack = new Stack<>();
                        for(Voice v:voices1){

                            name[i] = v.getLocale().getDisplayName();
                            voiceArrayList.add(v);
                            VoicePojo voicePojo = new VoicePojo();
                            voicePojo.setVoice(v);
                            if(v.getLocale().getDisplayCountry().equalsIgnoreCase("India")){
                                voicePojoStack.push(voicePojo);
                                voiceStack.push(v);
                            }
                            voicePojos.add(voicePojo);
                            i++;
                        }
                        voicePojos.addAll(0,voicePojoStack);
                        voiceArrayList.addAll(0,voiceStack);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ArrayAdapter<String>arrayAdapter = new ArrayAdapter<>(getActivity().getApplication(),android.R.layout.simple_list_item_1,name);
                                LanguagesAdapter languagesAdapter = new LanguagesAdapter(getActivity().getApplicationContext(),R.layout.voice_list,voicePojos);
                                listView.setAdapter(languagesAdapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        selectedVoice = voiceArrayList.get(position);
                                        textToSpeech.setVoice(voiceArrayList.get(position));
                                        textToSpeech.speak("Hey There, I'm your notification announcer",TextToSpeech.QUEUE_FLUSH,null);
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
                                        try{
                                            settingsActivity.getFirebaseAnalytics().setUserProperty("pref_lang"+ UUID.randomUUID()
                                                    ,voiceArrayList.get(position).getName()+" "
                                                    +voiceArrayList.get(position).getLocale()+" "
                                                    +voiceArrayList.get(position).getName());
                                        }catch ( Exception e){
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        });

                    }


                });
              thread.start();





            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        List<VoicePojo> voiceArrayList = new ArrayList<>();
        VoicePojo voicePojoRoom = new VoicePojo();
        voicePojoRoom.setVoice(selectedVoice);
        voiceArrayList.add(voicePojoRoom);
        new Thread(new Runnable() {

            private int targetLang;

            @Override
            public void run() {
                if(selectedVoice == null){

                    return;
                }


                //translator
//                try{
//                    targetLang = FirebaseTranslateLanguage.languageForLanguageCode(selectedVoice.getLocale().getLanguage());
//                }catch (NullPointerException e){
//                    e.printStackTrace();
//                    Toast.makeText(getActivity().getApplicationContext(),"Something went wrong, please try again later.",Toast.LENGTH_SHORT).show();
//                    targetLang = FirebaseTranslateLanguage.EN;
//                }
//                FirebaseTranslatorOptions translatorOptions = new FirebaseTranslatorOptions.Builder()
//                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
//                        .setTargetLanguage(targetLang)
//                        .build();
//                FirebaseTranslator translator = FirebaseNaturalLanguage.getInstance()
//                        .getTranslator(translatorOptions);
//                FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().build();
//                translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Log.d("translator","model downlaoded");
//
//
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        e.printStackTrace();
//                        Log.d("translator","model downlaod failed");
//                    }
//
//
//
//                });
//                FirebaseModelManager modelManager = FirebaseModelManager.getInstance();
//                modelManager.getDownloadedModels(FirebaseTranslateRemoteModel.class)
//                        .addOnSuccessListener(firebaseTranslateRemoteModels -> {
//                            if(firebaseTranslateRemoteModels.size()>0){
//                                for(FirebaseTranslateRemoteModel model :firebaseTranslateRemoteModels){
//                                    if(!model.getLanguageCode().equals(selectedVoice.getLocale().getLanguage())){
//
//                                        modelManager.deleteDownloadedModel(model).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d("model","deleted");
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.d("model","deletion failed");
//
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        }).addOnFailureListener(e -> {
//                            Log.d("model","get downloaded failed");
//                        });


            }


        }).start();
        if(textToSpeech!=null)
        textToSpeech.shutdown();

//


    }
}

