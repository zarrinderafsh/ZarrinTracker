package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.media.Image;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.renderscript.Sampler;
import android.service.media.MediaBrowserService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.drive.internal.QueryRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 11/1/2015.
 */
public class Tools {

public static  Boolean HasCredit=true;


    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String GetImei(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMEI = tm.getDeviceId();
        return IMEI;
    }


    public static Intent resultIntent;
    public static NotificationCompat.Builder mBuilder;
    public static TaskStackBuilder stackBuilder;
    public static PendingIntent resultPendingIntent;
    public static NotificationManager mNotificationManager;

    public static void Notificationm(Context context, String Title, String Details, String packge) {
        if (mBuilder == null) {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notification_icon_anim)
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
        mNotificationManager.notify(0, mBuilder.build());
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

    public static  int MyPersonId=0;
    public static int FindMyPersonID(){
        MyPersonId=0;
        WebServices W;
        HashMap<String, String> params;
        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c;
        c = db.query(DatabaseContracts.Persons.TABLE_NAME,new String[]{ DatabaseContracts.Persons.COLUMN_NAME_ID},DatabaseContracts.Persons.COLUMN_is_me +"=1", null, "", "", "", "");
        if (c.moveToFirst()) {
            MyPersonId=Integer.valueOf(c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_ID)));
        }
        return  MyPersonId;
    }


    public static GoogleMap GoogleMapObj;
    public static Marker locationMarker;

