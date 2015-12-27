package ir.tsip.tracker.zarrintracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    String state="NoName";

    @Override
    public void onReceive(Context context, Intent intent) {
         entering = intent.getBooleanExtra(key, false);
        int id = intent.getIntExtra("id",0);
        if(id!=0){
            DatabaseHelper dbh=new DatabaseHelper(context);
            SQLiteDatabase db = dbh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Geogences.COLUMN_NAME_name};
            Cursor c;
            c = db.query(DatabaseContracts.Geogences.TABLE_NAME, columns, DatabaseContracts.Geogences.COLUMN_NAME_ID+"=?", new String[]{String.valueOf(id)}, "", "", "");
            c.moveToLast();
            while (true && c.getCount() > 0) {
                state=c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_name));
                break;
            }
            c.close();
            db.close();
            dbh.close();
        }
        if (entering) {
            state="Entered "+state;
        }else {
            state="Exited "+state;
        }
        HashMap<String, String> params;
        params = new HashMap<>();
        params.put("message", state);
        params.put("imei", Tools.GetImei(context));
        params.put("gpID", "-1");
        WebServices W = new WebServices(context);
        W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 4, params, "SetMessage");
        W=null;
        (new EventManager(context)).AddEvevnt(state, "-1");
        Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
        Log.d(getClass().getSimpleName(),state);
    }
}
