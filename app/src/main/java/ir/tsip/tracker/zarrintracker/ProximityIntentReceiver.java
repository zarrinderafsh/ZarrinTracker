package ir.tsip.tracker.zarrintracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ali on 11/21/15.
 */
public class ProximityIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_PROXIMITY_ENTERING;
        Boolean entering = intent.getBooleanExtra(key, false);
        if (entering) {
            (new EventManager(context)).AddEvevnt("enter.","-1");
            Toast.makeText(context, "Enter", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getSimpleName(), "entering");
        }else {
            (new EventManager(context)).AddEvevnt("exit.","-1");
            Toast.makeText(context, "exit", Toast.LENGTH_SHORT).show();
            Log.d(getClass().getSimpleName(), "exiting");
        }
    }
}
