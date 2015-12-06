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
import android.content.pm.ResolveInfo;
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
import android.provider.Settings;
import android.renderscript.Sampler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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


public static  GoogleMap GoogleMapObj;

static Boolean isFirst=true;
    public static void setUpMap(GoogleMap googleMap, Context context) {
        if (googleMap == null || LocationListener.CurrentLocation == null)
            return;
        GoogleMapObj=googleMap;
        GoogleMapObj.setMyLocationEnabled(true);

        if (markers == null)
            markers = new HashMap<Integer, Marker>();
        if (Tools.isOnline(context))
            getDevicesLocation(googleMap.getProjection().getVisibleRegion().latLngBounds.toString(), String.valueOf(googleMap.getCameraPosition().zoom), context, googleMap);
        else {
      }
        if(isFirst) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LocationListener.CurrentLocation.getLatitude(), LocationListener.CurrentLocation.getLongitude()), 16.0f));
            isFirst = false;
        }
    }

//    private static RequestQueue queue;
    public static Map<Integer, Marker> markers;
private  static  WebServices WS;
    public static void backWebServices(int ObjectCode, String Data) {
        if (ObjectCode == 0) {//Markers
            try {
                JSONObject jo = new JSONObject(Data);
                Marker m;
                String lat, lng;
                JSONArray ja = jo.getJSONArray("ChangingMarkers");
                for (int i = 0; i < ja.length(); i++) {
                    m = markers.get(Integer.valueOf(ja.getJSONObject(i).getString("ID")));
                    lat = ja.getJSONObject(i).getJSONObject("Location").getString("X");
                    lng = ja.getJSONObject(i).getJSONObject("Location").getString("Y");
                    if (m == null) {

                        markers.put(Integer.valueOf(ja.getJSONObject(i).getString("ID").toString()),
                                GoogleMapObj.addMarker(new MarkerOptions().position(
                                        new LatLng(Double.valueOf(lat), Double.valueOf(lng))).title(ja.getJSONObject(i).getString("Title"))));
                    } else {
                        m.setPosition(new LatLng(Double.valueOf(lat), Double.valueOf(lng)));
                        m.setTitle(ja.getJSONObject(i).getString("Title"));
                    }
                }
            } catch (Exception er) {

            }
        }
    }
    public static void getDevicesLocation(String bounds, String zoom, final Context context, final GoogleMap gmap) {
        bounds = bounds.replace("LatLngBounds{southwest=lat/lng: ", "(");
        bounds = bounds.replace("northeast=lat/lng: ", "");
        bounds = bounds.replace("}", ")");
        HashMap<String, String> params = new HashMap<>();
        params.put("bounds", bounds);
        params.put("zoom", zoom);
        if(WS==null)
            WS=new WebServices(context);

         WS.addQueue("ir.tsip.tracker.zarrintracker.Tools",0,params,"GetMarkers");

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
        if (bitmap == null)
            return null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    public  static void PlayAlert(Context c)
    {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(c.getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LoadImage(ImageView iv,byte[] imageAsBytes , int Radious)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 *32];

        Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, options);
        if(Radious>0)
            bm = CircleImage.getRoundedRectBitmap(bm,Radious);
        iv.setImageBitmap(bm);
    }

    public static Bitmap LoadImage(byte[] imageAsBytes , int Radious)
    {
        if(imageAsBytes == null)
            return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[1024 *32];

        Bitmap bm = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length, options);
        if(Radious>0)
            bm = CircleImage.getRoundedRectBitmap(bm,Radious);
        return bm;
    }

    public static String HashMapToString(HashMap<String,String> Data)
    {
        String Ret = "";
        Iterator myVeryOwnIterator = Data.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
            String key=(String)myVeryOwnIterator.next();
            String value=(String)Data.get(key);
            Ret+=key+String.valueOf((char)26)+value+String.valueOf((char)25);
        }
        return  Ret;
    }

    public static HashMap<String,String> StringToHashMap(String Data)
    {
        HashMap<String,String> Ret = new HashMap<>();
        String[] Splt = Data.split(String.valueOf((char)25));
        for(String S : Splt)
        {
            if(S.length() > 0)
            {
                String[] b = S.split(String.valueOf((char)26));
                if(b.length == 2)
                    Ret.put(b[0],b[1]);
            }
        }
        return  Ret;
    }

    public static void setTitleColor(Activity context)
    {
        context.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
    }
}

