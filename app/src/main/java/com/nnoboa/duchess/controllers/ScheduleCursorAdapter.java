package com.nnoboa.duchess.controllers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.nnoboa.duchess.data.AlarmContract.ScheduleEntry;
import com.nnoboa.duchess.R;
import com.nnoboa.duchess.data.AlarmContract;
import com.nnoboa.duchess.R.drawable;

public class ScheduleCursorAdapter extends CursorAdapter {
    public ScheduleCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //find the respective fields to populate to inflated template
        TextView idTextView = view.findViewById(R.id.course_id);
        TextView scheduleName = view.findViewById(R.id.course_name);
        TextView scheduleTopic = view.findViewById(R.id.course_topic);
        TextView scheduleDate = view.findViewById(R.id.schedule_date);
        TextView  scheduleTime = view.findViewById(R.id.schedule_time);
        TextView scheduleInterval = view.findViewById(R.id.schedule_interval);
        ImageView isDoneImage = view.findViewById(R.id.schedule_is_done);


        //get the columnInddex from database
        int courseIDColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
        int courseNameColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
        int courseTopicColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
        int courseTimeColumnIndex  = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME);
        int courseDateColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
        int courseRepeatColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT);
        int courseIntervalColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
        int courseDoneColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE);

        String currentCourseId = cursor.getString(courseIDColumnIndex);
        String currentCourseName = cursor.getString(courseNameColumnIndex);
        String currentTopic = cursor.getString(courseTopicColumnIndex);
        String currentTime = cursor.getString(courseTimeColumnIndex);
        String currentDate = cursor.getString(courseDateColumnIndex);
        int currentRepeat = cursor.getInt(courseRepeatColumnIndex);
        int currentInterval = cursor.getInt(courseIntervalColumnIndex);
        int currentDone = cursor.getInt(courseDoneColumnIndex);

        //set appropriate image to match repeat state
        switch (currentDone){
            case ScheduleEntry.DONE:
                isDoneImage.setImageResource(drawable.checked_64);
                break;
            case ScheduleEntry.NOT_DONE:
                isDoneImage.setImageResource(R.drawable.ic_not_done);
                break;
        }

        switch(currentInterval){
            case ScheduleEntry.SCHEDULE_NOT_REPEATING:
                scheduleInterval.setText(R.string.not_repeating);
                break;
            case ScheduleEntry.SCHEDULE_REPEAT_DAILY:
                scheduleInterval.setText(R.string.daily);
                break;
            case ScheduleEntry.SCHEDULE_REPEAT_WEEKLY:
                scheduleInterval.setText(R.string.weekly);
                break;
            case ScheduleEntry.SCHEDULE_REPEAT_MONTHLY:
                scheduleInterval.setText(R.string.monthly);
                break;
        }

        switch (currentRepeat){

        }

        scheduleName.setText(currentCourseName);
        scheduleTime.setText(currentTime);
        scheduleDate.setText(currentDate);
        idTextView.setText(currentCourseId);
        scheduleTopic.setText(currentTopic);
    }
}
