package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Administrator on 11/1/2015.
 */
public class Tools {

    public static Boolean HasCredit = true, showrating = false, justadminsee = false, Mute = false, VisibleToOwnGroupMembers = true;
    private static Boolean AnswerLastGetMarkers = true;
    private static ConnectivityManager cm;
    private static NetworkInfo netInfo;
    public static String ptype = "0";

    public static boolean isOnline(Context context) {
        if (cm == null)
            cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = null;
        netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String GetImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = tm.getDeviceId();
        return IMEI;
    }


    public static Intent resultIntent;
    //    public static NotificationCompat.Builder mBuilder;
    public static TaskStackBuilder stackBuilder;
    public static PendingIntent resultPendingIntent;
    public static NotificationManager mNotificationManager;

    public static void Notificationm(Context context, String Title, String Details, String packge, int notifyNumber, int Drawable) {
        NotificationCompat.Builder mBuilder = null;
        if (mBuilder == null) {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(Drawable)
                            .setContentTitle(Title)
                            .setContentText(Details);
        } else {
            mBuilder.setContentTitle(Title);
            mBuilder.setContentText(Details);
        }
        if (resultIntent == null)
            resultIntent = new Intent(context, MainActivity.class);

        if (stackBuilder == null) {
            stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
        }

        if (resultPendingIntent == null) {
            resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
        }
        mBuilder.setContentIntent(resultPendingIntent);
        if (mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.notify(notifyNumber, mBuilder.build());
    }

    public static void HideNotificationm() {
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    public static Point GetDesktopSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static void ShareText(String pShareBody, Activity pActivity) {
        String shareBody = pShareBody;
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Send Invitation");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        pActivity.startActivity(Intent.createChooser(sharingIntent, "Share"));
    }

    public static void turnGPSOnOff(Activity activity) {
        Intent gpsOptionsIntent = new Intent(
                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivity(gpsOptionsIntent);
    }


    public static GoogleMap initGoogleMap(MapFragment MF) {
        if (MF == null)
            return null;
        GoogleMap googleMap = MF.getMap();


        // check if map is created successfully or not
        if (googleMap == null) {
        } else {

        }
        return googleMap;
    }

    public static int MyPersonId = 0;

    public static int FindMyPersonID() {
        MyPersonId = 0;
        WebServices W;
        HashMap<String, String> params;
        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = null;
        try {
            c = db.query(DatabaseContracts.Persons.TABLE_NAME, new String[]{DatabaseContracts.Persons.COLUMN_NAME_ID}, DatabaseContracts.Persons.COLUMN_is_me + "=1", null, "", "", "", "");
            if (c.moveToFirst()) {
                MyPersonId = Integer.valueOf(c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_ID)));
            }
            return MyPersonId;
        } finally {
            c.close();
            db.close();
            dbh.close();
            c = null;
            db = null;
            dbh = null;
        }
    }


    public static GoogleMap GoogleMapObj;
    public static Marker locationMarker;

    static boolean IsFirst = true;

    public static void setUpMap(GoogleMap googleMap, Context context, boolean isfirst) {

        if (isfirst || IsFirst && googleMap != null)
            GoogleMapObj = googleMap;
        if (markers == null)
            markers = new HashMap<Integer, Marker>();

        if (Tools.isOnline(context) && googleMap != null)
            Tools.getDevicesLocation(googleMap.getProjection().getVisibleRegion().latLngBounds.toString(), String.valueOf(googleMap.getCameraPosition().zoom), context, googleMap);

        if (googleMap == null)// || LocationListener.CurrentLocation == null)
            return;
        //Toast.makeText(MainActivity.Base, "from map", Toast.LENGTH_SHORT).show();

        if (isfirst || IsFirst) {
            if (LocationListener.CurrentLocation != null)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationListener.CurrentLocation.getLatitude(), LocationListener.CurrentLocation.getLongitude()), 16.0f));
            DrawCircles(context);
            GoogleMapObj.setMyLocationEnabled(true);
            IsFirst = false;
            GoogleMapObj.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    try {

                        WebServices w = new WebServices(MainActivity.Base);
                        WS.addQueue("ir.tsip.tracker.zarrintracker.Tools", 5, marker.getTitle().split(",")[2], "getInfoAndroid", 1);
                        w = null;
                        if (minfo == null)
                            minfo = marker;
                    }
                    catch (Exception er){
                        minfo=null;
                    }
                    finally {
                        return true;
                    }
                }
            });
            //set customInfoWIndo
            GoogleMapObj.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    View v = MainActivity.Base.getLayoutInflater().inflate(R.layout.custom_info_window, null);
                    ((TextView) v.findViewById(R.id.txtName)).setText(marker.getTitle().split(",")[0]);
                    ((TextView) v.findViewById(R.id.txtDate)).setText(marker.getTitle().split(",")[1]);

                    return v;
                }
            });
        }
    }

    private static Marker minfo;

    public static void DrawCircles(Context context) {
        if (Tools.GoogleMapObj == null)
            return;
        Tools.GoogleMapObj.clear();

        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] columns = {DatabaseContracts.Geogences.COLUMN_NAME_ID, DatabaseContracts.Geogences.COLUMN_NAME_radius, DatabaseContracts.Geogences.COLUMN_NAME_center, DatabaseContracts.Geogences.COLUMN_NAME_name};
        Cursor c;
        c = db.query(DatabaseContracts.Geogences.TABLE_NAME, columns, "", null, "", "", "");
        c.moveToFirst();
        String center;
        String meters;
        while (true && c.getCount() > 0) {
            try {
                center = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_center)).replace("lat/lng: (", "").replace(")", "");
                meters = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_radius));
                Tools.GoogleMapObj.addCircle(new CircleOptions().center(new LatLng(Double.valueOf(center.split(",")[0]), Double.valueOf(center.split(",")[1]))).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(Float.valueOf(meters)));
            } catch (Exception er) {
                er.getMessage();
            }
            if (c.isLast())
                break;
            c.moveToNext();
        }
        c.close();
        db.close();
        dbh.close();
    }

    private static ArrayList<Integer> proximities = new ArrayList<>();

    public static void setupGeofences(Context context) {

        if (Tools.GoogleMapObj == null)
            return;

        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] columns = {DatabaseContracts.Geogences.COLUMN_NAME_ID, DatabaseContracts.Geogences.COLUMN_NAME_radius, DatabaseContracts.Geogences.COLUMN_NAME_center, DatabaseContracts.Geogences.COLUMN_NAME_name};
        Cursor c;
        c = db.query(DatabaseContracts.Geogences.TABLE_NAME, columns, "", null, "", "", "");
        c.moveToFirst();
        String center;
        String meters;
        int id = 0;
        while (true && c.getCount() > 0) {
            try {
                center = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_center)).replace("lat/lng: (", "").replace(")", "");
                meters = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_radius));
                Tools.GoogleMapObj.addCircle(new CircleOptions().center(new LatLng(Double.valueOf(center.split(",")[0]), Double.valueOf(center.split(",")[1]))).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(Float.valueOf(meters)));
                id = Integer.valueOf(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_ID)));
                if (!proximities.contains(id)) {
                    LocationListener.locationManager.addProximityAlert(
                            Double.valueOf(center.split(",")[0]),
                            Double.valueOf(center.split(",")[1]),
                            Float.valueOf(meters),
                            -1,
                            PendingIntent.getBroadcast(LocationListener.mContext, id, new Intent("ir.tstracker.activity.proximity").putExtra("id", id), PendingIntent.FLAG_UPDATE_CURRENT));
                    proximities.add(id);
                }
            } catch (Exception er) {
                Log.e("Tools.GeofenceSetup", er.getMessage());
            }
            if (c.isLast())
                break;
            c.moveToNext();
        }
        DrawCircles(context);
        c.close();
        db.close();
        dbh.close();

    }


    //    private static RequestQueue queue;
    public static Map<Integer, Marker> markers;
    private static WebServices WS;
    public static ListView lsvMarkers;
    public static ImageListAdapter imgAdapter,imgAdapterTEMP;

    public static void backWebServicesError(int ObjectCode, String Data) {
        if (ObjectCode == 0) {//Markers
            AnswerLastGetMarkers = true;
        }
    }
    public  static HashMap<String,String> groupMembers=new HashMap<>();
