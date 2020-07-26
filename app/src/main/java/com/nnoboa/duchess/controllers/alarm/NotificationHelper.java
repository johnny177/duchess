package com.nnoboa.duchess.controllers.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

public class NotificationHelper extends ContextWrapper {

    NotificationManager notificationManager;

    public static final String ChannelID = "ChannelId";
    public static final String ChannelName = "Channel Name";

    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    private void createChannel() {

        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(ChannelID, ChannelName, importance);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String ringtone = preferences.getString("general_ringtone",null);
            Log.d("Helper","Notification "+ringtone);
            AudioAttributes builder = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            notificationChannel.setSound(Uri.parse(ringtone),builder );
            getManager().createNotificationChannel(notificationChannel);
        }

    }

    public NotificationManager getManager(){
        if(notificationManager == null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public NotificationCompat.Builder getChannelNotification(){
        return new NotificationCompat.Builder(getApplicationContext(),ChannelID)
                .setAutoCancel(false);
    }


}
