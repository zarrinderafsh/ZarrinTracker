package ir.tsip.tracker.zarrintracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by ali on 9/26/15.
 */
public class DatabaseHelper extends android.database.sqlite.SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tsTrackerDB.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContracts.Settings.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.AVLData.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.ChatLog.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.QueueTable.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.Events.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.Groups.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.Temp.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.Geogences.SQL_CREATE_Table);
        db.execSQL(DatabaseContracts.Persons.SQL_CREATE_Table);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContracts.Settings.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.AVLData.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.ChatLog.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.QueueTable.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.Events.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.Groups.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.Temp.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.Geogences.SQL_DELETE_Table);
        db.execSQL(DatabaseContracts.Persons.SQL_DELETE_Table);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}
