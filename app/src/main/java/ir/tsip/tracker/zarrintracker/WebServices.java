package ir.tsip.tracker.zarrintracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Created by morteza on 2015-11-13.
 */
public class WebServices {

    private static String url = "http://tstracker.ir/services/webbasedefineservice.asmx/";
    private static com.android.volley.RequestQueue queue;
    private Context context;

    public WebServices()
    {
        context = MainActivity.Base;
    }

    public WebServices(Context pContext)
    {
        context = pContext;
    }

    public void addQueue(String ClassName, int ObjectCode , String Data, String WebServiceName)
    {

        ContentValues Val = new ContentValues();
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        try {
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_ClassName,ClassName);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_ObjectCode,ObjectCode);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_Data_String,Data);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_WebServiceName,WebServiceName);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_State,0);


            db.insert(DatabaseContracts.QueueTable.TABLE_NAME, DatabaseContracts.QueueTable.COLUMN_NAME_ID, Val);
        } catch (Exception ex) {
        }
        db.close();
        dbh.close();
    }

    public void addQueue(String ClassName, int ObjectCode , HashMap<String,String> Data, String WebServiceName)
    {

        ContentValues Val = new ContentValues();
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        try {
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_ClassName,ClassName);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_ObjectCode,ObjectCode);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_Data_String,Tools.HashMapToString(Data));
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_WebServiceName,WebServiceName);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_State,0);

        db.insert(DatabaseContracts.QueueTable.TABLE_NAME, DatabaseContracts.QueueTable.COLUMN_NAME_ID, Val);
        } catch (Exception ex) {
        }
        db.close();
        dbh.close();
    }

    public void addQueue(String ClassName, int ObjectCode , byte[] Data, String WebServiceName)
    {
        ContentValues Val = new ContentValues();
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        try {
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_ClassName,ClassName);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_ObjectCode,ObjectCode);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_Data_Blob,Data);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_WebServiceName,WebServiceName);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_State,0);

            long id = db.insert(DatabaseContracts.QueueTable.TABLE_NAME, DatabaseContracts.QueueTable.COLUMN_NAME_ID, Val);
//            Toast.makeText(context,"-"+id,Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
        }
        db.close();
        dbh.close();
    }
    public void RunSend() {
        RunSend(1000,0);
        RunSend(1000 * 60 * 10, 2);
    }
    Cursor c;
    public void RunSend(int DelaySecound, final int pState) {
        Timer _Timer = new Timer(true);
        _Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!Tools.isOnline(context))
                        return;
                    DatabaseHelper dbh = new DatabaseHelper(context);
                    SQLiteDatabase db = dbh.getReadableDatabase();

                    c = db.query(DatabaseContracts.QueueTable.TABLE_NAME, null, DatabaseContracts.QueueTable.COLUMN_NAME_State + "=" + pState, null, "", "", "");
                    try {
                        if (c.moveToFirst()) {
                            do {
                                SetState(c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.QueueTable.COLUMN_NAME_ID)), 2);
                                String S = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.QueueTable.COLUMN_NAME_Data_String));
                                if (S == null || S.length() == 0) {
                                    byte[] B = c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.QueueTable.COLUMN_NAME_Data_Blob));
                                    S = new String(B);
                                }
                                SendData(
                                        c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.QueueTable.COLUMN_NAME_ID)),
                                        c.getString(c.getColumnIndexOrThrow(DatabaseContracts.QueueTable.COLUMN_NAME_ClassName)),
                                        c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.QueueTable.COLUMN_NAME_ObjectCode)),
                                        S,
                                        c.getString(c.getColumnIndexOrThrow(DatabaseContracts.QueueTable.COLUMN_NAME_WebServiceName))
                                );
                            } while (c.moveToNext());
                        }
                    } catch (Exception er) {
                        er.toString();
                    }
                    c.close();
                    db.close();
                    dbh.close();
                } catch (Exception ex) {
                    ex.toString();
                }
            }

        }, 0, DelaySecound);
    }

    private void SendData(final int Id,final String ClassName, final int ObjectCode , String Data, String FuncName)
    {
        Map<String, String> params = new HashMap<>();
        params = Tools.StringToHashMap(Data);
        if(params.size() == 0)
            params.put("Data",Data);// Encryprt(Data).toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url+FuncName,
                new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Delete(Id);
                    String data = response.getString("d");//Decrypt(response.getString("d"));
                    Object ret  = Action.run(ClassName,"backWebServices",new Class[] {int.class,data.getClass()}, new Object[] {ObjectCode,data});
                } catch (Exception er) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Delete(Id);
                SetState(Id,2);
            }
        });
        if (Data.length() > 1) {
            if(queue == null)
                queue = Volley.newRequestQueue(context);
            queue.add(jsObjRequest);
        }
    }

    public byte[] DecryptData(String data){
        try {
            byte[] key2=Tools.getkey(Tools.keyfromdb(context));
// decrypt
            byte[] decryptedData = Tools.encrypt(key2, data.getBytes());
            return decryptedData;
        }
        catch (Exception er){
            return new byte[]{};
        }
    }


    public   byte[] EncryptData(String data){
        try {
            byte[] b = data.getBytes();
            byte[] key2=Tools.getkey(Tools.keyfromdb(context));
// encrypt
            byte[] encryptedData = Tools.encrypt(key2, b);
            return encryptedData;
        }
        catch (Exception er){
            return new byte[]{};
        }
    }



//    private void SendData(final int Id,final String ClassName, final int ObjectCode , byte[] Data, String FuncName)
//    {
//        Map<String, byte[]> params = new HashMap<>();
//        params.put("Data", Data);
//        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url+FuncName,
//                new JSONObject(params), new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    Delete(Id);
//                    String data = response.getString("d");
//                    Object ret  = Action.run(ClassName,"backWebServices",new Class[] {int.class,data.getClass()}, new Object[] {ObjectCode,data});
//                } catch (Exception er) {
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                SetState(Id,2);
//            }
//        });
//        if (Data.length > 1) {
//            if(queue == null)
//                queue = Volley.newRequestQueue(context);
//            queue.add(jsObjRequest);
//        }
//    }

    private void Delete(int id)
    {
        DatabaseHelper dh = new DatabaseHelper(context);
        SQLiteDatabase db = dh.getReadableDatabase();
        if(db.delete(DatabaseContracts.QueueTable.TABLE_NAME, DatabaseContracts.QueueTable.COLUMN_NAME_ID+ " in ("+id+")", null)>0){
        }
        db.close();
        dh.close();
    }

    private void SetState(int id, int State)
    {
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.QueueTable.COLUMN_NAME_State,State);
        DatabaseHelper dh = new DatabaseHelper(context);
        SQLiteDatabase db = dh.getReadableDatabase();
        if(db.update(DatabaseContracts.QueueTable.TABLE_NAME,V, DatabaseContracts.QueueTable.COLUMN_NAME_ID + " = " + id + "", null)>0){
        }
        db.close();
        dh.close();
    }

}
