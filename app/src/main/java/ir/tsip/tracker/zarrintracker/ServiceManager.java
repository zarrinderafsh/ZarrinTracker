package ir.tsip.tracker.zarrintracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 10/31/2015.
 */

public class ServiceManager {

    enum ServiceState{
        Create,
        Running,
        Stop,
    }

    public static boolean isMyServiceRunning(Context pContext , Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) pContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ServiceState StartService(Context pContext, Class<?> pClass)
    {
        try {
            if (!ServiceManager.isMyServiceRunning(pContext, pClass)) {
                //Create Location Service
                Intent LocationService = new Intent(pContext, pClass);
                pContext.startService(LocationService);
                return ServiceState.Create;
            } else {
                //Location Service is run
                return ServiceState.Running;
            }
        }
        catch (Exception ex)
        {
            return ServiceState.Stop;
        }
    }
}
