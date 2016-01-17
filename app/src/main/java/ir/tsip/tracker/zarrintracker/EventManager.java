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
        AddEvevnt(Base.getResources().getString(R.string.sosMessage), "-2",MessageEvent.SOS_EVENT);

    }

}
