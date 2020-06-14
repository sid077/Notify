package com.example.notify.fragments;

import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.example.notify.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SleepFragment extends PreferenceDialogFragmentCompat implements TimePickerDialog.OnTimeSetListener {
    TimePickerDialog timePicker;
    EditText editTextTo,editTextFrom;
    Calendar calendar;
    private boolean isTo;
    private String timeTo;
    private String timeFrom;
    int hrTo,hrFrom,minTo ,minFrom;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep_time,container,false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


    }
    public static SleepFragment newInstance(Preference preference) {
        SleepFragment fragment = new SleepFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARG_KEY, preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        editTextFrom = view.findViewById(R.id.editTextFrom);
        editTextTo = view.findViewById(R.id.editTextTo);
        timeTo =    PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("timeTo","07:00 AM");
        timeFrom =  PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("timeFrom","10:00 PM");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
         calendar = Calendar.getInstance();
        try {
            Date dateFrom = simpleDateFormat.parse(timeFrom);
            Date dateTo = simpleDateFormat.parse(timeTo);
            calendar.setTime(dateFrom);
            hrFrom = calendar.get(Calendar.HOUR_OF_DAY);
            minFrom = calendar.get(Calendar.MINUTE);
            calendar.setTime(dateTo);
            hrTo = calendar.get(Calendar.HOUR_OF_DAY);
            minTo = calendar.get(Calendar.MINUTE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       // calendar = new GregorianCalendar();
        if(timeFrom!= null){
            editTextFrom.setText(timeFrom);

        }
        if(timeTo!= null){
            editTextTo.setText(timeTo);
        }
        timePicker = new TimePickerDialog(getContext(),this::onTimeSet, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
        editTextTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setTitle("Sleep until");
                if(hrTo!=0&&minTo!=0)
                timePicker.updateTime(hrTo,minTo);
                timePicker.show();
                isTo =true;
            }
        });
        editTextFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.setTitle("Sleep from");
                if(hrFrom!=0&&minFrom!=0)
                timePicker.updateTime(hrFrom,minFrom);

                timePicker.show();
                isTo =false;
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
        calendar.set(Calendar.MINUTE,minute);
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
        String alarmDate = simpleDateFormat.format(date);
       if(isTo){
           editTextTo.setText(alarmDate);
           timeTo = alarmDate;

       }
       else {
           editTextFrom.setText(alarmDate);
           timeFrom = alarmDate;
       }
       isTo =false;

    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("timeTo",timeTo).apply();
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).edit().putString("timeFrom",timeFrom).apply();



    }


}
