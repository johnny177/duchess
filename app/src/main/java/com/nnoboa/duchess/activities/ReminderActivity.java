package com.nnoboa.duchess.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import com.nnoboa.duchess.R;
import com.nnoboa.duchess.activities.editors.ReminderEditorActivity;
import com.nnoboa.duchess.activities.editors.ScheduleEditorActivity;
import com.nnoboa.duchess.controllers.adapters.ReminderCursorAdapter;
import com.nnoboa.duchess.data.AlarmContract;
import com.nnoboa.duchess.data.AlarmDbHelper;
import com.nnoboa.duchess.data.AlarmContract.ReminderEntry;

public class ReminderActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor>{

    ExtendedFloatingActionButton addReminder;
    ListView listView;
    ReminderCursorAdapter reminderCursorAdapter;
    View emptyView;

    int REMINDER_LOADER_ID = 00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        android.app.LoaderManager loaderManager = getLoaderManager();

        //find the resp views
        findViews();

        //start editor activity
        startEditorIntent();

        listView.setEmptyView(emptyView);

        listView.setAdapter(reminderCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editReminderIntent = new Intent(ReminderActivity.this, ReminderEditorActivity.class);

                Uri currentReminderUri = ContentUris.withAppendedId(ReminderEntry.CONTENT_URI,id);

                editReminderIntent.setData(currentReminderUri);
                startActivity(editReminderIntent);
            }
        });

        loaderManager.initLoader(REMINDER_LOADER_ID,null,this);

//        displayDatabaseInfo();

    }

    /**
     * Find the respective views
     */

    private void findViews(){
        addReminder = findViewById(R.id.add_reminder);
        listView = findViewById(R.id.reminder_list);
        emptyView = findViewById(R.id.rempty_view);
        reminderCursorAdapter =new ReminderCursorAdapter(this,null);
    }

    /**
     * Start the editor activity
     */

    private void startEditorIntent(){

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editorIntent = new Intent(ReminderActivity.this, ReminderEditorActivity.class);
                startActivity(editorIntent);
            }
        });

    }


//    private void displayDatabaseInfo(){
//
//        SQLiteOpenHelper dbHelper = new AlarmDbHelper(this);
//        SQLiteDatabase database = dbHelper.getReadableDatabase();
//
//        try (Cursor cursor = database.rawQuery("SELECT * FROM " + ReminderEntry.TABLE_NAME, null)) {
//            TextView displayText = findViewById(R.id.display_text);
//            displayText.setText(String.format("Number of rows in alarm databasetable : %d", cursor.getCount()));
//        }
//    }

    private void insertDummyData(){
        AlarmDbHelper dbHelper= new AlarmDbHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ReminderEntry.COLUMN_COURSE_ID,"MATH 350");
        values.put(ReminderEntry.COLUMN_COURSE_NAME,"DIFFERENTIAL EQUATIONS");
        values.put(ReminderEntry.COLUMN_REMINDER_TYPE,ReminderEntry.REMINDER_TYPE_LECTURES);
        values.put(ReminderEntry.COLUMN_REMINDER_TIME,"19:00");
        values.put(ReminderEntry.COLUMN_REMINDER_DATE, "09/07/2020");
        values.put(ReminderEntry.COLUMN_REMINDER_LOCATION,"NNB");
        values.put(ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS, ReminderEntry.REMINDER_IS_OFFLINE);
        values.put(ReminderEntry.COLUMN_REMINDER_REPEAT,ReminderEntry.REMINDER_IS_NOT_REPEATING);
        values.put(ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL,ReminderEntry.ONCE);

        Uri rowID = getContentResolver().insert(ReminderEntry.CONTENT_URI,values);
        Toast.makeText(ReminderActivity.this,"New row added "+rowID,Toast.LENGTH_SHORT).show();


    }

    private void clearDatabase(){
        getContentResolver().delete(ReminderEntry.CONTENT_URI,null,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all:
                clearDatabase();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                ReminderEntry._ID,
                ReminderEntry.COLUMN_COURSE_ID,
                ReminderEntry.COLUMN_COURSE_NAME,
                ReminderEntry.COLUMN_REMINDER_TYPE,
                ReminderEntry.COLUMN_REMINDER_TIME,
                ReminderEntry.COLUMN_REMINDER_DATE,
                ReminderEntry.COLUMN_REMINDER_LOCATION,
                ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS,
                ReminderEntry.COLUMN_REMINDER_REPEAT,
                ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL,
                ReminderEntry.COLUMN_REMINDER_STATUS,
                ReminderEntry.COLUMN_REMINDER_NOTE};
        return new CursorLoader(this,
                ReminderEntry.CONTENT_URI,
                projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        reminderCursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reminderCursorAdapter.swapCursor(null);

    }
}