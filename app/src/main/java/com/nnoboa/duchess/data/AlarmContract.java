package com.nnoboa.duchess.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class AlarmContract  {

    private AlarmContract(){}

    public static final String CONTENT_AUTHORITY = "com.nnoboa.duchess.schedules";
    public  static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_SCHEDULES = "schedules";

    public static final class ScheduleEntry implements BaseColumns{

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of  schedules
         */

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/"+
                        CONTENT_AUTHORITY +
                        "/"+ PATH_SCHEDULES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single schedule
         */

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/"+
                        CONTENT_AUTHORITY+
                        "/"+PATH_SCHEDULES;

        /**
         * The content uri to access the schedule data in the provider
         */

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SCHEDULES);

        public static final String TABLE_NAME = "schedules";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_SCHEDULE_COURSE_ID = "course_id";
        public static final String COLUMN_SCHEDULE_COURSE_NAME = "course_name";
        public static final String COLUMN_SCHEDULE_TOPIC = "topic";
        public static final String COLUMN_SCHEDULE_TIME = "time";
        public static final String COLUMN_SCHEDULE_DATE = "date";
        public static final String COLUMN_SCHEDULE_REPEAT = "repeat";
        public static final String COLUMN_SCHEDULE_INTERVAL= "interval";
        public static final String COLUMN_SCHEDULE_NOTE = "note";
        public static final String COLUMN_SCHEDULE_DONE = "done";

        /**
         * Possible intervals
         */
        public static final int SCHEDULE_NOT_REPEATING = 0;
        public static final int SCHEDULE_REPEAT_DAILY = 1;
        public static final int SCHEDULE_REPEAT_WEEKLY = 2;
        public static final int SCHEDULE_REPEAT_MONTHLY = 3;

        /**
         * Possible repeat values
         */

        public static final int REPEAT_ON = 100;
        public static final int REPEAT_OFF = 101;

        /**
         * Possible done values
         */

        public static final int DONE = 1000;
        public static final int NOT_DONE = 1001;

        public static boolean isValidInterval(int interval){
            if(interval == SCHEDULE_REPEAT_DAILY ||interval ==SCHEDULE_NOT_REPEATING
                    ||interval == SCHEDULE_REPEAT_MONTHLY ||
            interval == SCHEDULE_REPEAT_WEEKLY) {
                return true;
            }
            return false;
        }

    }

    public static final class ReminderEntry implements BaseColumns {

        //TODO implement methods
    }
}
