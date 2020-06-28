package com.nnoboa.duchess.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.nnoboa.duchess.data.AlarmContract.*;

public class AlarmDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alarm.db";
    private static final String COMMA = ", ";
    private static final String TEXT = " TEXT";
    private static final String INT = " INTEGER";

    public static final String SQL_CREATE_SCHEDULE_DBT_ENTRIES =
            "CREATE TABLE "+ ScheduleEntry.TABLE_NAME +
                    " ("+
                    ScheduleEntry.COLUMN_ID + INT+" PRIMARY KEY AUTOINCREMENT"+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_COURSE_ID+ TEXT+" NOT NULL" + COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_COURSE_NAME+ TEXT+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_TOPIC+TEXT +" NOT NULL"+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_TIME+TEXT+" NOT NULL"+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_DATE+TEXT+" NOT NULL"+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_REPEAT+" NOT NULL"+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_INTERVAL+INT+" NOT NULL"+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_NOTE+TEXT+COMMA+
                    ScheduleEntry.COLUMN_SCHEDULE_DONE+INT+" NOT NULL DEFAULT "+ScheduleEntry.NOT_DONE+" )";






    public AlarmDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SCHEDULE_DBT_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
