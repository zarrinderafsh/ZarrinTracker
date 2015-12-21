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

import java.util.HashMap;

/**
 * Created by ali on 11/21/15.
 */
public class ProximityIntentReceiver extends BroadcastReceiver {
    String key = LocationManager.KEY_PROXIMITY_ENTERING;
    Boolean entering;
    String state;

    @Override
    public void onReceive(Context context, Intent intent) {
         entering = intent.getBooleanExtra(key, false);
        if (entering) {
            state="enter";
        }else {
            state="exit";
        }
        HashMap<String, String> params;
        params = new HashMap<>();
        params.put("message", state);
        params.put("imei", Tools.GetImei(context));
        params.put("gpID", "-1");
        WebServices W = new WebServices(context);
        W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 0, params, "SetMessage");
        W=null;
        (new EventManager(context)).AddEvevnt(state, "-1");
        Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
        Log.d(getClass().getSimpleName(),state);
    }
}
