package ir.tsip.tracker.zarrintracker;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

public class LocationService extends Service {

    private boolean _StartService;
    private Intent _Intent;
    private Thread _Thread;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        _Intent = intent;
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId)
    {
        _StartService = true;
        _Intent = intent;
        Thread _Thread = new Thread() {
            @Override
            public void run() {
                Process();
            }
        };
        _Thread.start();
        return START_STICKY;
    }

    @Override
    public  void onDestroy()
    {
        _StartService = false;
    }

    public void Process()
    {
        ContentValues Data = new ContentValues();
        DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db =  dbh.getWritableDatabase();
        while(_StartService)
        {
            try
            {
                Thread.sleep(1000);

                if(LocationListener.isNewLocation()){
                    Data = LocationListener.getData();

                    db.insert(DatabaseContracts.AVLData.TABLE_NAME,DatabaseContracts.AVLData.COLUMN_NAME_ID,Data);
                    MessageManager.SetMessage(
                            "Your Location is - \nLat: " +
                             LocationListener.getLatitude() +
                             "\nLong: " +
                             LocationListener.getLongitude());
                }else{
                }
            }
            catch (Exception ex)
            {

            }
        }
        db.close();
        dbh.close();
    }
}
