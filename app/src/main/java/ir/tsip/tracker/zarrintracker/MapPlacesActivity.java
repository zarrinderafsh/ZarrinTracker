package ir.tsip.tracker.zarrintracker;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;

public class MapPlacesActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
private double lat;
    private double lng;
    private double radius;
    Button btnDeacrease,btnIncrease,btnSave;
    ImageButton btnSearchLocation;
    EditText txtSearchPlace,txtCirleName;
    Circle circle;
    int id;
String name="Default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_places);
        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);


        btnDeacrease = (Button) findViewById(R.id.btnDecreaseArea);
        btnIncrease = (Button) findViewById(R.id.btnIncreaseArea);
        btnSave = (Button) findViewById(R.id.btnSaveNewPlace);
        btnSearchLocation = (ImageButton) findViewById(R.id.btnSearchPlace);
        txtSearchPlace = (EditText) findViewById(R.id.txtSearchPlaces);

        id = this.getIntent().getIntExtra("id", 0);
        lat = this.getIntent().getDoubleExtra("lat", 0);
        lng = this.getIntent().getDoubleExtra("lng", 0);
        radius = this.getIntent().getDoubleExtra("radius", 100);
        name=this.getIntent().getStringExtra("name");
     if (lat != 0 && lng != 0) {
            circle = mMap.addCircle(new CircleOptions().center(new LatLng(lat, lng)).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(radius));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationListener.CurrentLocation.getLatitude(), LocationListener.CurrentLocation.getLongitude()), 16.0f));


        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                circle.remove();
                circle = mMap.addCircle(new CircleOptions().center(latLng).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(radius));
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapPlacesActivity.this);
                builder.setTitle("Choose a name!");
                LayoutInflater inflate = MapPlacesActivity.this.getLayoutInflater();
                View view = inflate.inflate(R.layout.name_of_circle, null);
                txtCirleName=(EditText)view.findViewById(R.id.txtCircleName);
                txtCirleName.setText(name);
                builder.setView(view);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            //add new area to database
                            ContentValues Val = new ContentValues();
                            DatabaseHelper dbh = new DatabaseHelper(MapPlacesActivity.this);
                            SQLiteDatabase db = dbh.getWritableDatabase();
                            Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, circle.getCenter().toString());
                            Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, txtCirleName.getText().toString());
                            Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, String.valueOf(circle.getRadius()));

                            if (id == 0) {
                                db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val);

                                //Add proximity alert to location manager
                                LocationListener.locationManager.addProximityAlert(circle.getCenter().latitude, circle.getCenter().longitude, (float) circle.getRadius(), -1, PendingIntent.getBroadcast(MapPlacesActivity.this, 0, new Intent("ir.tsip.tracker.zarrintracker.ProximityAlert"), 0));
                            } else {
                                db.update(DatabaseContracts.Geogences.TABLE_NAME, Val, DatabaseContracts.Geogences.COLUMN_NAME_ID+"=?", new String[]{String.valueOf(id)});
                            }
                            //Send circle to the server for exposing to others
                            HashMap<String, String> params;
                            params = new HashMap<>();
                            params.put("message", circle.getCenter().toString() + "~" + String.valueOf(circle.getRadius()) + "~" + txtCirleName.getText().toString());
                            params.put("imei", Tools.GetImei(getApplicationContext()));
                            params.put("gpID", "-10");
                            WebServices W = new WebServices(getApplicationContext());
                            W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 0, params, "SetMessage");

                        } catch (Exception er) {

                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        btnDeacrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle.setRadius(circle.getRadius() - 100);
            }
        });
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                circle.setRadius(circle.getRadius() + 100);
            }
        });
        btnSearchLocation.setOnClickListener(new View.OnClickListener() {
            Address _address;

            @Override
            public void onClick(View v) {
                if (txtSearchPlace.getText().toString().length() > 2) {
                    Geocoder g = new Geocoder(MapPlacesActivity.this);
                    try {
                        _address = g.getFromLocationName(txtSearchPlace.getText().toString(), 1).get(0);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_address.getLatitude(), _address.getLongitude()), 14));
                    } catch (Exception er) {

                    }
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

    }
}
