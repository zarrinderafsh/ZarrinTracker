package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    public  static PendingIntent resultPendingIntent;
    public static NotificationManager mNotificationManager;

    public static void Notificationm(Context context, String Title, String Details, String packge) {
        if (mBuilder == null) {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.notification_icon_anim)
                            .setContentTitle(Title)
                            .setContentText(Details);
        }
        else
        {
            mBuilder.setContentTitle(Title);
            mBuilder.setContentText(Details);
        }
        if (resultIntent == null)
            resultIntent = new Intent(context, MainActivity.class);

        if(stackBuilder == null) {
            stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
        }

        if(resultPendingIntent == null) {
            resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
        }
        mBuilder.setContentIntent(resultPendingIntent);
        if(mNotificationManager == null) {
            mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        mNotificationManager.notify(0, mBuilder.build());
    }

    public static void HideNotificationm()
    {
        if(mNotificationManager != null)
        {
            mNotificationManager.cancelAll();
        }
    }

    public static Point GetDesktopSize(Activity activity)
    {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public  static void ShareText(String pShareBody,Activity pActivity)
    {
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
        GoogleMap googleMap = null;
        if (googleMap == null) {
      //      MapFragment MF = ((MapFragment) activity.getFragmentManager().findFragmentById(
        //            R.id.map));
            if(MF != null)
                googleMap = MF.getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
            }
            else
            {

            }
        }
        return googleMap;
    }

    private static Marker myMarker;
    private static  LatLng myPosition;
    public static void setUpMap(GoogleMap googleMap) {
        if (googleMap == null ||  LocationListener.CurrentLocation== null)
            return;
        Location currentLocation = LocationListener.CurrentLocation;

        myPosition= new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        if(myMarker == null) {
          myMarker = googleMap.addMarker(new MarkerOptions().position(myPosition).title("موقعیت من"));
            googleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(myPosition, 15.0f));
        }
        myMarker.setPosition(myPosition);


    }
    public static float getBatteryLevel(Activity activate) {
        Intent batteryIntent = activate.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }
}