static boolean IsFirst=true;
    public static void setUpMap(GoogleMap googleMap, Context context,boolean isfirst) {
        if (googleMap == null || LocationListener.CurrentLocation == null)
            return;
        else if (isfirst || IsFirst)
            GoogleMapObj = googleMap;
        //Toast.makeText(MainActivity.Base, "from map", Toast.LENGTH_SHORT).show();
        if (markers == null)
            markers = new HashMap<Integer, Marker>();
        if (Tools.isOnline(context))
            getDevicesLocation(googleMap.getProjection().getVisibleRegion().latLngBounds.toString(), String.valueOf(googleMap.getCameraPosition().zoom), context, googleMap);
        else {
        }
        if (isfirst || IsFirst) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationListener.CurrentLocation.getLatitude(), LocationListener.CurrentLocation.getLongitude()), 16.0f));

                Tools.setupGeofences(MainActivity.Base);
            Tools.DrawCircles(MainActivity.Base);
            GoogleMapObj.setMyLocationEnabled(true);
            IsFirst=false;
        }
    }

    public static void DrawCircles(Context context){
        if(Tools.GoogleMapObj== null)
            return;
        Tools.GoogleMapObj.clear();

        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] columns = {DatabaseContracts.Geogences.COLUMN_NAME_ID, DatabaseContracts.Geogences.COLUMN_NAME_radius,DatabaseContracts.Geogences.COLUMN_NAME_center,DatabaseContracts.Geogences.COLUMN_NAME_name};
        Cursor c;
        c = db.query(DatabaseContracts.Geogences.TABLE_NAME, columns, "", null, "", "", "");
        c.moveToFirst();
        String center;
        String meters;
        while(true && c.getCount()>0) {
            try {
                center = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_center)).replace("lat/lng: (", "").replace(")", "");
                meters = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_radius));
                Tools.GoogleMapObj.addCircle(new CircleOptions().center(new LatLng(Double.valueOf(center.split(",")[0]), Double.valueOf(center.split(",")[1]))).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(Float.valueOf(meters)));
          } catch (Exception er) {

            }
            if (c.isLast())
                break;
            c.moveToNext();
        }
        c.close();
        db.close();
        dbh.close();
    }

    public static boolean proximityCreated=false;
    public static void setupGeofences(Context context){

        if(Tools.GoogleMapObj== null || proximityCreated)
            return;
        Tools.GoogleMapObj.clear();

        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] columns = {DatabaseContracts.Geogences.COLUMN_NAME_ID, DatabaseContracts.Geogences.COLUMN_NAME_radius,DatabaseContracts.Geogences.COLUMN_NAME_center,DatabaseContracts.Geogences.COLUMN_NAME_name};
        Cursor c;
        c = db.query(DatabaseContracts.Geogences.TABLE_NAME, columns, "", null, "", "", "");
        c.moveToFirst();
        String center;
        String meters;
        int id=0;
        while(true && c.getCount()>0) {
            try {
                center = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_center)).replace("lat/lng: (", "").replace(")", "");
                meters = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_radius));
                Tools.GoogleMapObj.addCircle(new CircleOptions().center(new LatLng(Double.valueOf(center.split(",")[0]), Double.valueOf(center.split(",")[1]))).fillColor(Color.TRANSPARENT).strokeColor(Color.RED).strokeWidth(5).radius(Float.valueOf(meters)));
                id=Integer.valueOf(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Geogences.COLUMN_NAME_ID)));
                        LocationListener.locationManager.addProximityAlert(
                                Double.valueOf(center.split(",")[0]),
                                Double.valueOf(center.split(",")[1]),
                                Float.valueOf(meters),
                                -1,
                                PendingIntent.getBroadcast(LocationListener.mContext, id, new Intent("ir.tstracker.activity.proximity").putExtra("id", id), 0));
            } catch (Exception er) {
Log.e("Tools.GeofenceSetup",er.getMessage());
            }
            if (c.isLast())
                break;
            c.moveToNext();
        }
        proximityCreated=true;
        c.close();
        db.close();
        dbh.close();

    }



    //    private static RequestQueue queue;
    public static Map<Integer, Marker> markers;
    private static WebServices WS;
    public static HorizontalListView lsvMarkers;
    private static ImageListAdapter imgAdapter;
    private  static ArrayList<Objects.MarkerItem> mlist;
    public static void backWebServices(int ObjectCode, String Data) {
        if(!Tools.HasCredit)
        return;
        if (ObjectCode == 0) {//Markers
            try {
                if(MainActivity.Base==null)
                    return;;
                mlist=new ArrayList<>();

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
                        p.name = ja.getJSONObject(i).getString("Title");
                        p.isme = false;
                        p.Save();
                    } else {
                        p.ID = Integer.valueOf(ja.getJSONObject(i).getString("PCode"));
                        p.name = ja.getJSONObject(i).getString("Title");
                        p.isme = false;
                        p.update();
                    }
                    if (p.image == null)
                        p.GetImageFromServer();
                    if (lsvMarkers == null)
                        lsvMarkers = (HorizontalListView) MainActivity.Base.findViewById(R.id.lsvMarkers);

                    if (m == null) {
                        markers.put(id,
                                GoogleMapObj.addMarker(new MarkerOptions().position(
                                        new LatLng(Double.valueOf(lat), Double.valueOf(lng))).title(ja.getJSONObject(i).getString("Title")).icon(BitmapDescriptorFactory.fromBitmap(LoadImage(p.image, 96)))));


                    } else {
                        m.setPosition(new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                        m.setTitle(ja.getJSONObject(i).getString("Title"));
                        m.setIcon(BitmapDescriptorFactory.fromBitmap(LoadImage(p.image, 96)));
//                         imgAdapter.GetItemByID(id)._image=LoadImage(p.image, 96);
//                        imgAdapter.GetItemByID(id)._name=p.name;
                    }//Add icon for each marker at bottom of map, and when click on it, go to marker location
                    mlist.add(new Objects().new MarkerItem(id,
                            LoadImage(p.image, 96),
                            ja.getJSONObject(i).getString("Title"),
                            "",
                            null));
                }
                imgAdapter = new ImageListAdapter(MainActivity.Base,mlist);
                lsvMarkers.setAdapter(imgAdapter);
//                imgAdapter.notifyDataSetChanged();
            } catch (Exception er) {
er.getMessage();
            }
        }
    }

    public static void getDevicesLocation(String bounds, String zoom, final Context context, final GoogleMap gmap) {
        bounds = bounds.replace("LatLngBounds{southwest=lat/lng: ", "(");
        bounds = bounds.replace("northeast=lat/lng: ", "");
        bounds = bounds.replace("}", ")");
        HashMap<String, String> params = new HashMap<>();
        params.put("bounds", bounds);
        //Zoom = zoomlevel,imei
        String zoomAndImei=new String (zoom + "," + Tools.GetImei(context));
        params.put("zoom",zoomAndImei);
        if (WS == null)
            WS = new WebServices(context);

        WS.addQueue("ir.tsip.tracker.zarrintracker.Tools", 0, params, "GetMarkers");

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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            Bitmap bTemp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);
            return stream.toByteArray();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static Bitmap getBitmapFromByte(byte[] bitmapByte) {
        if (bitmapByte == null)
            return null;
        try {
            Bitmap bitmap;
            bitmap = BitmapFactory.decodeByteArray(bitmapByte,0,bitmapByte.length);
            return bitmap;
        }
        catch (Exception e)
        {

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
    public static Bitmap LoadImage(Bitmap b, int Radious) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 * 32];
if (Radious > 0)
            b = CircleImage.getRoundedRectBitmap(b, Radious);
        return  b;
    }
    public static void LoadImage(ImageView iv, byte[] imageAsBytes, int Radious) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 * 32];

        Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, options);
        if (Radious > 0)
            bm = CircleImage.getRoundedRectBitmap(bm, Radious);
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
            bm = CircleImage.getRoundedRectBitmap(bm, Radious);
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


}

