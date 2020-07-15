package com.nnoboa.duchess.controllers.alarm;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.nnoboa.duchess.data.AlarmContract;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class AlarmJobService extends JobService {

    private static final String TAG = "AlarmJobService";
    private static AlarmManager alarmManager;

    private static ContentValues values = new ContentValues();
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG,"Job Started");
//        ScheduleAlarmStarter.startAlarm(getApplicationContext());
        alarmManager = (AlarmManager) AlarmJobService.this.getSystemService(Context.ALARM_SERVICE);
        startAlarm(params);

        return true;
    }

    private void startAlarm(final JobParameters params) {
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
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE };


        Cursor cursor = getContentResolver().query(AlarmContract.ScheduleEntry.CONTENT_URI,projection,null,null,null);

        //get the columnIndex from database
        int _id = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry._ID);
        int courseIDColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
        int courseNameColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
        int courseTopicColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
        int courseTimeColumnIndex  = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME);
        int courseDateColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
        int courseRepeatColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT);
        int courseIntervalColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
        int courseDoneColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE);
        int courseNoteColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE);
        int millisecondsColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_MILLI);


        while (cursor.moveToNext()){
            int currentID = cursor.getInt(_id);
            String currentCourseId = cursor.getString(courseIDColumnIndex);
            String currentCourseName = cursor.getString(courseNameColumnIndex);
            String currentTopic = cursor.getString(courseTopicColumnIndex);
            String currentTime = cursor.getString(courseTimeColumnIndex);
            String currentDate = cursor.getString(courseDateColumnIndex);
            int currentRepeat = cursor.getInt(courseRepeatColumnIndex);
            int currentInterval = cursor.getInt(courseIntervalColumnIndex);
            int currentDone = cursor.getInt(courseDoneColumnIndex);
            String currentNote = cursor.getString(courseNoteColumnIndex);
            long milliseconds = cursor.getLong(millisecondsColumnIndex);

            Log.d("JobScheduler","Querying alarm.db"+currentID+" - "+currentCourseName+" - "+milliseconds);
            Intent intent = new Intent(AlarmJobService.this,AlarmReceiver.class);
            intent.putExtra("id",currentID);
            intent.putExtra("courseID",currentCourseId);
            intent.putExtra("courseName",currentCourseName);
            intent.putExtra("currentTopic",currentTopic);
            intent.putExtra("currentNote",currentNote);
            intent.putExtra("currentStatus",currentDone);
            intent.putExtra("fromJobService","true");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmJobService.this,_id,intent,PendingIntent.FLAG_UPDATE_CURRENT);


            if(currentDone == AlarmContract.ScheduleEntry.NOT_DONE) {
                switch (currentRepeat) {
                    case AlarmContract.ScheduleEntry.REPEAT_OFF:
                        alarmManager.set(AlarmManager.RTC_WAKEUP, milliseconds, pendingIntent);
                        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE, AlarmContract.ScheduleEntry.DONE);
                        String selection = _id+"=?";
                        getContentResolver().update(AlarmContract.ScheduleEntry.CONTENT_URI,values,selection,null);

                    case AlarmContract.ScheduleEntry.REPEAT_ON:
                        switch (currentInterval) {
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_DAILY:
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliseconds, AlarmManager.INTERVAL_DAY, pendingIntent);
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_WEEKLY:
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliseconds, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                            case AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_MONTHLY:
                                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, milliseconds, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
                        }
                }
            }

            Util.scheduleJob(AlarmJobService.this);

        }

        cursor.close();

        jobFinished(params,true);

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        return true;
    }

}
