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

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public final class AlarmStarter {

    public static String ALARM_CATEGORY_SCHEDULE = "scheduleAlarm";
    static long milliseconds;
    public static String ALARM_CATEGORY = "alarmCategory";
    public static String ALARM_CATEGORY_REMINDER = "reminderAlarm";

    public static void init(Context context) {
        startScheduleAlarm(context);
        startReminderAlarm(context);
    }

    public static void startReminderAlarm(Context context) {

    }

    public static void startScheduleAlarm(Context context) {
        AlarmManager alarmManager;
        Intent myIntent;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        myIntent = new Intent(context, AlarmReceiver.class);
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
            Long currentID = cursor.getLong(_id);
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
            Uri
                    currentScheduleUri =
                    ContentUris.withAppendedId(AlarmContract.ScheduleEntry.CONTENT_URI, currentID);

//
            Log.d("ScheduleAlarmStarter", "Querying alarm.db" + currentID.getClass().getSimpleName() + " - " + currentCourseName + " - " + milliseconds);

            myIntent.setData(currentScheduleUri);
            myIntent.putExtra("id", currentID);
            myIntent.putExtra("courseID", currentCourseId);
            myIntent.putExtra("courseName", currentCourseName);
            myIntent.putExtra("currentTopic", currentTopic);
            myIntent.putExtra("currentNote", currentNote);
            myIntent.putExtra("currentStatus", currentDone);
            myIntent.putExtra("milli", milliseconds);
            myIntent.putExtra("currentRepeatStat", currentRepeat);
            myIntent.putExtra("currentScheduleStat", currentDone);
            myIntent.putExtra("currentRepeatInterval", currentInterval);
            myIntent.putExtra(ALARM_CATEGORY, ALARM_CATEGORY_SCHEDULE);

            PendingIntent
                    pendingIntent =
                    null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                pendingIntent =
                        PendingIntent.getBroadcast(context, Math.toIntExact(currentID), myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            if (currentDone == AlarmContract.ScheduleEntry.NOT_DONE) {
                switch (currentRepeat) {
                    case AlarmContract.ScheduleEntry.REPEAT_OFF:
                        alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
                        Log.d("ScheduleAlarmStarter", "Non repeating Alarm " + currentID);
                        break;

                    case AlarmContract.ScheduleEntry.REPEAT_ON:
                        switch (currentInterval) {
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_DAILY:
                                setupRepeating(1, context, milliseconds, currentID, pendingIntent, alarmManager, AlarmContract.ScheduleEntry.CONTENT_URI, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI,
                                        AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
                                Log.d("ScheduleAlarmStarter", "Daily repeating Alarm " + currentID);

                                break;
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_WEEKLY:
                                setupRepeating(7, context, milliseconds, currentID, pendingIntent, alarmManager, AlarmContract.ScheduleEntry.CONTENT_URI,
                                        AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
                                Log.d("ScheduleAlarmStarter", "Weekly repeating Alarm");

                                break;
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_MONTHLY:
                                setupRepeating(30, context, milliseconds, currentID, pendingIntent, alarmManager, AlarmContract.ScheduleEntry.CONTENT_URI,
                                        AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME, AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
                                Log.d("ScheduleAlarmStarter", "Monthly repeating Alarm");

                                break;
                        }
                        break;
                }
            }
            Util.scheduleJob(context);

        }
        cursor.close();
    }

    public static void cancelAlarms(Context context, long id) {
        Uri
                currentScheduleUri =
                ContentUris.withAppendedId(AlarmContract.ScheduleEntry.CONTENT_URI, id);
        ContentValues values = new ContentValues();
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE, AlarmContract.ScheduleEntry.DONE);

        AlarmManager alarmManager;
        Intent myIntent;
        myIntent = new Intent(context, AlarmReceiver.class);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent
                pendingIntent =
                PendingIntent.getBroadcast(context, (int) id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        context.getContentResolver().update(currentScheduleUri, values, null, null);
        Log.d("ScheduleAlarmStarter", "Cancel Alarm and update db with " + currentScheduleUri + " " + Calendar.getInstance().getTimeInMillis() + milliseconds);


    }

    private static void setupRepeating(int Interval, Context context, long milliseconds, long id, PendingIntent pendingIntent,
                                       AlarmManager alarmManager, Uri uri, String COLUMN_MILLI, String COLUMN_TIME, String COLUMN_DATE) {
        long timeNow = Calendar.getInstance().getTimeInMillis();
        long intervalNowAndDue = timeNow - milliseconds;
        if (intervalNowAndDue >= 0) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
            milliseconds = milliseconds + (60 * 60000 * 24 * Interval);
            Time time = new Time(milliseconds);
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String newTime = timeFormat.format(time);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String newDate = dateFormat.format(time);
            Log.d("Daily alarm ", milliseconds + " " + newTime + " " + newDate);
            ContentValues values = new ContentValues();
            values.put(COLUMN_MILLI, milliseconds);
            values.put(COLUMN_TIME, newTime);
            values.put(COLUMN_DATE, newDate);
            Uri currentUri = ContentUris.withAppendedId(uri, id);
            context.getContentResolver().update(currentUri, values, null, null);
        }
    }

}
