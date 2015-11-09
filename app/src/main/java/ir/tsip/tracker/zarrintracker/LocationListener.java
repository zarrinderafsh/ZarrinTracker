package ir.tsip.tracker.zarrintracker;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class LocationListener  extends Service implements android.location.LocationListener,GpsStatus.Listener {

    private static Context mContext;

    // flag for GPS status
    public static boolean isGPSEnabled = false;

    // flag for network status
    public static boolean isNetworkEnabled = false;

    // flag for GPS status
    public static boolean canGetLocation = false;

    // flag for Internet status
    private static boolean InternetConnection = false;

    private static double latitude; // latitude
    private static double longitude; // longitude
    private static int Altitude;
    private static int Speed;
    private static int LastMoveBearing;
    private static int MoveBearing;
    private static int MoveDistance;
    private static Location LastLocation;
    private static long gpsTime;
    private static boolean isNewLocation;

    public static int CurrentBearing;
    public static int CurrentSpeed;
    public static Date CurrentTime;
    public static float CurrentAccuracy;
    public static double CurrentLat;
    public static double CurrentLon;
    public static double CurrentSignal;
public static Location CurrentLocation;


    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 0 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; // 0 Second

    // Declaring a Location Manager
    protected static LocationManager locationManager;

    public LocationListener() {
    }

    public LocationListener(Context context) {
        this.mContext = context;
    }

    public void PrepareLocation() {
        try {
            if(locationManager==null)
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

            locationManager.addGpsStatusListener(this);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                this.canGetLocation = false;
            } else {
                this.canGetLocation = true;

                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Location getLocation() {
        Location GPS_location = null; // GPS_location
        Location Network_location = null; // Network_location

        try {

            if (!isGPSEnabled && !isNetworkEnabled) {
                return null;
            } else {
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (locationManager != null) {
                        GPS_location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }

                if (isNetworkEnabled) {
                    if (locationManager != null) {
                        Network_location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //if there are both values use the latest one
        if (GPS_location != null && Network_location != null) {
            if (GPS_location.getTime() > Network_location.getTime())
                return GPS_location;
            else
                return Network_location;
        }

        if (GPS_location != null) {
            return GPS_location;
        }
        if (Network_location != null) {
            return Network_location;
        }
        return null;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationListener.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public static double getLatitude(){
        if(LastLocation != null){
            latitude = LastLocation.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public static double getLongitude(){
        if(LastLocation != null){
            longitude = LastLocation.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public static boolean isNewLocation() {
        if(isNewLocation) {
            isNewLocation = false;
            return true;
        }
        else {
            return false;
        }
    }

    public static boolean GetInternetConnect() {
        return InternetConnection;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public void showSettingsAlert(){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//            }
//        });
//
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        // Showing Alert Message
//        alertDialog.show();
    }

    @Override
    public void onGpsStatusChanged(int p) {
        int count=0;
        float Snr=0;
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        if(gpsStatus != null) {
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();
            int i=0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                Snr += satellite.getSnr();
                count++;
            }
        }
        CurrentSignal = Snr / count;
    }

    @Override
    public void onLocationChanged(Location location) {

        CurrentBearing = (int)location.getBearing();
        CurrentSpeed = (int)(location.getSpeed() * 3.6) ; // KM
        CurrentTime = (new Date(location.getTime()));
        CurrentAccuracy = location.getAccuracy();
        CurrentLat=location.getLatitude();
        CurrentLon = location.getLongitude();
        CurrentLocation=location;
        MessageManager.SetMessage(
                " New Location is MoveBearing:" + CurrentBearing +
                        " Time:" + (new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(CurrentTime)) +
                        " Speed:" + CurrentSpeed);

        if(LastLocation == null)
            LastLocation = location;
        MoveBearing =  (int)location.bearingTo(LastLocation);
        MoveDistance = (int)location.distanceTo(LastLocation);

        if(     (MoveDistance > 5 && Math.abs(MoveBearing-LastMoveBearing) >10 && location.getSpeed() > 0.0)
                ||
                (MoveDistance > 40)
                ) {
            isNewLocation = true;
            LastLocation = location;
            LastMoveBearing =  MoveBearing;

            latitude = location.getLatitude();
            longitude=location.getLongitude();
            Altitude = (int)location.getAltitude();
            Speed = (int)(location.getSpeed()*3.6);
            gpsTime = location.getTime();
            MessageManager.SetMessage("New Location Get :)");
        }
    }

    public static ContentValues getData()
    {
        Date date = new Date(gpsTime);
        String SDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
        String data =  latitude + "," +
                longitude + "," +
                Altitude + "," +
                Speed + "," +
                MoveBearing + "," +
                SDate + "," +
                0;
        ContentValues Val = new ContentValues();
        Val.put(DatabaseContracts.AVLData.COLUMN_NAME_Data , data);
        return Val;

    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        if(mContext == null)
            this.mContext = getApplicationContext();
        PrepareLocation();
        StartServices();
        return START_STICKY;
    }

    private void StartServices()
    {
        Timer _Timer = new Timer(true);
        _Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PrepareLocation();
                InternetConnection = Tools.isOnline(mContext);
                String SDate=" None";
                if(LastLocation != null) {
                    Date date = new Date(LastLocation.getTime());
                    SDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
                }
                if(isGPSEnabled || isNetworkEnabled)
                    Tools.Notificationm(mContext,"ZTracker","Last Get Location:"+SDate,"");
                else
                    Tools.HideNotificationm();
            }

        }, 0, 10000);
    }

}
