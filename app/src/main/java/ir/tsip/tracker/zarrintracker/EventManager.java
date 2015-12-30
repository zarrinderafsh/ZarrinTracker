package ir.tsip.tracker.zarrintracker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by morteza on 2015-11-14.
 */
public class EventManager {

    private Context Base;
    private static WebServices WS;
    HashMap<String, String> params;
    public EventManager(Context context)
    {
        Base = context;
    }

    public void AddEvevnt(String S,String msgtype,String eventType)
    {
       params = new HashMap<>();
        params.put("message", S);
        params.put("imei",Tools.GetImei(Base));
        params.put("gpID", "0");
        params.put("msgtype", msgtype);
        if(WS == null)
            WS = new WebServices(Base);
        WS.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity",2,params,"SetMessage");
        MessageEvent.InsertMessage(Base, S,eventType);
    }

    public void SendSOS() {
        AddEvevnt("Please contact me ASAP.I may need your help", "-2",MessageEvent.SOS_EVENT);
//        WebServices W;
//        HashMap<String, String> params;
//        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
//        SQLiteDatabase db = dbh.getReadableDatabase();
//        Cursor c;
//        c = db.query(DatabaseContracts.Groups.TABLE_NAME, null, "", null, "", "", DatabaseContracts.Groups.COLUMN_NAME_LastTime + " DESC", "");
//        if (c.moveToFirst()) {
//            do {
//                int id = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_ID));
//
//                params = new HashMap<>();
//                params.put("message", "Please contact me ASAP.I may need your help");
//                params.put("imei", Tools.GetImei(MainActivity.Base));
//                params.put("gpID", String.valueOf(id));
//                W = new WebServices(MainActivity.Base);
//                W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", -1, params, "SetMessage");
//
//                W = null;
//            }
//            while (c.moveToNext());
//        }
    }

}
