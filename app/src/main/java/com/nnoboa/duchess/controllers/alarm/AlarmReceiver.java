package com.nnoboa.duchess.controllers.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.Objects;

public class AlarmReceiver extends BroadcastReceiver {
    Intent service;
    @Override
    public void onReceive(Context context, Intent intent) {
        Util.scheduleJob(context);
        long id = Objects.requireNonNull(intent.getExtras()).getLong("id");

        Uri currentUri = intent.getData();

        String courseId = intent.getExtras().getString("courseID");

        String courseName = intent.getExtras().getString("courseName");

        String courseTopic = intent.getExtras().getString("currentTopic");

        String courseNote = intent.getExtras().getString("currentNote");

        String alarmCategory = intent.getExtras().getString(AlarmStarter.ALARM_CATEGORY);

//        int state = intent.getExtras().getInt("currentState");
        int isRepeating = intent.getExtras().getInt("currentRepeatStat");
        long milliseconds = intent.getExtras().getLong("milli");
        String jobService = intent.getExtras().getString("fromJobService");

        service = new Intent(context, RingTonePlayingService.class);

        service.putExtra("id", id);
        service.putExtra("courseId", courseId);
        service.putExtra("courseName", courseName);
        service.putExtra("currentTopic", courseTopic);
        service.putExtra("currentNote", courseNote);
        service.putExtra("fromJobService", jobService);
        service.putExtra("isRepeating", isRepeating);
        service.putExtra("milli", milliseconds);
        service.putExtra("alarmCategory", alarmCategory);
        service.setData(currentUri);

        Log.d("Receiver Received ", "" + id + courseId + courseName + courseTopic + courseNote);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service);
        } else {
            context.startService(service);
        }



//        context.startService(service);
    }
}
