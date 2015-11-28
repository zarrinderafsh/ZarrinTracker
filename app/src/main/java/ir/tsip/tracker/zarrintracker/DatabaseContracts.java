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

    public static abstract class Events implements BaseColumns {
        public static final String TABLE_NAME = "Events";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_Date = "Date";
        public static final String COLUMN_NAME_Data = "content";
        public static final String COLUMN_NAME_Image = "Image";
        public static final String COLUMN_NAME_Lat = "Lat";
        public static final String COLUMN_NAME_Lon = "Lon";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + Events.TABLE_NAME + " (" +
                        Events.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        Events.COLUMN_NAME_Date + " Date," +
                        Events.COLUMN_NAME_Data + " nvarchar(250),"+
                        Events.COLUMN_NAME_Lat + " float,"+
                        Events.COLUMN_NAME_Lon + " float,"+
                        Events.COLUMN_NAME_Image + " BLOB )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + Events.TABLE_NAME;
    }

    public static abstract class Groups implements BaseColumns {
        public static final String TABLE_NAME = "Groups";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_Name = "Name";
        public static final String COLUMN_NAME_Image = "Image";
        public static final String COLUMN_NAME_LastMessage = "LastMessage";
        public static final String COLUMN_NAME_LastTime = "LastTime";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + Groups.TABLE_NAME + " (" +
                        Groups.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        Groups.COLUMN_NAME_Name + " nvarchar(250)," +
                        Groups.COLUMN_NAME_Image + " BLOB,"+
                        Groups.COLUMN_NAME_LastMessage + "  nvarchar(250),"+
                        Groups.COLUMN_NAME_LastTime + " Date )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + Groups.TABLE_NAME;
    }

    public static abstract class Temp implements BaseColumns {
        public static final String TABLE_NAME = "Temps";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_Image = "Image";
        public static final String COLUMN_NAME_name = "name";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + Temp.TABLE_NAME + " (" +
                        Temp.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        Temp.COLUMN_NAME_name + " nvarchar(250)," +
                        Temp.COLUMN_NAME_Image + " BLOB )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + Temp.TABLE_NAME;
    }


    public static abstract class Geogences implements BaseColumns {
        public static final String TABLE_NAME = "geofences";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_radius = "radius";
        public static final String COLUMN_NAME_name = "name";
        public static final String COLUMN_NAME_center = "center";
        public static final String SQL_CREATE_Table =
                "CREATE TABLE " + Geogences.TABLE_NAME + " (" +
                        Geogences.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        Geogences.COLUMN_NAME_name + " nvarchar(250)," +
                        Geogences.COLUMN_NAME_center + " nvarchar(250)," +
                        Geogences.COLUMN_NAME_radius + " INTEGER )";

        public static final String SQL_DELETE_Table =
                "DROP TABLE IF EXISTS " + Geogences.TABLE_NAME;
    }
}

