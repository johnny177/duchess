package com.nnoboa.duchess.activities;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.nnoboa.duchess.R;
import com.nnoboa.duchess.activities.editors.ReminderEditorActivity;
import com.nnoboa.duchess.controllers.adapters.ReminderCursorAdapter;
import com.nnoboa.duchess.controllers.alarm.AlarmStarter;
import com.nnoboa.duchess.controllers.alarm.Util;
import com.nnoboa.duchess.data.AlarmContract.ReminderEntry;
import com.nnoboa.duchess.data.AlarmDbHelper;

public class ReminderActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    final int REMINDER_LOADER_ID = 00;
    ExtendedFloatingActionButton addReminder;
    ListView listView;
    ReminderCursorAdapter reminderCursorAdapter;
    View emptyView;

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
        AlarmStarter.init(this);
        listView.setAdapter(reminderCursorAdapter);

        listView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) {
                    addReminder.shrink();
                } else {
                    addReminder.extend();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent
                        editReminderIntent =
                        new Intent(ReminderActivity.this, ReminderEditorActivity.class);

                Uri currentReminderUri = ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, id);

                editReminderIntent.setData(currentReminderUri);
                startActivity(editReminderIntent);
            }
        });

        loaderManager.initLoader(REMINDER_LOADER_ID, null, this);

//        displayDatabaseInfo();

    }

    /**
     * Find the respective views
     */

    private void findViews() {
        addReminder = findViewById(R.id.add_reminder);
        listView = findViewById(R.id.reminder_list);
        emptyView = findViewById(R.id.rempty_view);
        reminderCursorAdapter = new ReminderCursorAdapter(this, null);
    }

    /**
     * Start the editor activity
     */

    private void startEditorIntent() {

        addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent
                        editorIntent =
                        new Intent(ReminderActivity.this, ReminderEditorActivity.class);
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

    @SuppressWarnings("unused")
    private void insertDummyData() {
        AlarmDbHelper dbHelper = new AlarmDbHelper(this);

        //noinspection unused
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ReminderEntry.COLUMN_COURSE_ID, "MATH 350");
        values.put(ReminderEntry.COLUMN_COURSE_NAME, "DIFFERENTIAL EQUATIONS");
        values.put(ReminderEntry.COLUMN_REMINDER_TYPE, ReminderEntry.REMINDER_TYPE_LECTURES);
        values.put(ReminderEntry.COLUMN_REMINDER_TIME, "19:00");
        values.put(ReminderEntry.COLUMN_REMINDER_DATE, "09/07/2020");
        values.put(ReminderEntry.COLUMN_REMINDER_MILLI, "34523736524");
        values.put(ReminderEntry.COLUMN_REMINDER_LOCATION, "NNB");
        values.put(ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS, ReminderEntry.REMINDER_IS_OFFLINE);
        values.put(ReminderEntry.COLUMN_REMINDER_REPEAT, ReminderEntry.REMINDER_IS_NOT_REPEATING);
        values.put(ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL, ReminderEntry.ONCE);

        Uri rowID = getContentResolver().insert(ReminderEntry.CONTENT_URI, values);
        Toast.makeText(ReminderActivity.this, "New row added " + rowID, Toast.LENGTH_SHORT).show();


    }

    private void clearDatabase() {
        getContentResolver().delete(ReminderEntry.CONTENT_URI, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyData();
                return true;
            case R.id.action_delete_all:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

// --Commented out by Inspection START (7/22/20 3:55 PM):
//    private void showDeleteConfirmationDialog() {
//        // Create an AlertDialog.Builder and set the message, and click listeners
//        // for the positive and negative buttons on the dialog.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(R.string.delete_dialog_msg);
//        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked the "Delete" button, so delete the reminder.
//                deleteReminder();
//
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked the "Cancel" button, so dismiss the dialog
//                // and continue editing the pet.
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        // Create and show the AlertDialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
// --Commented out by Inspection STOP (7/22/20 3:55 PM)

    private void deleteReminder() {
        if (ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, listView.getId()) != null) {
            int
                    rowDeleted =
                    getContentResolver().delete(ContentUris.withAppendedId(ReminderEntry.CONTENT_URI, listView.getId()), null, null);

            if (rowDeleted == 0) {
                Toast.makeText(getApplicationContext(), R.string.editor_delete_schedule_unsuccessful, Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_schedule_successful),
                        Toast.LENGTH_SHORT).show();
            }
            Log.d("Editor Deleted", "Row Deleted " + rowDeleted);
        }
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
                ReminderEntry.COLUMN_REMINDER_MILLI,
                ReminderEntry.COLUMN_REMINDER_ONLINE_STATUS,
                ReminderEntry.COLUMN_REMINDER_REPEAT,
                ReminderEntry.COLUMN_REMINDER_REPEAT_INTERVAL,
                ReminderEntry.COLUMN_REMINDER_STATUS,
                ReminderEntry.COLUMN_REMINDER_NOTE};

        return new CursorLoader(this,
                ReminderEntry.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        reminderCursorAdapter.swapCursor(data);
        AlarmStarter.init(this);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reminderCursorAdapter.swapCursor(null);

    }

    @Override
    protected void onStart() {
        Util.scheduleJob(this);
        AlarmStarter.init(this);
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        AlarmStarter.init(this);
        Util.scheduleJob(this);
        super.onDestroy();
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_all_confirmation));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the reminder.
                clearDatabase();

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
}