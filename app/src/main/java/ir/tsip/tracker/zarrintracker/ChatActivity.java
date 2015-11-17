package ir.tsip.tracker.zarrintracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    Button btnSend;
    EditText txtMessage;
    private static RequestQueue queue;
    Map<String, String> params;
    String gpID;
    ListView lsvChat;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        gpID = getIntent().getStringExtra("gpID");


        txtMessage = (EditText) findViewById(R.id.txtSendMessage);
        lsvChat = (ListView) findViewById(R.id.lsvChats);

        btnSend = (Button) findViewById(R.id.btnSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            final String url = "http://tstracker.ir/services/webbasedefineservice.asmx/SetMessage";

            @Override
            public void onClick(View v) {
                params = new HashMap<>();
                params.put("message", txtMessage.getText().toString());
                params.put("imei", Tools.GetImei(getApplicationContext()));
                params.put("gpID", gpID);
                txtMessage.setText("");
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                        new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                        } catch (Exception er) {
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                if (queue == null)
                    queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(jsObjRequest);
            }
        });


        adapter = new ArrayAdapter<String>(this, R.layout.drawerlistlayout, msgs);
        lsvChat.setAdapter(adapter);

        GetNewstMessages();
    }


    private static RequestQueue queue2;
    Map<String, String> params2;

    private void GetNewstMessages() {

        Timer _Timer = new Timer(true);
        _Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    params2 = new HashMap<>();
                    params2.put("imei", Tools.GetImei(getApplicationContext()));
                    params2.put("gpID", gpID);
                    JsonObjectRequest jsObjRequest2 = new JsonObjectRequest(Request.Method.POST, "http://tstracker.ir/services/webbasedefineservice.asmx/GetMessage",
                            new JSONObject(params2), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                            try {
                                if (response.getString("d").length() > 4) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            try {
                                                InsertMessages(response.getString("d").split(","));
                                                for (String s : response.getString("d").split(",")
                                                        ) {
                                                    msgs.add(s);
                                                }
                                            } catch (Exception er) {

                                            }
                                        }
                                    });
                                }
                            } catch (Exception er) {
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    });
                    if (queue2 == null)
                        queue2 = Volley.newRequestQueue(getApplicationContext());
                    queue2.add(jsObjRequest2);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                ShowMessages();
                            } catch (Exception er) {

                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.toString();
                }
            }

        }, 0, 1000);
    }

    ContentValues Data;
    DatabaseHelper dbh;
    SQLiteDatabase db;

    private void InsertMessages(String[] messages) {
        if (messages == null)
            return;
        if (dbh == null)
            dbh = new DatabaseHelper(getApplicationContext());
        if (db == null)
            db = dbh.getWritableDatabase();
        for (String msg : messages) {
            Data = new ContentValues();
            Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Data, msg);
            Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Group, gpID);
            db.insert(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_ID, Data);
            msgs.add(msg);
        }

    }

    ArrayList<String> msgs = new ArrayList<>();
    SQLiteDatabase readabledb;

    private void ShowMessages() {

        if (msgs.isEmpty()) {
            if (dbh == null)
                dbh = new DatabaseHelper(getApplicationContext());
            if (readabledb == null)
                readabledb = dbh.getReadableDatabase();

            String[] columns = {DatabaseContracts.ChatLog.COLUMN_NAME_ID, DatabaseContracts.ChatLog.COLUMN_NAME_Group, DatabaseContracts.ChatLog.COLUMN_NAME_Data};
            Cursor c = readabledb.query(DatabaseContracts.ChatLog.TABLE_NAME, columns, DatabaseContracts.ChatLog.COLUMN_NAME_Group + "=?", new String[]{String.valueOf(gpID)}, "", "", "");
            c.moveToFirst();
            while (true) {
                msgs.add(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.ChatLog.COLUMN_NAME_Data)));
                if (c.isLast())
                    break;
                c.moveToNext();
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}