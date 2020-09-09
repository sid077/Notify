package com.craft.notify.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import com.craft.notify.NotificationServiceList;

public class NotificationJobService extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), NotificationServiceList.class);
        service.putExtra("restarted",true);
        getApplicationContext().startService(service);
        JobUtil.scheduleJob(getApplicationContext());

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


}
