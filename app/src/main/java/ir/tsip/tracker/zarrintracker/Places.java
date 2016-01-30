package ir.tsip.tracker.zarrintracker;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Places extends AppCompatActivity {

    ArrayList<Objects.GeofenceItem> geos=new ArrayList<>();
    GeofenceItemAdapter geoAdapter;
    ListView lsvPlaces;
    static Context _context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
_context=this;
        lsvPlaces=(ListView)findViewById(R.id.lsvPlaces);
        GetGeofences();

        Button btnPlaces=(Button)findViewById(R.id.btnAddNewPlace);
        btnPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Places.this, MapPlacesActivity.class);
                myIntent.putExtra("lat",LocationListener.getLatitude());
                myIntent.putExtra("lng",LocationListener.getLongitude());
                myIntent.putExtra("radius",100);
                myIntent.putExtra("id", "0");
                myIntent.putExtra("name", "Area"+(geos.size()+1));
                Places.this.startActivity(myIntent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        GetGeofences();
    }

    public void GetGeofences() {
        geos.clear();
        DatabaseHelper dbh = new DatabaseHelper(this);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c;
        Objects.GeofenceItem geo;
        c = db.query(DatabaseContracts.Geogences.TABLE_NAME, null, "", null, "", "","", "");
        if (c.moveToFirst()) {
            do {
                geo=new Objects().new GeofenceItem(
                        c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_name)),
                        c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_ID)),
                        Double.valueOf(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_center)).split(",")[0].replace("lat/lng: (","")),
                        Double.valueOf(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_center)).split(",")[1].replace(")","")),
                        c.getDouble(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_radius)),
                        (c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_isOwner))>0)?true:false);
                if(  geo.name==null || geo.name=="" || geo.name==" ")
                    geo.name="Unnamed";
                geos.add(geo);
          }
            while (c.moveToNext());
        }
        c.close();
        db.close();
        dbh.close();
        if(geoAdapter==null)
            geoAdapter=new GeofenceItemAdapter(this,geos);
        lsvPlaces.setAdapter(geoAdapter);
    }

    public static void backWebServices (int ObjectCode, String Data) {
        if(ObjectCode==0){
            if((!Data.startsWith("-1")) && Data.length()>3){
                //                               Name~Points~radius~clientAreaCode~ownerDeviceCOde
                Circle circle=  Tools.GoogleMapObj.addCircle(new CircleOptions().center(new LatLng(Double.valueOf(Data.split("~")[1].split(",")[0]), Double.valueOf(Data.split("~")[1].split(",")[1]))).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(Float.valueOf(Data.split("~")[2])));

                ContentValues Val = new ContentValues();
                DatabaseHelper dbh = new DatabaseHelper(_context);
                SQLiteDatabase db = dbh.getWritableDatabase();
                Val.clear();
                Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, Data.split("~")[0]);
                Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, circle.getCenter().toString().replace("lat/lng: (", "").replace(")", ""));
                Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, circle.getRadius());
                Val.put(DatabaseContracts.Geogences.COLUMN_NAME_isOwner, 1);
                Val.put(DatabaseContracts.Geogences.COLUMN_OwnerCOde,Integer.valueOf(Data.split("~")[4]));
              int id=(int) db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val);

                //Add proximity alert to location manager
                try {
                    LocationListener.locationManager.addProximityAlert(
                            circle.getCenter().latitude,
                            circle.getCenter().longitude,
                            (float)circle.getRadius(),
                            -1,
                            PendingIntent.getBroadcast(LocationListener.mContext,id, new Intent("ir.tstracker.activity.proximity").putExtra("id",id), PendingIntent.FLAG_UPDATE_CURRENT));
                }
                catch (Exception er){

                }
                Toast.makeText(LocationListener.mContext, LocationListener.mContext.getResources().getString(R.string.geofenceadded), Toast.LENGTH_SHORT).show();
            }
            else if(Data.startsWith("-1")){
                Intent myIntent = new Intent(_context, PurchaseActivity.class);
                 myIntent.putExtra("msg","You don't have enough credit.");
               _context.startActivity(myIntent);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_places, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
