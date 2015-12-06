package ir.tsip.tracker.zarrintracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Places extends AppCompatActivity {

    ArrayList<Objects.GeofenceItem> geos=new ArrayList<>();
    GeofenceItemAdapter geoAdapter;
    ListView lsvPlaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

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
