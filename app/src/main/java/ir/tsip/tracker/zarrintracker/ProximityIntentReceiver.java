package ir.tsip.tracker.zarrintracker;


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
    int areaOwnerCode;

    @Override
    public void onReceive(Context context, Intent intent) {
         entering = intent.getBooleanExtra(key, false);
        int id =Integer.valueOf(intent.getExtras().getInt("id"));
        if(id==0) {
            try {
                id = Integer.valueOf(intent.getStringExtra("id"));
            } catch (Exception er) {

            }
        }
        if(id!=0){
            DatabaseHelper dbh=new DatabaseHelper(context);
            SQLiteDatabase db = dbh.getReadableDatabase();
            String[] columns = {DatabaseContracts.Geogences.COLUMN_NAME_name,DatabaseContracts.Geogences.COLUMN_OwnerCOde};
            Cursor c;
            c = db.query(DatabaseContracts.Geogences.TABLE_NAME, columns, DatabaseContracts.Geogences.COLUMN_NAME_ID+"=?", new String[]{String.valueOf(id)}, "", "", "");
            c.moveToLast();
            while (true && c.getCount() > 0) {
                state=c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_name));

                 areaOwnerCode = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_OwnerCOde));

                 break;
            }
            c.close();
            db.close();
            dbh.close();
        }
        else {
            try {
                LocationListener.locationManager.removeProximityAlert(PendingIntent.getBroadcast(LocationListener.mContext, id, new Intent("ir.tstracker.activity.proximity").putExtra("id", id), 0));
            } catch (Exception exx) {


            }
        }
            if (entering) {
            state=context.getResources().getString(R.string.entered)+" "+state;
        }else {
            state=context.getResources().getString(R.string.exited)+" "+state;
        }
       EventManager ev=(new EventManager(context));
        ev.gpId=String.valueOf(areaOwnerCode);
       ev.AddEvevnt(state, "-1", MessageEvent.AREA_EVENT);
     //   Toast.makeText(context, state, Toast.LENGTH_SHORT).show();
    }
}
