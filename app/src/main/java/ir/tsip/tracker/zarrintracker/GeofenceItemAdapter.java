package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ali on 12/3/15.
 */
public class GeofenceItemAdapter extends BaseAdapter {
    ArrayList<Objects.GeofenceItem> items;
    Activity activity;
    private LayoutInflater inflater;
ImageButton ibtnEdit,ibtnDelete;
    TextView txtGEoName;

    public GeofenceItemAdapter(Activity activity,ArrayList<Objects.GeofenceItem> Items){
        super();
        this.items=Items;
        this.activity=activity;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.geofence_item, null);

      final  Objects.GeofenceItem item=(Objects.GeofenceItem)this.getItem(position);

       txtGEoName= ((TextView)convertView.findViewById(R.id.txtGeofenceName));
        ibtnDelete=(ImageButton)convertView.findViewById(R.id.ibtnDeletegeofence);
        ibtnEdit=(ImageButton)convertView.findViewById(R.id.ibtnEditGeofence);

        txtGEoName.setText(item.name);
        ibtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity, MapPlacesActivity.class);
                myIntent.putExtra("lat", item.latitude);
                myIntent.putExtra("lng", item.longitude);
                myIntent.putExtra("radius", item.radius);
                myIntent.putExtra("id", item.id);
                myIntent.putExtra("name", item.name);
                activity.startActivity(myIntent);

            }
        });
        ibtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params;
                params = new HashMap<>();
                params.put("imei", Tools.GetImei(GeofenceItemAdapter.this.activity));
                params.put("clientCode",String.valueOf( items.get(position).id));
                params.put("center", String.valueOf(items.get(position).latitude)+","+String.valueOf( items.get(position).longitude));
                params.put("radius", String.valueOf( items.get(position).radius));
                params.put("name", items.get(position).name);
                params.put("operation", "3");//delete
                WebServices W = new WebServices(GeofenceItemAdapter.this.activity);
                W.addQueue("ir.tsip.tracker.zarrintracker.Places", 3, params, "GeofenceOperations");
                W = null;
                DatabaseHelper dbh = new DatabaseHelper(GeofenceItemAdapter.this.activity);
                SQLiteDatabase db = dbh.getWritableDatabase();
                db.delete(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(items.get(position).id)});
                db.close();
                dbh.close();
                db=null;
                dbh=null;
                try {
                    LocationListener.locationManager.removeProximityAlert(  PendingIntent.getBroadcast(LocationListener.mContext, 0, new Intent("ir.tstracker.activity.proximity").putExtra("id",String.valueOf(items.get(position).id)), 0));
                }
                catch (Exception er){

                 }
                items.remove(position);
                GeofenceItemAdapter.this.notifyDataSetChanged();
            }
        });
        return  convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }
}
