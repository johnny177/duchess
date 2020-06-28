package com.nnoboa.duchess.controllers.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.data.AlarmContract;

public class ReminderCursorAdapter extends CursorAdapter {

    public ReminderCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_list, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor data) {
        TextView courseIdText = view.findViewById(R.id.reminder_course_id_text);
        TextView courseNameText = view.findViewById(R.id.reminder_course_name_text);
        TextView typeText = view.findViewById(R.id.reminder_type_text);
        TextView locText = view.findViewById(R.id.reminder_loc_text);
        TextView timeText = view.findViewById(R.id.reminder_time);
        TextView dateText = view.findViewById(R.id.reminder_date);
        TextView intervalText = view.findViewById(R.id.reminder_interval);
        TextView noteText = view.findViewById(R.id.reminder_note_text);
        ImageView doneImage = view.findViewById(R.id.reminder_done_image);

            /*
              get the column index
             */

        int courseIdColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_COURSE_ID);
        int courseNameColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_COURSE_NAME);
        int reminderTypeColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_TYPE);
        int reminderTimeColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_TIME);
        int reminderDateColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_DATE);
        int reminderLocColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_LOCATION);
        int reminderRepeatColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT);
        int reminderOnlineColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS);
        int reminderIntervalColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL);
        int reminderStatusColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_STATUS);
        int reminderNoteColumnIndex = data.getColumnIndexOrThrow(AlarmContract.ReminderEntry.COLUMN_REMINDER_NOTE);

        String courseId = data.getString(courseIdColumnIndex);
        String courseName = data.getString(courseNameColumnIndex);
        String courseNote = data.getString(reminderNoteColumnIndex);
        String reminderTime = data.getString(reminderTimeColumnIndex);
        String reminderDate = data.getString(reminderDateColumnIndex);
        String reminderLoc = data.getString(reminderLocColumnIndex);

        int reminderRepeatStat = data.getInt(reminderRepeatColumnIndex);
        int reminderRepeatInterval = data.getInt(reminderIntervalColumnIndex);
        int reminderStatus = data.getInt(reminderStatusColumnIndex);
        int reminderType = data.getInt(reminderTypeColumnIndex);
        int reminderOnlineStatus = data.getInt(reminderOnlineColumnIndex);

        courseIdText.setText(courseId);
        courseNameText.setText(courseName);
        noteText.setText(courseNote);
        timeText.setText(reminderTime);
        dateText.setText(reminderDate);

        switch (reminderRepeatInterval){
            case AlarmContract.ReminderEntry.ONCE:
                intervalText.setText(R.string.once);
                break;
            case AlarmContract.ReminderEntry.INTERVAL_DAILY:
                intervalText.setText(R.string.daily);
                break;
            case AlarmContract.ReminderEntry.INTERVAL_3_DAYS:
                intervalText.setText(R.string.in_every_3_days);
                break;
            case AlarmContract.ReminderEntry.INTERVAL_WEEKLY:
                intervalText.setText(R.string.weekly);
                break;
        }

        switch (reminderOnlineStatus){
            case AlarmContract.ReminderEntry.REMINDER_IS_ONLINE:
                locText.setText(R.string.tap_to_open_url);
                break;
            case AlarmContract.ReminderEntry.REMINDER_IS_OFFLINE:
                locText.setText(reminderLoc);
                break;
        }

        switch (reminderType){
            case AlarmContract.ReminderEntry.REMINDER_TYPE_LECTURES:
                typeText.setText(R.string.lectures);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_ASSIGNMENT:
                typeText.setText(R.string.assignment);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_IA:
                typeText.setText(R.string.interim_assessment);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_EXAMS:
                typeText.setText(R.string.exam);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_PROJECT:
                typeText.setText(R.string.project);
                break;
            case AlarmContract.ReminderEntry.REMINDER_TYPE_QUIZ:
                typeText.setText(R.string.quiz);
                break;
            default:
                typeText.setText(R.string.other);
                break;
        }

        switch (reminderStatus){
            case AlarmContract.ReminderEntry.STATUS_IS_DONE:
                doneImage.setImageResource(R.drawable.checked_64);
                break;
            case AlarmContract.ReminderEntry.STATUS_IS_NOT_DONE:
                doneImage.setVisibility(View.GONE);
        }



    }
}
