package ir.tsip.tracker.zarrintracker.util;

import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import ir.tsip.tracker.zarrintracker.LocationListener;
import ir.tsip.tracker.zarrintracker.R;

public class MapActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        initilizeMap();
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);

            return rootView;
        }
    }

    private GoogleMap googleMap;

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
            else
            {
                setUpMap();
            }
        }
    }


    private void setUpMap() {

//        if (Tools.currentLocation != null) {
//            Location currentLocation=null;
//            if (isGPSEnabled) {
//                if (LocationListener.locationManager != null) {
//                    GPS_location = locationManager
//                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                }
//            }
//
//            if (isNetworkEnabled) {
//                if (locationManager != null) {
//                    Network_location = locationManager
//                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                }
//            LatLng ll = new LatLng(Tools.currentLocation.getLatitude(), Tools.currentLocation.getLongitude());
//            mMap.addMarker(new MarkerOptions().position(ll).title("موقعیت من"));
//
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0f));
//        }
    }
}
