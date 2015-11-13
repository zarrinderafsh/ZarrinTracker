package ir.tsip.tracker.zarrintracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by morteza on 2015-11-13.
 */
public class WebServices {

    private static String url = "http://tstracker.ir/services/webbasedefineservice.asmx/";
    private static com.android.volley.RequestQueue queue;
    private Context context;

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
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_Data,Data);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_WebServiceName,WebServiceName);
            Val.put(DatabaseContracts.QueueTable.COLUMN_NAME_State,0);

            db.insert(DatabaseContracts.QueueTable.TABLE_NAME, DatabaseContracts.QueueTable.COLUMN_NAME_ID, Val);
        } catch (Exception ex) {
        }
        db.close();
        dbh.close();
    }

    private void RunSend() {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getWritableDatabase();
        Timer _Timer = new Timer(true);
        _Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
//                    c = db.query(DatabaseContracts.AVLData.TABLE_NAME, columns, "", null, "", "", "");
//                    c.moveToFirst();
//                    try {
//                        Counter = 0;
//                        CountPoint = c.getCount();
//                        if (c.getCount() > 0)
//                            while (true || Counter < 20) {
//                                Counter++;
//                                Data += c.getString(c.getColumnIndexOrThrow(DatabaseContracts.AVLData.COLUMN_NAME_Data)) + "#";
//                                if (IDSend.length() > 0)
//                                    IDSend += ',';
//                                IDSend += c.getString(c.getColumnIndexOrThrow(DatabaseContracts.AVLData.COLUMN_NAME_ID));
//                                if (c.isLast())
//                                    break;
//                                c.moveToNext();
//                            }
//                    } catch (Exception er) {
//                    }
//                    c.close();
//                    c = null;
                } catch (Exception ex) {
                    ex.toString();
                }
            }

        }, 0, 1000);
    }

    private void SendData(final String ClassName, final int ObjectCode , String Data, String FuncName)
    {
        Map<String, String> params = new HashMap<>();
        params.put("Data", Data);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url+FuncName,
                new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String data = response.getString("d");
                    Object ret  = Action.run(ClassName,"backWebServices",new Class[] {int.class,data.getClass()}, new Object[] {ObjectCode,data});
                    if((Boolean) ret)
                    {
                        SetState(1);
                    }
                    else
                        SetState(0);
                } catch (Exception er) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        if (Data.length() > 1) {
            if(queue == null)
                queue = Volley.newRequestQueue(context);
            queue.add(jsObjRequest);
        }
    }

    private void SetState(int i)
    {

    }

}
