package ir.tsip.tracker.zarrintracker;

import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import ir.tsip.tracker.zarrintracker.persindatepicker.util.PersianCalendar;

public class OfflineMap extends FragmentActivity {

    static private GoogleMap mMap; // Might be null if Google Play services APK is not available.
private HorizontalListView hlsvUsers;
     private PersianDatePicker dtpDate;
    private TimePicker tmpFromTime;
    static WebServices w;
     final SimpleDateFormat smplDate=new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US);


    private static int startrow,count;
    private static  String startdate,enddate;
    static SeekBar seekbar;
static  Persons person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);


        setUpMapIfNeeded();



        InitializeWidgets();
    }


    private  void InitializeWidgets(){
        hlsvUsers=(HorizontalListView)findViewById(R.id.hlsvUsers);
        final ArrayList<Persons> persons=Persons.GetAll();
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


          hlsvUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  mMap.clear();
                  latslngs.clear();
                  seekbar.setEnabled(false);
                  seekbar.setProgress(0);
                  person=new Persons();
                  person.GetData(Integer.valueOf(view.getTag().toString()));
                  startrow = 0;
                  count = 20;
                  startdate = smplDate.format(dtpDate.getDisplayDate()) + " " + String.valueOf(tmpFromTime.getCurrentHour()) + ":" + String.valueOf(tmpFromTime.getCurrentMinute()) + ":00";
                  enddate = smplDate.format(dtpDate.getDisplayDate()) + " " + String.valueOf(tmpFromTime.getCurrentHour() + 1) + ":" + String.valueOf(tmpFromTime.getCurrentMinute()) + ":00";
                  RequestServer();
                  Toast.makeText(OfflineMap.this, OfflineMap.this.getResources().getString(R.string.wait), Toast.LENGTH_LONG).show();

              }
          });
        if(latslngs==null)
            latslngs=new ArrayList<>();

seekbar=(SeekBar)this.findViewById(R.id.seekBar);
        seekbar.setEnabled(false);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (progress >= latslngs.size())
                    return;
                marker.setPosition(latslngs.get(progress));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latslngs.get(progress), 14.0f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final TextView txthelp=((TextView) findViewById(R.id.txthelp));
        txthelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txthelp.setVisibility(View.GONE);
            }
        });
    }
private static Marker marker;

    private  static  void RequestServer() {
        w = new WebServices(MainActivity.Base);
        final HashMap<String, String> params = new HashMap<>();
        params.clear();
        params.put("pcode",String.valueOf( person.ID));
        params.put("startdate", startdate);
        params.put("enddate", enddate);
        params.put("startrow", String.valueOf(startrow));
        params.put("lastrow", String.valueOf(startrow + count));
        w.addQueue("ir.tsip.tracker.zarrintracker.OfflineMap", 0, params, "GetDirectionForAndroid", 1);
        w = null;
    }

    private static ArrayList<LatLng> latslngs;

    public static void backWebServicesError (int ObjectCode, String ClassName) {
        Toast.makeText(MainActivity.Base, MainActivity.Base.getResources().getString(R.string.ServerError), Toast.LENGTH_LONG).show();
    }

    public static void backWebServices (int ObjectCode, String Data) {
        if (ObjectCode == 0) {
            try {
                if ((Data == null || Data.equals("null"))&& startrow!=0) {
                    return;
                }
                if (Data == null || Data.equals("null")) {
                    Toast.makeText(MainActivity.Base, MainActivity.Base.getResources().getString(R.string.noMarkerData), Toast.LENGTH_LONG).show();
                return;
                }

                JSONObject location;
                JSONArray jo = new JSONArray(Data);

                for (int i = 0; i < jo.length(); i++) {
                    location = jo.getJSONObject(i).getJSONObject("Location");
                   latslngs.add(new LatLng(location.getDouble("X"), location.getDouble("Y")));
                }
                if(startrow==0) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latslngs.get(0), 14.0f));
                   marker= mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(Tools.drawCustomMarker(BitmapFactory.decodeResource(MainActivity.Base.getResources(), R.drawable.redmarker), Tools.LoadImage(person.image, 96)))).position(latslngs.get(0)));
                }
                startrow+=count;
                if(!seekbar.isEnabled())
                seekbar.setEnabled(true);
                seekbar.setMax(startrow);
                RequestServer();
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
