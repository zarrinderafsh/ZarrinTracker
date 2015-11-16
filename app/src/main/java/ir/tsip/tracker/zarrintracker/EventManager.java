package ir.tsip.tracker.zarrintracker;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

import java.util.Iterator;

/**
 * Created by morteza on 2015-11-14.
 */
public class EventManager {

    private Context Base;
    private static WebServices WS;
    public EventManager(Context context)
    {
        Base = context;
    }

    public void AddEvevnt(String S)
    {
        if(WS == null)
            WS = new WebServices(Base);
        WS.addQueue("ir.tsip.tracker.zarrintracker.EventManager",0,Tools.GetImei(Base)+"|"+S,"SendEvent");
    }

    public void SendSOS()
    {
        AddEvevnt("Please contact me ASAP.I my need your help");
        MessageEvent.AddMessage(Base , "Please contact me ASAP.I my need your help");
    }

}
