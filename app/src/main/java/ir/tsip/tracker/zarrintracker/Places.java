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
        lsvPlaces=(ListView)findViewById(R.id.lsvPlaces);
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
                        c.getDouble(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_radius)));
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
                //                               Name~Points~radius~clientAreaCode
                Circle circle=  Tools.GoogleMapObj.addCircle(new CircleOptions().center(new LatLng(Double.valueOf(Data.split("~")[1].split(",")[0]), Double.valueOf(Data.split("~")[1].split(",")[1]))).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(Float.valueOf(Data.split("~")[2])));

                ContentValues Val = new ContentValues();
                DatabaseHelper dbh = new DatabaseHelper(_context);
                SQLiteDatabase db = dbh.getWritableDatabase();
                Val.clear();
                Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, Data.split("~")[0]);
                Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, circle.getCenter().toString().replace("lat/lng: (", "").replace(")", ""));
                Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, circle.getRadius());
              int id=(int) db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val);

                //Add proximity alert to location manager
                try {
                    LocationListener.locationManager.addProximityAlert(
                            circle.getCenter().latitude,
                            circle.getCenter().longitude,
                            (float)circle.getRadius(),
                            -1,
                            PendingIntent.getBroadcast(LocationListener.mContext,0, new Intent("ir.tstracker.activity.proximity").putExtra("id",id), 0));
                }
                catch (Exception er){

                }

            }
            else if(Data.startsWith("-1")){
//                DatabaseHelper dbh = new DatabaseHelper(_context);
//                SQLiteDatabase db = dbh.getWritableDatabase();
//                db.delete(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(Data.split("~")[3])});
//                db.close();
//                dbh.close();
//                db=null;
//                dbh=null;
//                try {
//                    LocationListener.locationManager.removeProximityAlert(  PendingIntent.getBroadcast(LocationListener.mContext, 0, new Intent("ir.tstracker.activity.proximity").putExtra("id",String.valueOf(Data.split("~")[3])), 0));
//                }
//                catch (Exception er){
//
//                }
                Intent myIntent = new Intent(_context, PurchaseActivity.class);
                myIntent.putExtra("msg","You can not create more than "+Data.split(",")[1]+" geofences.");
               _context.startActivity(myIntent);
            }
        }
        else if (ObjectCode == 4) {
            if (Data.length() > 1) {
                try {
                    //add new area to database
                    ContentValues Val = new ContentValues();
                    DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
                    SQLiteDatabase db = dbh.getWritableDatabase();

                    String[] geos = Data.split("\\|");
                    //                               Name~Points~radius~clientAreaCode|
                    for (String g : geos) {
                        Val.clear();
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, g.split("~")[0]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, g.split("~")[1]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, g.split("~")[2]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_ID, g.split("~")[3]);
                        db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val);
                        Circle circle=  Tools.GoogleMapObj.addCircle(new CircleOptions().center(new LatLng(Double.valueOf(Data.split("~")[1].split(",")[0]), Double.valueOf(Data.split("~")[1].split(",")[1]))).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(Float.valueOf(Data.split("~")[2])));

                    }
                    db.close();
                    dbh.close();
                    db = null;
                    dbh = null;
                }
                catch (Exception er){
                    String e="";
                }
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
