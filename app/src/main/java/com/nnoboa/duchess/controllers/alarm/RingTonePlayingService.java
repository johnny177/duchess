package com.nnoboa.duchess.controllers.alarm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.activities.editors.ReminderEditorActivity;
import com.nnoboa.duchess.activities.editors.ScheduleEditorActivity;
import com.nnoboa.duchess.data.AlarmContract;

import java.util.Calendar;
import java.util.Objects;

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
        long _id = Objects.requireNonNull(intent.getExtras()).getLong("id");
        String courseId = intent.getExtras().getString("courseId");
        String courseName = intent.getExtras().getString("courseName");
        String courseTopic = intent.getExtras().getString("currentTopic");
        String courseNote = intent.getExtras().getString("currentNote");
        int isRepeating = intent.getExtras().getInt("isRepeating");
        long milliseconds = intent.getExtras().getLong("milli");
        String alarmCategory = intent.getExtras().getString(AlarmStarter.ALARM_CATEGORY);
        long reminderId = intent.getExtras().getLong("reminderId");
        String reminderCourseId = intent.getExtras().getString("reminderCourseId");
        String reminderCourseName = intent.getExtras().getString("reminderCourseName");
        String reminderType = intent.getExtras().getString("reminderType");
        String reminderNote = intent.getExtras().getString("reminderNote");
        long reminderMilli = intent.getExtras().getLong("reminderMilli");
        String reminderLoc = intent.getExtras().getString("reminderLoc");
        int reminderOnlineStatus = intent.getExtras().getInt("reminderOnlineStatus");
        int repeatStatus = intent.getExtras().getInt("repeatStatus");

        NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Uri ringtone = Uri.parse(preferences.getString("general_ringtone", null));
        Log.d("RingtonePlayingService", "Notification " + ringtone);
        nb.setSound(ringtone);
//        MediaPlayer ringTone = MediaPlayer.create(getApplicationContext(),ringtone);
//        ringTone.start();
        AlarmRingTone.playAudio(getApplicationContext(), ringtone);

        assert alarmCategory != null;
        if (alarmCategory.equals(AlarmStarter.ALARM_CATEGORY_REMINDER)) {
            Intent contentIntent = new Intent(this, ReminderEditorActivity.class);
            Uri
                    uri =
                    ContentUris.withAppendedId(AlarmContract.ReminderEntry.CONTENT_URI, reminderId);
            contentIntent.setData(uri);
            PendingIntent
                    contentPendingIntent =
                    PendingIntent.getActivity(this, (int) reminderId, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (!TextUtils.isEmpty(reminderCourseName)) {
                nb.setContentTitle(reminderCourseId + " - " + reminderCourseName);
            } else {
                nb.setContentTitle(reminderCourseId);
            }
            nb.setSmallIcon(R.drawable.add_reminder);

            if (!TextUtils.isEmpty(courseNote)) {
                nb.setContentText(reminderType + "\n\n" + reminderLoc + "\n\n" + reminderNote);
            } else {
                nb.setContentText(reminderType + "\n\n" + reminderLoc);
            }

            nb.setTicker(reminderCourseId);
            nb.setContentIntent(contentPendingIntent);
            if (reminderOnlineStatus == AlarmContract.ReminderEntry.REMINDER_IS_ONLINE) {
                assert reminderLoc != null;
                Intent browserIntent = openLink(reminderLoc);
                PendingIntent
                        pendingIntent =
                        PendingIntent.getActivity(this, (int) reminderId, browserIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                nb.addAction(R.drawable.ic_baseline_web_24, getString(R.string.open_site), pendingIntent);
            }
            notificationHelper.getManager().notify((int) reminderId, nb.build());

            if (repeatStatus == AlarmContract.ReminderEntry.REMINDER_IS_NOT_REPEATING && Calendar.getInstance().getTimeInMillis() >= reminderMilli) {
                AlarmStarter.cancelAlarms(getApplicationContext(), reminderId, AlarmContract.ReminderEntry
                        .CONTENT_URI, AlarmContract.ReminderEntry.COLUMN_REMINDER_STATUS, AlarmContract.ReminderEntry.STATUS_IS_DONE);
                Log.d("Cancelling Alarm ", "Current time " + Calendar.getInstance().getTimeInMillis() + " - " + reminderMilli + " Repeat Status " + repeatStatus);
            }
        } else if (alarmCategory.equals(AlarmStarter.ALARM_CATEGORY_SCHEDULE)) {
            Intent contentIntent = new Intent(this, ScheduleEditorActivity.class);
            contentIntent.setData(currentUri);
            PendingIntent
                    contentPendingIntent =
                    PendingIntent.getActivity(this, (int) _id, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);


            if (!TextUtils.isEmpty(courseName)) {
                nb.setContentTitle(courseId + " - " + courseName);
            } else {
                nb.setContentTitle(courseId);
            }

            nb.setSmallIcon(R.drawable.add_schedule96);

            if (!TextUtils.isEmpty(courseNote)) {
                nb.setContentText(courseTopic + "\n" + courseNote);
            } else {
                nb.setContentText(courseTopic);
            }

            nb.setTicker(courseId);
            nb.setContentIntent(contentPendingIntent);
            nb.setSound(ringtone, AudioManager.STREAM_ALARM);
            Notification notification = nb.build();
            notification.sound = ringtone;
            notificationHelper.getManager().notify((int) _id, notification);

//        startForeground((int) _id,nb.build());
            if (isRepeating == AlarmContract.ScheduleEntry.REPEAT_OFF && Calendar.getInstance().getTimeInMillis() >= milliseconds) {
                AlarmStarter.cancelAlarms(getApplicationContext(), _id, AlarmContract.ScheduleEntry.CONTENT_URI, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE, AlarmContract.ScheduleEntry.DONE);
                Log.d("Cancelling Alarm ", "Current time " + Calendar.getInstance().getTimeInMillis() + " - " + milliseconds + " isRepeating " + isRepeating);
            }

        }
        Log.d("RingtonePlayingService ", "Notification started & cancel " + _id);
        return super.onStartCommand(intent, flags, startId);
    }

    public Intent openLink(String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "http://" + url;
        }
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }
}
