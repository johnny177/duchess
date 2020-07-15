package com.nnoboa.duchess.controllers.alarm;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.data.AlarmContract;

import java.util.Calendar;

public class RingTonePlayingService extends Service {

    String TAG = "RingtoneService";

    public RingTonePlayingService() {
        super();
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        startForeground(1,new Notification());
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"In the Ringtone Service");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long _id = intent.getExtras().getLong("id");
        String courseId = intent.getExtras().getString("courseId");
        String courseName = intent.getExtras().getString("courseName");
        String courseTopic = intent.getExtras().getString("currentTopic");
        String courseNote = intent.getExtras().getString("currentNote");
        int isRepeating = intent.getExtras().getInt("isRepeating");
        long milliseconds = intent.getExtras().getLong("milli");

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
        notificationHelper.getManager().notify((int) _id,nb.build());

        startForeground((int) _id,nb.build());
        if(isRepeating == AlarmContract.ScheduleEntry.SCHEDULE_NOT_REPEATING && Calendar.getInstance().getTimeInMillis()>=milliseconds){
            ScheduleAlarmStarter.cancelAlarms(getApplicationContext(),_id);
        }

        Log.d("RingtonePlayingService ","Notification started & cancel "+_id);
        return super.onStartCommand(intent, flags, startId);
    }
}