public static String[] getGroupNames(Context _context) {
    DatabaseHelper dbh = new DatabaseHelper(_context);
    SQLiteDatabase db = dbh.getReadableDatabase();
    Cursor c = db.query(DatabaseContracts.Groups.TABLE_NAME, null, "", null, "", "", DatabaseContracts.Groups.COLUMN_NAME_LastTime + " DESC", "");
    String[] names = new String[c.getCount()];
    int count=0;
    if (c.moveToFirst()) {
        do {
names[count]=c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Name)).replace(';','\b');
            groupMembers.put(names[count],c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Members)));
                    count++;
        }
        while (c.moveToNext());
    }
    return names;
}
public static  Spinner spnrChoosenGroup;
    public static HashSet<String> MembersToShowOnMap=new HashSet<>();
    public static void CreateGroupChooserSpinner(){

        //spnrChoosenGroup
        spnrChoosenGroup =(Spinner)MainActivity.Base.findViewById(R.id.spnrChooseGroup);
        spnrChoosenGroup.setAdapter(new ArrayAdapter<String>(MainActivity.Base,R.layout.spinnerlayout,getGroupNames(MainActivity.Base)));

        spnrChoosenGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                MembersToShowOnMap.clear();
                for (String s : groupMembers.get(((TextView) view).getText()).split(";")) {
                    MembersToShowOnMap.add(s.split("-")[0]);
                }
                if (markers != null) {
                    int ID = -1;
                    if (imgAdapterTEMP == null)
                        imgAdapterTEMP = new ImageListAdapter(MainActivity.Base);
                    imgAdapterTEMP.items.clear();
                    for (Marker m : markers.values()) {
                        ID = Integer.valueOf(m.getTitle().split(",")[2]);
                        if (MembersToShowOnMap.contains(String.valueOf(ID))) {
                            m.setVisible(true);
                            imgAdapterTEMP.AddMarker(imgAdapter.GetItemByID(ID));
                        } else {
                            m.setVisible(false);
                        }
                    }
                lsvMarkers.setAdapter(imgAdapterTEMP);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public static void backWebServices(int ObjectCode, String Data) {

        if (ObjectCode == 0) {//Markers
            AnswerLastGetMarkers = true;
            try {

                if (MainActivity.Base == null)
                    return;
                ;
                if (imgAdapter == null) {
                    imgAdapter = new ImageListAdapter(MainActivity.Base);
                }
                if (markers.size() == 0)
                    imgAdapter.Clear();
                JSONObject jo = new JSONObject(Data);
                Marker m;
                String lat, lng;
                JSONArray ja = jo.getJSONArray("ChangingMarkers");
                for (int i = 0; i < ja.length(); i++) {
                    final int id = Integer.valueOf(ja.getJSONObject(i).getString("ID").toString());
                    //load marker if already exists
                    m = markers.get(id);
                    lat = ja.getJSONObject(i).getJSONObject("Location").getString("X");
                    lng = ja.getJSONObject(i).getJSONObject("Location").getString("Y");
                    //load person from database
                    Persons p = new Persons();
                    if (!p.GetData(Integer.valueOf(ja.getJSONObject(i).getString("PCode")))) {
                        p.ID = Integer.valueOf(ja.getJSONObject(i).getString("PCode"));
                        p.name = ja.getJSONObject(i).getString("Title").split(",")[0];
                        p.isme = false;
                        p.Save();
                    } else {
                        p.ID = Integer.valueOf(ja.getJSONObject(i).getString("PCode"));
                        p.name = ja.getJSONObject(i).getString("Title").split(",")[0];
                        p.isme = false;
                        p.lastLatLng = lat + "," + lng;
                        p.update();
                    }
                    if (p.image == null)
                        p.GetImageFromServer();
                    if (lsvMarkers == null)
                        lsvMarkers = (ListView) MainActivity.Base.findViewById(R.id.lsvMarkers);

                    if (m == null) {
                        Bitmap PersonImage = drawCustomMarker(BitmapFactory.decodeResource(MainActivity.Base.getResources(), R.drawable.redmarker), LoadImage(p.image, 96), ja.getJSONObject(i).getString("Title").split(",")[1]);
                        markers.put(id,
                                GoogleMapObj.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                                        .title(ja.getJSONObject(i).getString("Title") + "," + id)
                                        .icon(BitmapDescriptorFactory.fromBitmap(PersonImage))));
                        imgAdapter.AddMarker(new Objects().new MarkerItem(id,
                                LoadImage(p.image, 96),
                                ja.getJSONObject(i).getString("Title").split(",")[0],
                                "",
                                null));

                    } else {
                        m.setPosition(new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                        m.setTitle(ja.getJSONObject(i).getString("Title") + "," + id);
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(drawCustomMarker(BitmapFactory.decodeResource(MainActivity.Base.getResources(), R.drawable.redmarker), LoadImage(p.image, 96), ja.getJSONObject(i).getString("Title").split(",")[1])));
                        imgAdapter.GetItemByID(id)._image = LoadImage(p.image, 96);
                        imgAdapter.GetItemByID(id)._name = p.name;
                    }//Add icon for each marker at bottom of map, and when click on it, go to marker location

                }
                if (markers.size() > 1)
                    showrating = true;
//                imgAdapter.notifyDataSetChanged();
                if(spnrChoosenGroup==null) {
                    MainActivity.Base.webView.setVisibility(View.INVISIBLE);
                    CreateGroupChooserSpinner();
                    spnrChoosenGroup.setSelection(0,true);
                }
            } catch (Exception er) {
                er.getMessage();
            }
        } else if (ObjectCode == 5) {
            if (minfo != null) {
                minfo.setTitle(Data);
                minfo.showInfoWindow();
                minfo = null;
            }
        }
    }


    public static Bitmap drawCustomMarker(Bitmap firstImage, Bitmap secondImage, String text) {
        if (firstImage == null || secondImage == null)
            return null;
        Bitmap b = Bitmap.createBitmap(170, 180, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        c.drawBitmap(Bitmap.createScaledBitmap(firstImage, 170, 180, true), 0, 0, null);
        int Left = 39;
        int Top = 19;
        c.drawBitmap(secondImage, new Rect(0, 0, 96, 96), new Rect(Left, Top, Left + 96, Top + 96), null);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setTextSize(20f);
        c.drawText(text, Left + 19, Top + 96 + 26, p);
        return b;
    }

    public static void getDevicesLocation(String bounds, String zoom, final Context context, final GoogleMap gmap) {
        if (AnswerLastGetMarkers && Tools.isOnline(context)) {
            AnswerLastGetMarkers = false;
            bounds = bounds.replace("LatLngBounds{southwest=lat/lng: ", "(");
            bounds = bounds.replace("northeast=lat/lng: ", "");
            bounds = bounds.replace("}", ")");
            HashMap<String, String> params = new HashMap<>();
            params.put("bounds", bounds);
            //Zoom = zoomlevel,imei
            String zoomAndImei = new String(zoom + "," + Tools.GetImei(context));
            params.put("zoom", zoomAndImei);
            if (WS == null)
                WS = new WebServices(context);
//((-71.71494382435657,-72.32145845890045), (71.71494382435657,72.32138872146606))
            //2.0,000000000000000
            WS.addQueue("ir.tsip.tracker.zarrintracker.Tools", 0, params, "GetMarkers", 1);
        }
    }

    public static float getBatteryLevel(Context activate) {
        Intent batteryIntent = activate.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        try {
            if (bitmap == null)
                return null;
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
            Bitmap bTemp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);
            return stream.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap getBitmapFromByte(byte[] bitmapByte) {
        if (bitmapByte == null)
            return null;
        try {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeByteArray(bitmapByte, 0, bitmapByte.length);
            return bitmap;
        } catch (Exception e) {

        }
        return null;
    }

    public static void PlayAlert(Context c) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(c.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap BorderImage(Bitmap Image, int Weight, int BorderColor) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(Image.getWidth() + Weight * 2, Image.getHeight() + Weight * 2, Image.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(BorderColor);
        canvas.drawBitmap(Image, Weight, Weight, null);
        return bmpWithBorder;
    }

    public static Bitmap LoadImage(Bitmap b, int Radious) {
        if (b == null)
            b = BitmapFactory.decodeResource(MainActivity.Base.getResources(), R.drawable.sample_user);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 * 32];
        if (Radious > 0)
            b = CircleImage.getRoundedRectBitmap(
                    Bitmap.createScaledBitmap(b, Radious, Radious, true), Radious);
        return b;
    }

    public static void LoadImage(ImageView iv, byte[] imageAsBytes, int Radious) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 * 32];

        Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, options);
        if (Radious > 0)
            bm = CircleImage.getRoundedRectBitmap(Bitmap.createScaledBitmap(bm, Radious, Radious, true), Radious);
        iv.setImageBitmap(bm);
    }

    public static Bitmap LoadImage(byte[] imageAsBytes, int Radious) {
        if (imageAsBytes == null)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 * 32];

        Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, options);
        if (Radious > 0)
            bm = CircleImage.getRoundedRectBitmap(Bitmap.createScaledBitmap(bm, Radious, Radious, true), Radious);
        return bm;
    }

    public static String HashMapToString(HashMap<String, String> Data) {
        String Ret = "";
        Iterator myVeryOwnIterator = Data.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            String value = (String) Data.get(key);
            Ret += key + String.valueOf((char) 26) + value + String.valueOf((char) 25);
        }
        return Ret;
    }

    public static HashMap<String, String> StringToHashMap(String Data) {
        HashMap<String, String> Ret = new HashMap<>();
        String[] Splt = Data.split(String.valueOf((char) 25));
        for (String S : Splt) {
            if (S.length() > 0) {
                String[] b = S.split(String.valueOf((char) 26));
                if (b.length == 2)
                    Ret.put(b[0], b[1]);
            }
        }
        return Ret;
    }

    public static void setTitleColor(Activity context) {
        context.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
    }

    public static void SetLocal() {
        Locale locale = new Locale(Tools.getLocale(MainActivity.Base));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        MainActivity.Base.getResources().updateConfiguration(config,
                MainActivity.Base.getResources().getDisplayMetrics());

    }

    public static String getLocale(Context context) {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();

        String locale = "fa";
        Cursor c = null;
        try {
            c = db.query(DatabaseContracts.Settings.TABLE_NAME,
                    null,
                    "",
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {

                locale = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_locale));


            }
        } catch (Exception e) {
        } finally {
            c.close();
            db.close();
            dbh.close();
        }
        return locale;
    }

    public static void SetBoleanColumn(String table, String column, Boolean value) {

        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            ContentValues V = new ContentValues();
            V.put(column, value ? 1 : 0);
            db.update(table, V, "", null);
        } catch (Exception e) {
            String k = e.getMessage();
        } finally {
            db.close();
            dbh.close();
        }
    }

    public static Boolean getBoleanColumn(Context context, String table, String column) {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.query(table,
                    null,
                    "",
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {

                return (c.getInt(c.getColumnIndexOrThrow(column)) > 0) ? true : false;


            }
        } catch (Exception e) {
        } finally {
            c.close();
            db.close();
            dbh.close();
        }
        return false;
    }


    public static void SetRate(Boolean rate) {
        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            ContentValues V = new ContentValues();
            V.put(DatabaseContracts.Settings.COlumn_Rate, rate ? 1 : 0);
            db.update(DatabaseContracts.Settings.TABLE_NAME, V, "", null);
        } catch (Exception e) {
            String k = e.getMessage();
        } finally {
            db.close();
            dbh.close();
        }
    }

    public static Boolean getRate(Context context) {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.query(DatabaseContracts.Settings.TABLE_NAME,
                    null,
                    "",
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {

                return (c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COlumn_Rate)) > 0) ? true : false;


            }
        } catch (Exception e) {
        } finally {
            c.close();
            db.close();
            dbh.close();
        }
        return false;
    }

    public static void SetMute(Boolean mut) {
        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            ContentValues V = new ContentValues();
            V.put(DatabaseContracts.Settings.COLUMN_mute, mut ? 1 : 0);
            db.update(DatabaseContracts.Settings.TABLE_NAME, V, "", null);
        } catch (Exception e) {
        } finally {
            db.close();
            dbh.close();
        }
    }

    public static Boolean getMute(Context context) {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();

        Cursor c = null;
        try {
            c = db.query(DatabaseContracts.Settings.TABLE_NAME,
                    null,
                    "",
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {

                return (c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_mute)) > 0) ? true : false;


            }
        } catch (Exception e) {
        } finally {
            c.close();
            db.close();
            dbh.close();
        }
        return false;
    }


    public static void SetLocale(String Locale) {
        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            ContentValues V = new ContentValues();
            V.put(DatabaseContracts.Settings.COLUMN_locale, Locale);
            db.update(DatabaseContracts.Settings.TABLE_NAME, V, "", null);
        } catch (Exception e) {
        } finally {
            db.close();
            dbh.close();
        }
    }

    public static byte[] encrypt(byte[] key, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static byte[] decrypt(byte[] key, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static byte[] keyfromdb(Context context) {
        //get key from db
        String key = "";
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.query(DatabaseContracts.Settings.TABLE_NAME, null, "", null, "", "", "");
        if (c.moveToFirst()) {
            key = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Settings.COLUMN_NAME_key));
        }
        c.close();
        db.close();
        dbh.close();
        c = null;
        db = null;
        dbh = null;

        try {
            byte[] en = Tools.encrypt(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6}, key.getBytes());
            return Tools.decrypt(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6}, en);
        } catch (Exception er) {
            return new byte[]{};
        }
    }

    public static byte[] getkey(byte[] k) {
        try {
            byte[] keyStart = k;
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(keyStart);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            return skey.getEncoded();
        } catch (Exception er) {
            return new byte[]{};
        }
    }


}

