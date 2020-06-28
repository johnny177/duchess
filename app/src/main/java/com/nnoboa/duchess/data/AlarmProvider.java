package com.nnoboa.duchess.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.nnoboa.duchess.data.AlarmContract.*;

public class AlarmProvider extends ContentProvider {
    private static final int SCHEDULES = 100;
    private static final int SCHEDULE_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULES,SCHEDULES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_SCHEDULES+"/#",SCHEDULE_ID);

    }

    /** Database Helper Object*/
    private AlarmDbHelper alarmDbHelper;



    @Override
    public boolean onCreate() {

        alarmDbHelper = new AlarmDbHelper(getContext());
        return true;
    }



    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = alarmDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case SCHEDULES:
                cursor = database.query(ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        ScheduleEntry.COLUMN_SCHEDULE_DATE,
                        null,
                        sortOrder);
                break;
            case SCHEDULE_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ScheduleEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ScheduleEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);

        switch (match){
            case SCHEDULES:
                return ScheduleEntry.CONTENT_LIST_TYPE;
            case SCHEDULE_ID:
                return ScheduleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);

        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match){
        case SCHEDULES:
            return insertSchedule(uri, values);
        default:
            throw new IllegalArgumentException("Insertion is not supported for this uri "+uri);
        }
    }

    private Uri insertSchedule(Uri uri, ContentValues values){
        SQLiteDatabase database = alarmDbHelper.getWritableDatabase();
        String courseID = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
        String courseName = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
        String courseTopic = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_TOPIC);
        String courseTime = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_TIME);
        String courseDate = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_DATE);

        String courseNote = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_NOTE);

        if(courseID == null){
            throw new IllegalArgumentException("Schedule requires Course ID");
        }

        if(courseTopic == null){
            throw new IllegalArgumentException("Schedule requires topic");
        }

        long id = database.insert(ScheduleEntry.TABLE_NAME,null,values);

        if (id == -1){
            Log.e(AlarmProvider.class.getSimpleName(),"Failed to insert row for "+ uri);

            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = alarmDbHelper.getWritableDatabase();
        int rowDeleted = 0;

        final int match = uriMatcher.match(uri);
        switch (match){
            case SCHEDULES:
                rowDeleted = database.delete(ScheduleEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case SCHEDULE_ID:
                selection = ScheduleEntry.COLUMN_ID +"=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = database.delete(ScheduleEntry.TABLE_NAME,selection,selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
                }
                if(rowDeleted != 0 ){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                return rowDeleted;
        }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        if(values.containsKey(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME)){
            String courseName = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME);
            if(courseName == null){
                throw new IllegalArgumentException("Schedule requires a name");
            }

            String courseID = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID);
            if(courseID == null){
                throw new IllegalArgumentException("schedule requires a course ID");
            }

            String courseTopic = values.getAsString(ScheduleEntry.COLUMN_SCHEDULE_TOPIC);

            if(courseTopic == null){
                throw new IllegalArgumentException("schedule requires a topic");
            }

            SQLiteDatabase database = alarmDbHelper.getWritableDatabase();

            int rowUpdated = database.update(ScheduleEntry.TABLE_NAME,values,selection,selectionArgs);

            if(rowUpdated != 0){
                getContext().getContentResolver().notifyChange(uri,null);
            }

            return rowUpdated;
        }
        return 0;
    }
}
