package com.nnoboa.duchess.activities.editors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.data.AlarmContract;

import java.util.Calendar;
import java.util.Objects;

import static com.nnoboa.duchess.data.AlarmContract.*;

public class ScheduleEditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    EditText nameEdit, idEdit, topicEdit, timeEdit, dateEdit, noteEdit;
    Spinner repeatSpinner;
    CheckBox repeatCheck, doneCheck;

    private int SCHEDULE_LOADER_ID = 0;

    private boolean mScheduleChanged = false;

    private int interval;
    private int doneWithSchedule;
    private int repeatSchedule;

    android.app.LoaderManager loaderManager;
    Uri currentScheduleUri;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mScheduleChanged = true;
            return false;
        }
    };

    String courseTime;

    String courseDate ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_editor);

        Intent intent = getIntent();

        currentScheduleUri = intent.getData();

        loaderManager = getLoaderManager();
        findViews();

        courseDate = DateDialog().trim();

        courseTime = TimeDialog().trim();
        if (currentScheduleUri == null) {
            getSupportActionBar().setTitle(" Add a schedule");
            doneCheck.setEnabled(false);
        } else {
            getSupportActionBar().setTitle("Edit  schedule");
            loaderManager.initLoader(SCHEDULE_LOADER_ID, null, this);

        }

        nameEdit.setOnTouchListener(touchListener);
        idEdit.setOnTouchListener(touchListener);
        topicEdit.setOnTouchListener(touchListener);
        dateEdit.setOnTouchListener(touchListener);
        timeEdit.setOnTouchListener(touchListener);
        repeatCheck.setOnTouchListener(touchListener);
        doneCheck.setOnTouchListener(touchListener);
        repeatSpinner.setOnTouchListener(touchListener);
        setupSpinner();
        setupCheckers();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * get data from the views and add it to the db
     */

    private void saveSchedule() {

        String courseID = idEdit.getText().toString().trim().toUpperCase();

        String courseName = nameEdit.getText().toString().trim();

        String courseTopic = topicEdit.getText().toString().trim();

        String courseNote = noteEdit.getText().toString().trim();

        String courseTime = TimeDialog().trim();

        String courseDate = DateDialog().trim();





        setupCheckers();

//        int repeat = repeatSchedule;
//
//        int done = doneWithSchedule;
//
//        int repeatingInterval = interval;

        ContentValues contentValues = new ContentValues();
        if(TextUtils.isEmpty(courseID) && TextUtils.isEmpty(courseName)
        && TextUtils.isEmpty(courseTopic) && TextUtils.isEmpty(courseTime)
                && TextUtils.isEmpty(courseDate)){
            idEdit.setError("Schedule requires course ID");
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME, "Unknown");
            topicEdit.setError("Please Add The Topic to Study");
            timeEdit.setError("Please add a Time for the schedule ");
            dateEdit.setError("Please add date for the schedule");
        }else {
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID, courseID);
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME, courseName);
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_TOPIC, courseTopic);
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_TIME, courseTime);
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_DATE, courseDate);
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_NOTE, courseNote);
        }

        if(repeatSchedule == ScheduleEntry.REPEAT_OFF){
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_INTERVAL,ScheduleEntry.SCHEDULE_NOT_REPEATING);
        }else{
            contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_INTERVAL, interval);
        }

        if (currentScheduleUri == null &&
                TextUtils.isEmpty(courseDate) &&
                TextUtils.isEmpty(courseName) &&
                TextUtils.isEmpty(courseID) &&
                TextUtils.isEmpty(courseTopic) &&
                repeatSchedule == ScheduleEntry.REPEAT_OFF &&
                interval == ScheduleEntry.SCHEDULE_NOT_REPEATING &&
                doneWithSchedule == ScheduleEntry.NOT_DONE) {
            return;
        }

        contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_REPEAT, repeatSchedule);

        contentValues.put(ScheduleEntry.COLUMN_SCHEDULE_DONE, doneWithSchedule);


        if(currentScheduleUri == null){
            try{
                Uri newRowId = getContentResolver().insert(ScheduleEntry.CONTENT_URI,contentValues);
                Log.d("Editor ", "Added new Row "+newRowId);
                Toast.makeText(this, R.string.schedule_saved,Toast.LENGTH_SHORT).show();
                finish();
            }catch (SQLException e){
                Toast.makeText(this, "Error Adding new Schedule",Toast.LENGTH_SHORT).show();
            }
        }else {
            int rowAffected = getContentResolver().update(currentScheduleUri,
                    contentValues,null,
                    null);

            if(rowAffected != 0){
                Toast.makeText(this, R.string.schedule_update_successful,Toast.LENGTH_SHORT).show();
                finish();
            }else {
                Toast.makeText(this, R.string.schedule_update_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Setup the checker to get the values
     */

    private void setupCheckers(){
        if(doneCheck.isChecked()){
            doneWithSchedule = ScheduleEntry.DONE;
        }else {
            doneWithSchedule  = ScheduleEntry.NOT_DONE;
        }

        if(repeatCheck.isChecked()){
            repeatSchedule = ScheduleEntry.REPEAT_ON;
            repeatSpinner.setEnabled(true);
        }else{
            repeatSchedule = ScheduleEntry.REPEAT_OFF;
            repeatSpinner.setEnabled(false);
        }

        repeatCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    repeatCheck.setEnabled(true);
                    repeatSpinner.setEnabled(true);
                }else{
                    repeatSpinner.setEnabled(false);
                    repeatSpinner.setSelection(0);
                }
            }
        });
    }




    /**
     * Setup the dropdown spinner that allows user to select the recurring interval
     */

    private void setupSpinner(){

        ArrayAdapter intervalSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.schedule_repeat_interval_options, android.R.layout.simple_spinner_item);

        intervalSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        repeatSpinner.setAdapter(intervalSpinnerAdapter);

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)){
                    if(selection.equals(getString(R.string.daily))){
                    interval = ScheduleEntry.SCHEDULE_REPEAT_DAILY;
                }else if(selection.equals(getString(R.string.weekly))){
                    interval = ScheduleEntry.SCHEDULE_REPEAT_WEEKLY;
                }else if(selection.equals(getString(R.string.monthly))){
                    interval = ScheduleEntry.SCHEDULE_REPEAT_MONTHLY;
                }}else if (selection.equals(getString(R.string.not_repeating))){
                    interval = ScheduleEntry.SCHEDULE_NOT_REPEATING;
                    repeatCheck.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                interval = ScheduleEntry.SCHEDULE_NOT_REPEATING;

            }
        });
    }

    /**
     * find the views
     */

    private void findViews(){
        repeatSpinner = findViewById(R.id.spinner_interval);
        repeatCheck = findViewById(R.id.check_repeat);
        doneCheck = findViewById(R.id.check_done);
        nameEdit = findViewById(R.id.edit_course_name);
        idEdit = findViewById(R.id.edit_course_id);
        topicEdit = findViewById(R.id.edit_topic);
        timeEdit = findViewById(R.id.edit_time);
        dateEdit = findViewById(R.id.edit_date);
        noteEdit = findViewById(R.id.edit_note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(currentScheduleUri == null){
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.add_a_schedule);
            invalidateOptionsMenu();
        }
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                saveSchedule();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mScheduleChanged) {
                    NavUtils.navigateUpFromSameTask(ScheduleEditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(ScheduleEditorActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(currentScheduleUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ScheduleEntry._ID,
                ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID,
                ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME,
                ScheduleEntry.COLUMN_SCHEDULE_TOPIC,
                ScheduleEntry.COLUMN_SCHEDULE_TIME,
                ScheduleEntry.COLUMN_SCHEDULE_DATE,
                ScheduleEntry.COLUMN_SCHEDULE_REPEAT,
                ScheduleEntry.COLUMN_SCHEDULE_INTERVAL,
                ScheduleEntry.COLUMN_SCHEDULE_NOTE,
                ScheduleEntry.COLUMN_SCHEDULE_DONE
        };

        return new CursorLoader(this,
                currentScheduleUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        if(cursor.moveToFirst()){

//            int idColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.SCHEDULE_COLUMN_ID);
            int courseIDColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
            int courseNameColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
            int courseTopicColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
            int courseTimeColumnIndex  = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME);
            int courseDateColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
            int courseRepeatColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT);
            int courseIntervalColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
            int courseNoteColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE);
            int courseDoneColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE);

            String currentCourseId = cursor.getString(courseIDColumnIndex);
            String currentCourseName = cursor.getString(courseNameColumnIndex);
            String currentTopic = cursor.getString(courseTopicColumnIndex);
            String currentTime = cursor.getString(courseTimeColumnIndex);
            String currentDate = cursor.getString(courseDateColumnIndex);
            int currentRepeat = cursor.getInt(courseRepeatColumnIndex);
            int currentInterval = cursor.getInt(courseIntervalColumnIndex);
            int currentDone = cursor.getInt(courseDoneColumnIndex);
            String currentNote = cursor.getString(courseNoteColumnIndex);

            //update the view on the screen with values from the database
            idEdit.setText(currentCourseId);
            nameEdit.setText(currentCourseName);
            topicEdit.setText(currentTopic);
            timeEdit.setText(currentTime);
            dateEdit.setText(currentDate);
            noteEdit.setText(currentNote);

            //interval is a dropdown spinner so map the constant value from the database
            switch (currentInterval){
                case ScheduleEntry.SCHEDULE_REPEAT_DAILY:
                    repeatSpinner.setSelection(1);
                    break;
                case ScheduleEntry.SCHEDULE_REPEAT_WEEKLY:
                    repeatSpinner.setSelection(2);
                    break;
                case ScheduleEntry.SCHEDULE_REPEAT_MONTHLY:
                    repeatSpinner.setSelection(3);
                    break;
                case ScheduleEntry.SCHEDULE_NOT_REPEATING:
                default:
                    repeatSpinner.setSelection(0);
                    break;
            }

            //the map the checkbox state with the value from the interval
            if (currentDone == ScheduleEntry.DONE) {
                doneCheck.setChecked(true);
            } else {
                doneCheck.setChecked(false);
            }

            //ma the repeat checkbox with the value from the database
            switch (currentRepeat){
                case ScheduleEntry.REPEAT_ON:
                    repeatCheck.setChecked(true);
                    break;
                case ScheduleEntry.REPEAT_OFF:
                default:
                    repeatCheck.setChecked(false);
            }
        }

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
            idEdit.setText("");
            noteEdit.setText("");
            nameEdit.setText("");
            timeEdit.setText("");
            doneCheck.setChecked(false);
            repeatCheck.setChecked(false);
            repeatSpinner.setSelection(0);
            dateEdit.setText("");
    }


    @Override
    public void onBackPressed() {
        if(!mScheduleChanged){
        super.onBackPressed();
        return;}

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteSchedule();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deleteSchedule() {

        if(currentScheduleUri !=null){
            int rowDeleted = getContentResolver().delete(currentScheduleUri,null,null);

            if(rowDeleted == 0){
                Toast.makeText(getApplicationContext(),R.string.editor_delete_schedule_unsuccessful,Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_schedule_successful),
                        Toast.LENGTH_SHORT).show();
            }
            Log.d("Editor Deleted","Row Deleted "+rowDeleted);
            finish();
        }
    }

    public String DateDialog(){

        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //launching the date dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(ScheduleEditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        if(month <10 & dayOfMonth>10){
                            String nDate = year+"/0"+(month+1)+"/"+dayOfMonth;
                            dateEdit.setText(nDate);
                        }
                        else if(dayOfMonth <10 & month>10){
                            String nDate = year+"/"+(month+1)+"/0"+dayOfMonth;
                            dateEdit.setText(nDate);
                        }
                        else if(month <10 & dayOfMonth <10){
                            String nDate = year+"/0"+(month+1)+"/0"+dayOfMonth;
                            dateEdit.setText(nDate);
                        }
                        else{
                            String nDate = year+"/"+(month+1)+"/"+dayOfMonth;
                            dateEdit.setText(nDate);
                        }
                    }

                },year,month,day);
                datePickerDialog.show();
            }
        });
        return dateEdit.getText().toString();
    }

    //get the time from the time dialog frag
    public String TimeDialog() {
        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int Hour = calendar.get(Calendar.HOUR_OF_DAY);
                int Minute = calendar.get(Calendar.MINUTE);

                //launching the timepicker dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleEditorActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay < 10 & minute > 10) {
                            String mTime = "0" + hourOfDay + ":" + minute;
                            timeEdit.setText(mTime);
                        } else if (hourOfDay > 10 & minute < 10) {
                            String mTime = hourOfDay + ":0" + minute;
                            timeEdit.setText(mTime);
                        } else if (hourOfDay < 10 & minute < 10) {
                            String mTime = "0" + hourOfDay + ":0" + minute;
                            timeEdit.setText(mTime);
                        } else {
                            String mTime = hourOfDay + ":" + minute;
                            timeEdit.setText(mTime);
                        }


                    }
                }, Hour, Minute, true);

                timePickerDialog.show();
            }
        });
        return timeEdit.getText().toString();
    }



}