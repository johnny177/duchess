package com.nnoboa.duchess.data;

import android.app.backup.BackupAgent;
import android.provider.BaseColumns;

public class FlashContract {

    private FlashContract(){}

    public class SetEntry implements BaseColumns{

    /**table name for flashSet**/

    public static final String TABLE_NAME ="flash_set";

    public static final String _ID = BaseColumns._ID;

    public static final String COLUMN_TITLE = "title";

    public static final String COLUMN_COUNT = BaseColumns._COUNT;

    public static final String COLUMN_STUDY_STATUS = "study_status";


    }
}
