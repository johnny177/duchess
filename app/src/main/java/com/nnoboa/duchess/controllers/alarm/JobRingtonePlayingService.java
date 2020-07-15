package com.nnoboa.duchess.controllers.alarm;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import com.nnoboa.duchess.R;

public class JobRingtonePlayingService extends JobIntentService {

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        int _id = intent.getExtras().getInt("id");
        String courseId = intent.getExtras().getString("courseId");
        String courseName = intent.getExtras().getString("courseName");
        String courseTopic = intent.getExtras().getString("courseTopic");
        String courseNote = intent.getExtras().getString("courseNote");

        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();

        if(!TextUtils.isEmpty(courseName)){
            nb.setContentTitle(courseId+" - "+ courseName);
        }else {
            nb.setContentTitle(courseId);
        }

        nb.setContentText(courseTopic+"\n"+courseNote);
        nb.setSmallIcon(R.drawable.add_schedule64);
        nb.setTicker(courseId);
        notificationHelper.getManager().notify(_id,nb.build());

        Log.d("JobRingtoneService ","Notification started");
    }
}
