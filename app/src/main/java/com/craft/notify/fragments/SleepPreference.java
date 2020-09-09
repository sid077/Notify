package com.craft.notify.fragments;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.craft.notify.R;

public class SleepPreference extends DialogPreference {
    public SleepPreference(Context context) {
        super(context);
    }
    public SleepPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }public SleepPreference(Context context, AttributeSet attrs,
                           int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }public SleepPreference(Context context, AttributeSet attrs,
                           int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.fragment_sleep_time;
    }
}
