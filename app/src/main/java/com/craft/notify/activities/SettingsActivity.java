package com.craft.notify.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.craft.notify.R;
import com.craft.notify.fragments.SleepFragment;
import com.craft.notify.fragments.SleepPreference;
import com.craft.notify.fragments.VoiceListFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;


public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;

    public FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        toolbar = findViewById(R.id.toolbarSettings);
        toolbar.setTitleTextColor(getColor(R.color.whiteColor));
        setSupportActionBar(toolbar);
        toolbar.setTitle("Settings");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);


    }



    public static class SettingsFragment extends PreferenceFragmentCompat {
        TextToSpeech textToSpeech;
        @Override
        public void onDestroy() {
            if(textToSpeech!=null){
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
            super.onDestroy();
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            DialogFragment dialogFragment = null;
            if (preference instanceof SleepPreference) {
                // Create a new instance of TimePreferenceDialogFragment with the key of the related
                // Preference
                dialogFragment = SleepFragment
                        .newInstance(preference);
            }

            // If it was one of our cutom Preferences, show its dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(this.getFragmentManager(),
                        "android.support.v7.preference" +
                                ".PreferenceFragment.DIALOG");
            }
            // Could not be handled here. Try with the super method.
            else {
                super.onDisplayPreferenceDialog(preference);
            }

        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
           SwitchPreferenceCompat screenOffPref = findPreference("isScreenOffEnabled");
            screenOffPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        ((SettingsActivity) getActivity()).firebaseAnalytics.setUserProperty("pref_screen_Off", String.valueOf(newValue));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    return  true;
                }
            });
            SwitchPreferenceCompat selectedApps = findPreference("isSelectedAppsEnabled");
            selectedApps.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    try {
                        ((SettingsActivity) getActivity()).firebaseAnalytics.setUserProperty("pref_selected_apps", String.valueOf(true));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    return  true;
                }
            });
            Preference voicesPrefrence = this.findPreference("announcer");
            voicesPrefrence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.settings,new VoiceListFragment()).addToBackStack("VoiceList").commit();

                    return true;
                }
            });
            Preference contactPref = this.findPreference("contact");
            contactPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL,"siddhant.d777@gmail.com");
                    intent.setData(Uri.parse("mailto:"));
                    intent.setType("text/pain");
                    startActivity(intent);
                    return false;
                }
            });
            EditTextPreference reviewPref = this.findPreference("comments");


            reviewPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference();
                    reference.child("reviews").child(UUID.randomUUID().toString()).setValue(String.valueOf(newValue));
                  //  FirebaseDatabase.getInstance().getReference().child("reviews").child(UUID.randomUUID().toString()).child(String.valueOf(newValue));
                    try {
                        ((SettingsActivity) getActivity()).firebaseAnalytics.setUserProperty("review", String.valueOf(newValue));
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    return true;
                }
            });


            Preference seekBar = this.findPreference("speed");
            seekBar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                  //  PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putInt("speed",(Integer)newValue);
                    textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                        @Override
                        public void onInit(int status) {
                            float val = ((Integer) newValue).floatValue();
                            val = (val/100)*2;

                            textToSpeech.setSpeechRate(val);
                            textToSpeech.speak("I will Speak with this speed",TextToSpeech.QUEUE_ADD,null);
                            try {
                                ((SettingsActivity) getActivity()).firebaseAnalytics.setUserProperty("pref_speed", String.valueOf(val));
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                    return true;
                }
            });


//            sleepTimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    DialogFragment fragment = SleepFragment.newInstance(preference);
//                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.settings,fragment).addToBackStack("SleepTimeFrag").commit();
//
//                    return true;
//                }
//            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        else if(item.getItemId()==R.id.menuBtnHelp){
            Intent intent = new Intent(getApplicationContext(),SliderActivity.class);
            intent.putExtra("isFromHelp",true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_appbar_menu_settings,menu);

       return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onDestroy() {


        super.onDestroy();

    }
}