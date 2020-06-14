package com.example.notify.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.strictmode.ServiceConnectionLeakedViolation;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.notify.R;
import com.example.notify.fragments.SleepFragment;
import com.example.notify.fragments.SleepPreference;
import com.example.notify.fragments.VoiceListFragment;

public class SettingsActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        toolbar = findViewById(R.id.toolbarSettings);
        toolbar.setTitleTextColor(getColor(R.color.whiteColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }



    public static class SettingsFragment extends PreferenceFragmentCompat {

        private TextToSpeech textToSpeech;

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
            Preference voicesPrefrence = this.findPreference("announcer");
            voicesPrefrence.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.settings,new VoiceListFragment()).addToBackStack("VoiceList").commit();

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