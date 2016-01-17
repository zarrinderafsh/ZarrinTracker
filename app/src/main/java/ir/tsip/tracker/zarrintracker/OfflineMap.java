package ir.tsip.tracker.zarrintracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import ir.tsip.tracker.zarrintracker.persindatepicker.util.PersianCalendar;

public class OfflineMap extends FragmentActivity {

    static private GoogleMap mMap; // Might be null if Google Play services APK is not available.
private HorizontalListView hlsvUsers;
    private PersianDatePicker dtpDate;
    private TimePicker tmpFromTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);


        setUpMapIfNeeded();



        InitializeWidgets();
    }


    private  void InitializeWidgets(){
        hlsvUsers=(HorizontalListView)findViewById(R.id.hlsvUsers);
        ArrayList<Persons> persons=Persons.GetAll();
        ImageListAdapter adapter=new ImageListAdapter(this);
        adapter.UseDefaultClickListener=false;
        if(persons.size()>0 )
            for (Persons p:persons) {
                adapter.AddMarker(new Objects().new MarkerItem(p.ID,p.image,p.name,"",null));
            }
        else
            Toast.makeText(OfflineMap.this, OfflineMap.this.getResources().getString(R.string.noMarkerData), Toast.LENGTH_LONG).show();

        hlsvUsers.setAdapter(adapter);

        dtpDate=(PersianDatePicker)findViewById(R.id.dtpCalendar);
        PersianCalendar pc=new PersianCalendar();
        pc.setPersianDate(pc.getPersianYear(), pc.getPersianMonth(), pc.getPersianDay());
        dtpDate.setDisplayPersianDate(pc);

        Calendar c=Calendar.getInstance();
        tmpFromTime=(TimePicker)findViewById(R.id.tmpFromTime);
        tmpFromTime.setCurrentHour(c.get(Calendar.HOUR_OF_DAY) - 1);
        tmpFromTime.setCurrentMinute(c.get(Calendar.MINUTE));
        tmpFromTime.setIs24HourView(true);


        final WebServices w=new WebServices(OfflineMap.this);
        final HashMap<String,String> params=new HashMap<>();
        final SimpleDateFormat smplDate=new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);
        hlsvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              mMap.clear();
                params.clear();
                params.put("pcode", view.getTag().toString());
                params.put("startdate", smplDate.format(dtpDate.getDisplayDate()) + " " + String.valueOf(tmpFromTime.getCurrentHour()) + ":" + String.valueOf(tmpFromTime.getCurrentMinute())+":00");
                params.put("enddate", smplDate.format(dtpDate.getDisplayDate()) + " " + String.valueOf(tmpFromTime.getCurrentHour() + 1) + ":" + String.valueOf(tmpFromTime.getCurrentMinute())+":00");
                w.addQueue("ir.tsip.tracker.zarrintracker.OfflineMap", 0, params, "GetDirectionForAndroid");
                Toast.makeText(OfflineMap.this, OfflineMap.this.getResources().getString(R.string.wait), Toast.LENGTH_LONG).show();
            }
        });


    }


    public static void backWebServices (int ObjectCode, String Data) {
        if (ObjectCode == 0) {
            try {
                if (Data == null || Data.equals("null")) {
                    Toast.makeText(MainActivity.Base, MainActivity.Base.getResources().getString(R.string.noMarkerData), Toast.LENGTH_LONG).show();
                return;
                }
                JSONObject location;
                PolygonOptions polyOpt= new PolygonOptions();
                ArrayList<PolygonOptions> pol=new ArrayList<>();
                LatLng prevLoc = null, curLoc;
                int blue = 255, red = 0, green = 150;
                JSONArray jo = new JSONArray(Data);
                  Toast.makeText(MainActivity.Base, MainActivity.Base.getResources().getString(R.string.wait), Toast.LENGTH_LONG).show();

                for (int i = 0; i < jo.length(); i++) {
                    location = jo.getJSONObject(i).getJSONObject("Location");
                    curLoc = new LatLng(location.getDouble("X"), location.getDouble("Y"));
                   // if (prevLoc != null) {
                      //  polyOpt = new PolygonOptions();
                       // polyOpt.add(prevLoc).add(curLoc);
                    polyOpt.add(curLoc);
                        polyOpt.strokeWidth(2);
                        polyOpt.strokeColor(Color.rgb(red, green, blue));
                        // mMap.addMarker(new MarkerOptions().position().title("Marker"));
//                        red += 1;
//                        blue -= 1;
//                        green -= 1;
//                        if (red >= 255)
//                            red = 255;
//                        if (blue <= 0)
//                            blue = 0;
//                        if (green <= 0)
//                            green = 0;
//                        pol.add(polyOpt);
                   // }
                    if (i == 0)
                       mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curLoc, 14.0f));
                 //   prevLoc = curLoc;
                }
                mMap.addPolygon(polyOpt);
//                for (PolygonOptions p:pol) {
//
//                    mMap.addPolygon(p);
//                }
                pol=null;
            } catch (Exception er) {
                String s = er.getMessage();
            }
        }
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
