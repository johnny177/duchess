package com.nnoboa.duchess.controllers.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.activities.editors.ScheduleEditorActivity;
import com.nnoboa.duchess.data.AlarmContract;

import java.util.Calendar;

public class RingTonePlayingService extends Service {

    String TAG = "RingtoneService";

    public RingTonePlayingService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CharSequence name = "Check if there's uncheck Alarm";
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("JobAlarmServiceNotification", name, NotificationManager.IMPORTANCE_LOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }

        Notification
                notification = new NotificationCompat.Builder(this, "JobAlarmServiceNotification")
                .setContentTitle("ALARM")
                .setContentText("Check if there is past due alarms").build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "In the Ringtone Service");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri currentUri = intent.getData();
        long _id = intent.getExtras().getLong("id");
        String courseId = intent.getExtras().getString("courseId");
        String courseName = intent.getExtras().getString("courseName");
        String courseTopic = intent.getExtras().getString("currentTopic");
        String courseNote = intent.getExtras().getString("currentNote");
        int isRepeating = intent.getExtras().getInt("isRepeating");
        long milliseconds = intent.getExtras().getLong("milli");
        String alarmCategory = intent.getExtras().getString(AlarmStarter.ALARM_CATEGORY);

        Intent contentIntent = new Intent(this, ScheduleEditorActivity.class);
        contentIntent.setData(currentUri);
        PendingIntent
                contentPendingIntent =
                PendingIntent.getActivity(this, (int) _id, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();

        if (!TextUtils.isEmpty(courseName)) {
            nb.setContentTitle(courseId + " - " + courseName);
        } else {
            nb.setContentTitle(courseId);
        }

        nb.setSmallIcon(R.drawable.add_reminder);

        if (!TextUtils.isEmpty(courseNote)) {
            nb.setContentText(courseTopic + "\n" + courseNote);
        } else {
            nb.setContentText(courseTopic);
        }

        nb.setTicker(courseId);
        nb.setContentIntent(contentPendingIntent);
        notificationHelper.getManager().notify((int) _id, nb.build());

//        startForeground((int) _id,nb.build());
        if (isRepeating == AlarmContract.ScheduleEntry.REPEAT_OFF && Calendar.getInstance().getTimeInMillis() >= milliseconds) {
            AlarmStarter.cancelAlarms(getApplicationContext(), _id);
            Log.d("Cancelling Alarm ", "Current time " + Calendar.getInstance().getTimeInMillis() + " - " + milliseconds + " isRepeating " + isRepeating);

        }


        Log.d("RingtonePlayingService ", "Notification started & cancel " + _id);
        return super.onStartCommand(intent, flags, startId);
    }
}
