package ir.tsip.tracker.zarrintracker;

import android.content.Context;
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

    public void AddEvevnt(String S,String gpID)
    {
        params = new HashMap<>();
        params.put("message", S);
        params.put("imei",Tools.GetImei(Base));
        params.put("gpID", gpID);
        if(WS == null)
            WS = new WebServices(Base);
        WS.addQueue("ir.tsip.tracker.zarrintracker.EventManager",0,params,"SendEvent");
        MessageEvent.InsertMessage(Base, S);
    }

    public void SendSOS()
    {
        AddEvevnt("Please contact me ASAP.I may need your help","-2");
    }

}
