package com.nnoboa.duchess.controllers.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.nnoboa.duchess.data.AlarmContract;

import java.util.Calendar;

public final class ScheduleAlarmStarter {
    static long milliseconds;

    public static void startAlarm(Context context) {
        AlarmManager alarmManager;
        Intent myIntent;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        myIntent = new Intent(context, AlarmReceiver.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ContentValues values = new ContentValues();
        String[] projection = {
                AlarmContract.ScheduleEntry._ID,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE};


        Cursor
                cursor =
                context.getContentResolver().query(AlarmContract.ScheduleEntry.CONTENT_URI, projection, null, null, null);

        //get the columnIndex from database
        assert cursor != null;
        int _id = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry._ID);
        int
                courseIDColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
        int
                courseNameColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
        int
                courseTopicColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
        int
                courseTimeColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME);
        int
                courseDateColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
        int
                courseRepeatColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT);
        int
                courseIntervalColumnIndex =
                cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
        int
                courseDoneColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE);
        int
                courseNoteColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE);
        int
                millisecondsColumnIndex =
                cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI);


        while (cursor.moveToNext()) {
            long currentID = Long.parseLong(String.valueOf(cursor.getInt(_id)));
            String currentCourseId = cursor.getString(courseIDColumnIndex);
            String currentCourseName = cursor.getString(courseNameColumnIndex);
            String currentTopic = cursor.getString(courseTopicColumnIndex);
//            String currentTime = cursor.getString(courseTimeColumnIndex);
//            String currentDate = cursor.getString(courseDateColumnIndex);
            int currentRepeat = cursor.getInt(courseRepeatColumnIndex);
            int currentInterval = cursor.getInt(courseIntervalColumnIndex);
            int currentDone = cursor.getInt(courseDoneColumnIndex);
            String currentNote = cursor.getString(courseNoteColumnIndex);
            milliseconds = cursor.getLong(millisecondsColumnIndex);
//
            Log.d("ScheduleAlarmStarter", "Querying alarm.db" + currentID + " - " + currentCourseName + " - " + milliseconds);

            myIntent.putExtra("id",currentID);
            myIntent.putExtra("courseID",currentCourseId);
            myIntent.putExtra("courseName",currentCourseName);
            myIntent.putExtra("currentTopic",currentTopic);
            myIntent.putExtra("currentNote",currentNote);
            myIntent.putExtra("currentStatus",currentDone);
            myIntent.putExtra("milli",milliseconds);
            myIntent.putExtra("currentRepeatStat",currentRepeat);
            myIntent.putExtra("currentScheduleStat",currentDone);
            myIntent.putExtra("currentRepeatInterval",currentInterval);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) currentID,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            if(currentDone == AlarmContract.ScheduleEntry.NOT_DONE) {
                switch (currentRepeat) {
                    case AlarmContract.ScheduleEntry.REPEAT_OFF:
                        alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
                        Log.d("ScheduleAlarmStarter","Non repeating Alarm "+currentID);
                        break;

                    case AlarmContract.ScheduleEntry.REPEAT_ON:
                        switch (currentInterval) {
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_DAILY:
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliseconds, AlarmManager.INTERVAL_DAY, pendingIntent);
                                Log.d("ScheduleAlarmStarter","Daily repeating Alarm");

                                break;
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_WEEKLY:
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliseconds, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                                Log.d("ScheduleAlarmStarter","Weekly repeating Alarm");

                                break;
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_MONTHLY:
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliseconds, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
                                Log.d("ScheduleAlarmStarter","Monthly repeating Alarm");

                                break;
                        }
                        break;
                }
            }
            Util.scheduleJob(context);

        }
        cursor.close();
    }

    public static void cancelAlarms(Context context, long id){
        Uri currentScheduleUri = ContentUris.withAppendedId(AlarmContract.ScheduleEntry.CONTENT_URI,id);
        ContentValues values = new ContentValues();
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE, AlarmContract.ScheduleEntry.DONE);

        AlarmManager alarmManager;
        Intent myIntent;
        myIntent = new Intent(context, AlarmReceiver.class);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id,myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        context.getContentResolver().update(currentScheduleUri,values,null,null);
        Log.d("ScheduleAlarmStarter","Cancel Alarm and update db with "+currentScheduleUri+Calendar.getInstance().getTimeInMillis()+milliseconds);


    }

}
