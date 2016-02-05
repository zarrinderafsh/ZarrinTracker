package ir.tsip.tracker.zarrintracker;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.provider.ContactsContract;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SendDataService extends Service {

    private boolean _SeriveStart;
    private Intent _Intent;
    private Thread _Thread;
    private Boolean _SendData;
    private static com.android.volley.RequestQueue queue;
    private static String IDSend = "";
    public static int CountPoint;


    public SendDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        _Intent = intent;
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId)
    {
        _SeriveStart = true;
        _Intent = intent;

        Thread _Thread = new Thread() {
            @Override
            public void run() {
                SendData();
            }
        };
        _Thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        _SeriveStart = false;
    }

    public void SendData() {
        DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbh.getWritableDatabase();
        String[] columns = {DatabaseContracts.AVLData.COLUMN_NAME_ID, DatabaseContracts.AVLData.COLUMN_NAME_Data};
        Cursor c=null;
        int Counter = 0;
        String Data = "";
        while (_SeriveStart) {
            try {
                Thread.sleep(1000);
                if(LocationListener.GetInternetConnect()) {
                    if (IDSend.length() == 0 && LocationListener.GetInternetConnect()) {
                        Data = "";
                        IDSend = "";
                        try {
                            c = db.query(DatabaseContracts.AVLData.TABLE_NAME, columns, "", null, "", "", "");
                            c.moveToFirst();
                            try {
                                Counter = 0;
                                CountPoint = c.getCount();
                                if (c.getCount() > 0)
                                    while (true || Counter < 20) {
                                        Counter++;
                                        Data += c.getString(c.getColumnIndexOrThrow(DatabaseContracts.AVLData.COLUMN_NAME_Data)) + "#";
                                        if (IDSend.length() > 0)
                                            IDSend += ',';
                                        IDSend += c.getString(c.getColumnIndexOrThrow(DatabaseContracts.AVLData.COLUMN_NAME_ID));
                                        if (c.isLast())
                                            break;
                                        c.moveToNext();
                                    }
                            } catch (Exception er) {
                            }
                            c.close();
                            c = null;
                        } catch (Exception er) {
                        }
                        if (Data.length() > 1) {
                            Data = Tools.GetImei(getApplicationContext()) + "|" + Data;
                            SendData(Data);

                            db.delete(DatabaseContracts.AVLData.TABLE_NAME, "", null);
                        }
                    }
                }
            } catch (Exception ex) {
                IDSend="";
            }
        }
        c.close();
        db.close();
        dbh.close();
        IDSend="";
    }

    private static String url = "http://tstracker.ir/services/webbasedefineservice.asmx/SaveAvlMobile";

    private void SendData(String Data)
    {
        Map<String, String> params = new HashMap<>();
        params.put("Data", Data);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    if (data.contains("1")) {
                        DatabaseHelper dh = new DatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = dh.getReadableDatabase();
                        if(db.delete(DatabaseContracts.AVLData.TABLE_NAME, DatabaseContracts.AVLData.COLUMN_NAME_ID+ " in ("+IDSend+")", null)>0){
                        }
                        IDSend="";
                        db.close();
                        dh.close();
                        dh=null;
                    }
                } catch (Exception er) {
                    IDSend="";
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                IDSend="";
            }
        });
        if (Data.length() > 1) {
            if(queue == null)
                queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(jsObjRequest);
        }
    }

}
