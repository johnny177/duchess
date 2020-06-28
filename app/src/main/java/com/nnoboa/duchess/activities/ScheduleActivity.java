package com.nnoboa.duchess.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nnoboa.duchess.R;
import com.nnoboa.duchess.controllers.ScheduleCursorAdapter;
import com.nnoboa.duchess.data.AlarmContract;
import com.nnoboa.duchess.data.AlarmDbHelper;

public class ScheduleActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    ExtendedFloatingActionButton addSchedule;
    TextView displayText;
    android.app.LoaderManager loaderManager;
    ListView scheduleList;
    ScheduleCursorAdapter scheduleCursorAdapter;
    View emptyView;

    int SCHEDULE_LOADER_ID = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        loaderManager = getLoaderManager();

        scheduleCursorAdapter = new ScheduleCursorAdapter(this,null,true);


        findViews();

        startEditorIntent();
        scheduleList.setEmptyView(emptyView);
        scheduleList.setAdapter(scheduleCursorAdapter);
        scheduleList.setDivider(null);
        if(scheduleList.getCount() != 0){
            addSchedule.shrink();
        }
        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editScheduleIntent = new Intent(ScheduleActivity.this, EditorActivity.class);


                Uri currentScheduleUri = ContentUris.withAppendedId(AlarmContract.ScheduleEntry.CONTENT_URI,id);

                editScheduleIntent.setData(currentScheduleUri);

                startActivity(editScheduleIntent);
            }
        });

        loaderManager.initLoader(SCHEDULE_LOADER_ID,null, this);

    }


    /**
     * Find the respective views
     */

    private void findViews(){
        addSchedule = findViewById(R.id.add_schedule);
        scheduleList = findViewById(R.id.schedule_list);
        emptyView = findViewById(R.id.empty_view);
//        displayText = findViewById(R.id.tem_textView);
    }

    /**
     * Start the editor activity
     */

    private void startEditorIntent(){

        addSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(ScheduleActivity.this, EditorActivity.class);
                startActivity(editorIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_schedule,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_delete_all_schedules:
                deleteAllSchedules();
                return true;

            case R.id.insert_dummy_data:
                insertSchedule();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertSchedule() {
        ContentValues values = new ContentValues();
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID,"ECON 312");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME,"Microeconomics");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC,"Demand");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME,"19:00");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE,"21/01/1990");
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT, AlarmContract.ScheduleEntry.REPEAT_OFF);
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL, AlarmContract.ScheduleEntry.SCHEDULE_REPEAT_DAILY);
        values.put(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE, AlarmContract.ScheduleEntry.NOT_DONE);
        Uri rowID = getContentResolver().insert(AlarmContract.ScheduleEntry.CONTENT_URI,values);

        Log.d("Dummy Data", ""+rowID);

        Toast.makeText(getApplicationContext(), "Inserted "+rowID,Toast.LENGTH_LONG).show();
    }

//    private void displayDatabaseInfo(){
//
//        AlarmDbHelper helper = new AlarmDbHelper(this);
//
//        SQLiteDatabase  db = helper.getReadableDatabase();
//
//        String[] projection = {
//                AlarmContract.ScheduleEntry.SCHEDULE_COLUMN_ID,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE,
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE
//        };
//
//        Cursor cursor = db.query(AlarmContract.ScheduleEntry.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null);
//
//        displayText.setText("The Number of Schedules in The Database is "+cursor.getCount()+" schedules.\n\n");
//        displayText.append(AlarmContract.ScheduleEntry.SCHEDULE_COLUMN_ID+" - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID+" - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME+" - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC+" - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME+" - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE+ " - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT+ "- "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL+ " - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE+" - "+
//                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE+"\n");
//
//
//        int idColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.SCHEDULE_COLUMN_ID);
//        int courseIDColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
//        int courseNameColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
//        int courseTopicColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
//        int courseTimeColumnIndex  = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME);
//        int courseDateColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE);
//        int courseRepeatColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT);
//        int courseIntervalColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL);
//        int courseNoteColumnIndex = cursor.getColumnIndex(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE);
//        int courseDoneColumnIndex = cursor.getColumnIndexOrThrow(AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE);
//
//
//
//        while (cursor.moveToNext()){
//            int currentID = cursor.getInt(idColumnIndex);
//            String currentCourseId = cursor.getString(courseIDColumnIndex);
//            String currentCourseName = cursor.getString(courseNameColumnIndex);
//            String currentTopic = cursor.getString(courseTopicColumnIndex);
//            String currentTime = cursor.getString(courseTimeColumnIndex);
//            String currentDate = cursor.getString(courseDateColumnIndex);
//            int currentRepeat = cursor.getInt(courseRepeatColumnIndex);
//            int currentInterval = cursor.getInt(courseIntervalColumnIndex);
//            int currentDone = cursor.getInt(courseDoneColumnIndex);
//            String currentNote = cursor.getString(courseNoteColumnIndex);
//
//            displayText.append("\n" +currentID +" - "+
//                    currentCourseId+" - "+
//                    currentCourseName+" - "+
//                    currentTopic+" - "+
//                    currentTime+" - "+
//                    currentDate+ " - "+
//                    currentRepeat+ " - "+
//                    currentInterval+" - "+
//                    currentNote+ " - "+
//                    currentDone);
//
//        }
//
//        if(cursor.getCount()!=0){
//            addSchedule.shrink();
//        }
//        cursor.close();
//
//    }





    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllSchedules() {
        int rowsDeleted = getContentResolver().delete(AlarmContract.ScheduleEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from schedule database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TOPIC,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_TIME,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DATE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_REPEAT,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_INTERVAL,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_NOTE,
                AlarmContract.ScheduleEntry.COLUMN_SCHEDULE_DONE
        };

        return new CursorLoader(this,
                AlarmContract.ScheduleEntry.CONTENT_URI,
                projection,
                null,
                null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        scheduleCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        scheduleCursorAdapter.swapCursor(null);

    }
}