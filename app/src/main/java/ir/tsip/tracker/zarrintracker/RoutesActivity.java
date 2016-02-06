package ir.tsip.tracker.zarrintracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RoutesActivity extends FragmentActivity {

    Polyline polylineToAdd;
    String origin, destination, waypoints;
    Runnable r;
    JSONObject jo;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button btnClearmarkers, btnFIndroutes;
    Persons person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_routes);
        setUpMapIfNeeded();
        if (mMap == null)
            return;
        mMap.setMyLocationEnabled(true);

        ImageButton ibtnHelp=(ImageButton)findViewById(R.id.ibtnHelp);
        ibtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RoutesActivity.this, HelpActivity.class);
                i.putExtra("index", 5);
                startActivity(i);
            }
        });

        btnFIndroutes = (Button) findViewById(R.id.btnFindRoute);
        btnClearmarkers = (Button) findViewById(R.id.btnClearmarkers);

        btnClearmarkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = 0;
                for (Marker m : markers) {
                    m.remove();
                }
                markers.clear();
                mMap.clear();
            }
        });
        btnFIndroutes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (markers.size() < 2) {
                    Toast.makeText(RoutesActivity.this, RoutesActivity.this.getResources().getString(R.string.nomarker), Toast.LENGTH_SHORT).show();
                    return;
                }
                origin = "origin=" + markers.get(0).getPosition().toString().replace("lat/lng: (", "").replace(")", "") + "&";
                destination = "destination=" + markers.get(markers.size() - 1).getPosition().toString().replace("lat/lng: (", "").replace(")", "");
                waypoints = "";
                if (markers.size() > 2) {
                    waypoints = "&waypoints=optimize:true|";
                    for (int j = 1; j < markers.size() - 1; j++)
                        waypoints += markers.get(j).getPosition().toString().replace("lat/lng: (", "").replace(")", "") + "|";
                    waypoints = waypoints.substring(0, waypoints.length() - 1);
                }
                if (polylineToAdd != null)
                    polylineToAdd.remove();
                Toast.makeText(RoutesActivity.this, RoutesActivity.this.getResources().getString(R.string.wait), Toast.LENGTH_LONG).show();

               RequestGoogle();
            }
        });

        HorizontalListView hlsvUsers = (HorizontalListView) findViewById(R.id.hlsvUsers);
        final ArrayList<Persons> persons = Persons.GetAll();
        ImageListAdapter adapter = new ImageListAdapter(this);
        adapter.UseDefaultClickListener = false;
        if (persons.size() > 0)
            for (Persons p : persons) {
                adapter.AddMarker(new Objects().new MarkerItem(p.ID, p.image, p.name, "", null));
            }
        hlsvUsers.setAdapter(adapter);

        hlsvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(RoutesActivity.this, RoutesActivity.this.getResources().getString(R.string.wait), Toast.LENGTH_LONG).show();
                person=new Persons();
                person.GetData(Integer.valueOf(view.getTag().toString()));
                if(person.lastLatLng==null || person.lastLatLng.isEmpty()){
                    Toast.makeText(RoutesActivity.this, RoutesActivity.this.getResources().getString(R.string.noMarkerData), Toast.LENGTH_LONG).show();
                    return;
                }
                if(LocationListener.getLongitude()==0 || LocationListener.getLatitude()==0) {
                    Toast.makeText(RoutesActivity.this, RoutesActivity.this.getResources().getString(R.string.gpsIsOff), Toast.LENGTH_SHORT).show();
                    return;
                }
                origin = "origin=" + LocationListener.getLatitude()+","+LocationListener.getLongitude() + "&";
                destination = "destination=" + person.lastLatLng;
waypoints="";
                mMap.clear();
                markers.clear();
                RequestGoogle();

            }
        });

    }

    private  void RequestGoogle(){
        Thread t = new Thread() {
            public void run() {

                try {
                    jo = Request(null, "https://maps.googleapis.com/maps/api/directions/json?" + origin + destination + waypoints);

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(r);
                } catch (Exception er) {
                    String msg = er.getMessage();
                }
            }
        };
        t.start();
        r = new Runnable() {
            @Override
            public void run() {
                try {
                    DrawRoutes(jo);

                } catch (Exception er) {
                    String msg = er.getMessage();
                }
            }
        };
    }

    private void DrawRoutes(JSONObject result) throws JSONException {
        JSONArray routes = result.getJSONArray("routes");
        PolylineOptions polyOptions = new PolylineOptions();
        long distanceForSegment;
        List<LatLng> lines = new ArrayList<LatLng>();

        for (int j = 0; j < routes.length(); j++) {
            distanceForSegment = routes.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
            JSONArray legs = routes.getJSONObject(j).getJSONArray("legs");

            for (int k = 0; k < legs.length(); k++) {
                JSONArray steps = legs.getJSONObject(k).getJSONArray("steps");
                for (int i = 0; i < steps.length(); i++) {
                    String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");

                    for (LatLng p : decodePolyline(polyline)) {
                        lines.add(p);
                    }
                }
            }
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(person.lastLatLng.split(",")[0]), Double.valueOf(person.lastLatLng.split(",")[1])))
                    .title(person.name)
                    .icon(BitmapDescriptorFactory.fromBitmap(Tools.drawCustomMarker(BitmapFactory.decodeResource( RoutesActivity.this.getResources(),R.drawable.red_marker),Tools.LoadImage(person.image,96),person.name))));


        }

        polylineToAdd = mMap.addPolyline(polyOptions.addAll(lines).width(3).color(Color.RED));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(polylineToAdd.getPoints().get(0), 15.0f));

    }

    /**
     * POLYLINE DECODER - http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     **/
    private List<LatLng> decodePolyline(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }

        return poly;
    }

    java.net.URL _url;
    java.net.HttpURLConnection _con;

    public JSONObject Request(JSONObject json, String address) throws java.io.IOException, org.json.JSONException {
        _url = new java.net.URL(address);
        _con = (java.net.HttpURLConnection) _url.openConnection();
        _con.setDoOutput(true);
        _con.setDoInput(true);
        _con.setRequestProperty("Content-Type", "application/json");

        if (json != null) {
            java.io.OutputStream out = new java.io.BufferedOutputStream(_con.getOutputStream());
            out.write(java.net.URLEncoder.encode(json.toString(), "UTF-8").getBytes());

            out.flush();
            out.close();
        }
        java.io.InputStream in = new java.io.BufferedInputStream(_con.getInputStream());
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(in));

        StringBuilder resultStr = new StringBuilder();
        String res;
        while ((res = reader.readLine()) != null) {
            resultStr.append(res);
        }
        reader.close();
        in.close();
        return new JSONObject(resultStr.toString());

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

    int i = 0;
    ArrayList<Marker> markers = new ArrayList<>();

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try {
                    if (i == 9) {
                        Toast.makeText(RoutesActivity.this, RoutesActivity.this.getResources().getString(R.string.cantaddmoremarkers), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    i++;
                    markers.add(mMap.addMarker(new MarkerOptions().position(latLng).title(String.valueOf(i))));

                } catch (Exception er) {

                }
            }
        });
    }
}
