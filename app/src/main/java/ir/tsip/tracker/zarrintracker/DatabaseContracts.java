package ir.tsip.tracker.zarrintracker;

import android.provider.BaseColumns;

/**
 * Created by Administrator on 10/31/2015.
 */
public class DatabaseContracts {
    public DatabaseContracts() {
    }

    public static abstract class Settings implements BaseColumns {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_logo = "logo";
        public static final String COLUMN_NAME_tell = "tell";
        public static final String COLUMN_NAME_key = "key";
        public static final String COLUMN_NAME_site = "site";
        public static final String COLUMN_NAME_days = "days";
        public static final String COLUMN_NAME_fromTime = "fromTime";
        public static final String COLUMN_NAME_endTime = "endTime";
        public static final String COLUMN_NAME_interval = "interval";
        public static final String COLUMN_NAME_RunningAlarm = "RunningAlarm";
        public static final String COLUMN_NAME_Accurate = "Accurate";

        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + Settings.TABLE_NAME + " (" +
                        Settings.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        Settings.COLUMN_NAME_days + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_endTime + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_fromTime + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_key + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_logo + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_site + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_tell + " varchar(20) " + COMMA_SEP +
                        Settings.COLUMN_NAME_Accurate + " char(1) " + COMMA_SEP +
                        Settings.COLUMN_NAME_RunningAlarm + " INTEGER " + COMMA_SEP +
                        Settings.COLUMN_NAME_interval + " varchar(20) " +  " )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + Settings.TABLE_NAME;
    }
    public static abstract class AVLData implements BaseColumns {
        public static final String TABLE_NAME = "AVLData";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_Data = "data";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + AVLData.TABLE_NAME + " (" +
                        AVLData.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        AVLData.COLUMN_NAME_Data + " varchar(20) )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + AVLData.TABLE_NAME;
    }

    public static abstract class ChatLog implements BaseColumns {
        public static final String TABLE_NAME = "Chat";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_Data = "content";
        public static final String COLUMN_NAME_Group = "GroupId";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + ChatLog.TABLE_NAME + " (" +
                        ChatLog.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        ChatLog.COLUMN_NAME_Group + " INTEGER," +
                        ChatLog.COLUMN_NAME_Data + " nvarchar(250) )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + ChatLog.TABLE_NAME;
    }

    public static abstract class QueueTable implements BaseColumns {
        public static final String TABLE_NAME = "QueueTable";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_ClassName = "ClassName";
        public static final String COLUMN_NAME_ObjectCode = "ObjectCode";
        public static final String COLUMN_NAME_WebServiceName = "WebServiceName";
        public static final String COLUMN_NAME_Data = "Data";
        public static final String COLUMN_NAME_State = "State";
        public static final String COLUMN_NAME_Resault = "Resault";

        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + QueueTable.TABLE_NAME + " (" +
                        QueueTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        QueueTable.COLUMN_NAME_ClassName + " nvarchar(250)," +
                        QueueTable.COLUMN_NAME_ObjectCode + " INTEGER," +
                        QueueTable.COLUMN_NAME_WebServiceName + " nvarchar(250)," +
                        QueueTable.COLUMN_NAME_Data + " BLOB," +
                        QueueTable.COLUMN_NAME_State + " INTEGER," +
                        QueueTable.COLUMN_NAME_Resault + " nvarchar(1024));";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + QueueTable.TABLE_NAME;
    }
}

