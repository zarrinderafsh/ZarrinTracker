package ir.tsip.tracker.zarrintracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.games.GameRef;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    Button btnSend;
    EditText txtMessage;
    static Context context;
    String gpID;
    static LinearLayout lsvChat;
    static ChatActivity _this;
static TextView txtGeneratedJoinCode;
    ImageView inInvite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _this=null;
        _this=this;
        context = getApplicationContext();
        inInvite = (ImageView) findViewById(R.id.ivInvite);

         if( getIntent().getBooleanExtra("myGroup",false)) {

             inInvite.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
                     AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                     builder.setTitle("Invite Others");
                     LayoutInflater inflate=ChatActivity.this.getLayoutInflater();
                     View view=inflate.inflate(R.layout.activity_invate,null);
                     txtGeneratedJoinCode=(TextView)view.findViewById(R.id.txtGeneratedJOinCode);
                     WebServices w=new WebServices(ChatActivity.this);
                     HashMap<String, String> params = new HashMap<>();
                     params.put("imei", Tools.GetImei(getApplicationContext()));
                     w.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 0, params, "GenerateJoinKey");
                     w=null;
                     builder.setView(view);
                     builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {
                             try {
                                 Tools.ShareText(txtGeneratedJoinCode.getText().toString(), _this);

                             } catch (Exception ex) {

                             }
                         }
                     });
                     builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialog, int which) {

                             dialog.cancel();
                         }
                     });

                   AlertDialog av=  builder.create();
                     av.show();
//                     RelativeLayout.LayoutParams lp=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//
//                     lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                     av.getButton(AlertDialog.BUTTON_NEGATIVE).setLayoutParams(lp);
//                     av.getButton(AlertDialog.BUTTON_POSITIVE).setGravity(Gravity.TOP| Gravity.RIGHT);
                 }
             });
             inInvite.setVisibility(View.VISIBLE);
         }
        else
             inInvite.setVisibility(View.INVISIBLE);


        gpID = getIntent().getStringExtra("gpID");


        txtMessage = (EditText) findViewById(R.id.txtSendMessage);
        lsvChat = (LinearLayout) findViewById(R.id.lsvChats);

        btnSend = (Button) findViewById(R.id.btnSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> params;
                params = new HashMap<>();
                params.put("message", txtMessage.getText().toString());
                params.put("imei", Tools.GetImei(getApplicationContext()));
                params.put("gpID", gpID);
                WebServices W = new WebServices(getApplicationContext());
                W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", -1, params, "SetMessage");
               // InsertMessages(txtMessage.getText().toString().split(String.valueOf((char) 26)));
                txtMessage.setText("");
                W=null;
            }
        });

        ShowMessages();
        ScrollView svChatView = (ScrollView)_this.findViewById(R.id.svChatView);
        svChatView.scrollTo(0, lsvChat.getBottom());
    }

    public static void backWebServices(int ObjectCode, String Data) {
        if(ObjectCode == 1) {
            if(_this == null || Data == "null")
                return;
            _this.InsertMessages(Data.split(","));
        }
        else if(ObjectCode==0) {
            if (Data != ("0")) {
                txtGeneratedJoinCode.setText(Data);
            }
        }
    }


    DatabaseHelper dbh;
    SQLiteDatabase db;

    private void InsertMessages(String[] messages) {
        if (messages == null)
            return;
        if (dbh == null)
            dbh = new DatabaseHelper(context);
        if (db == null)
            db = dbh.getWritableDatabase();
        ContentValues Data;
        for (String msg : messages) {
            Data = new ContentValues();
            if(msg.contains("[C]")) {
                Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Data, msg.replace("[C]",""));
                Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Group, gpID);
                db.insert(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_ID, Data);
                CreateGroupLayer(new Date(), msg, null, 0);
                MessageEvent.InsertMessage(context, "New message!");
            }
            else  if(msg.contains("[E]"))
            {
                MessageEvent.InsertMessage(context,msg.replace("[E]",""));
            }
            else  if(msg.contains("[G]"))
            {
                String[] data=msg.replace("[G]","").split("~");
                LatLng latLng=new LatLng(Double.valueOf(data[0].split(",")[0]),Double.valueOf(data[0].split(",")[1]));
                Circle circle = Tools.GoogleMapObj.addCircle(new CircleOptions().center(latLng).fillColor(Color.RED).strokeColor(Color.RED).strokeWidth(1).radius(Integer.valueOf(data[1])));
                try {
                    LocationListener.locationManager.addProximityAlert(circle.getCenter().latitude, circle.getCenter().longitude, (float) circle.getRadius(), -1, PendingIntent.getBroadcast(ChatActivity.this, 0, new Intent("ir.tsip.tracker.zarrintracker.ProximityAlert"), 0));
                }
                catch (SecurityException er) {

                }

                ContentValues Val = new ContentValues();
                DatabaseHelper dbh = new DatabaseHelper(ChatActivity.this);
                SQLiteDatabase db = dbh.getWritableDatabase();
                try {
                    Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, latLng.toString());
                    Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, data[2]);
                    Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, data[1]);
                    db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val);
                }
                catch (Exception er){

                }
                db.close();
                dbh.close();
                }
            Data=null;
        }

    }

    SQLiteDatabase readabledb;

    private void ShowMessages() {

        //if (msgs.isEmpty())
        {
            if (dbh == null)
                dbh = new DatabaseHelper(getApplicationContext());
            if (readabledb == null)
                readabledb = dbh.getReadableDatabase();

            String[] columns = {DatabaseContracts.ChatLog.COLUMN_NAME_ID, DatabaseContracts.ChatLog.COLUMN_NAME_Group, DatabaseContracts.ChatLog.COLUMN_NAME_Data};
            Cursor c = readabledb.query(DatabaseContracts.ChatLog.TABLE_NAME, columns, DatabaseContracts.ChatLog.COLUMN_NAME_Group + "=?", new String[]{String.valueOf(gpID)}, "", "",DatabaseContracts.ChatLog.COLUMN_NAME_ID );
           if(c.getCount()>0) {
               c.moveToFirst();
               while (true) {

                   CreateGroupLayer(new Date(), c.getString(c.getColumnIndexOrThrow(DatabaseContracts.ChatLog.COLUMN_NAME_Data)), null, 0);

                   if (c.isLast())
                       break;
                   c.moveToNext();
               }
           }
            c.close();
        }
    }

    private static void CreateGroupLayer(Date date,String Message,Bitmap img, int State)
    {
        if(context == null)
            return;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_list, null);
        if(Message.contains("~~ME~~")) {
            Message=  Message.replace("~~ME~~", "");
            view = inflater.inflate(R.layout.chat_list_owner, null);
        }
        view.setId(new Random().nextInt());
        lsvChat.addView(view);
       TextView tvLastChatMessage = (TextView) view.findViewById(R.id.tvLastChatMessage);
        TextView txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        txtUsername.setText(Message.split(" : ")[0]);
        tvLastChatMessage.setText(Message.replace("[","").replace("]","").split(" : ")[1]);
        if(img!=null) {
            ImageView ivChatPic = (ImageView) view.findViewById(R.id.ivChatPic);
            ivChatPic.setImageBitmap(img);
        }

        ScrollView svChatView = (ScrollView)_this.findViewById(R.id.svChatView);
        svChatView.scrollTo(0, lsvChat.getScrollY());
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