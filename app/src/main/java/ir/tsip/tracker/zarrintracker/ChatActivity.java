package ir.tsip.tracker.zarrintracker;

import android.app.usage.UsageEvents;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

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

    ImageView inInvite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _this=null;
        _this=this;
        context = getApplicationContext();

         if( getIntent().getStringExtra("myGroup")=="true") {

             inInvite = (ImageView) findViewById(R.id.ivInvite);
             inInvite.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View v) {
                     Intent myIntent = new Intent(ChatActivity.this, Invite.class);
                     ChatActivity.this.startActivity(myIntent);
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
                W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity",0,params,"SetMessage");
               // InsertMessages(txtMessage.getText().toString().split(String.valueOf((char) 26)));
                txtMessage.setText("");
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
        view.setId(new Random().nextInt());

        lsvChat.addView(view);
        TextView tvChatDate = (TextView) view.findViewById(R.id.tvChatDate);
        tvChatDate.setText(date.toString());
        TextView tvLastChatMessage = (TextView) view.findViewById(R.id.tvLastChatMessage);
        tvLastChatMessage.setText(Message);
        if(img!=null) {
            ImageView ivChatPic = (ImageView) view.findViewById(R.id.ivChatPic);
            ivChatPic.setImageBitmap(img);
        }
        View.OnClickListener ClickOpenGroup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        };

        LinearLayout llGroupList1 = (LinearLayout) view.findViewById(R.id.llChatList1);
        LinearLayout llGroupList2 = (LinearLayout) view.findViewById(R.id.llChatList2);
        LinearLayout llGroupList3 = (LinearLayout) view.findViewById(R.id.llChatList3);
        LinearLayout llGroupList4 = (LinearLayout) view.findViewById(R.id.llChatList4);

        llGroupList1.setOnClickListener(ClickOpenGroup);
        llGroupList2.setOnClickListener(ClickOpenGroup);
        llGroupList3.setOnClickListener(ClickOpenGroup);
        llGroupList4.setOnClickListener(ClickOpenGroup);

        ScrollView svChatView = (ScrollView)_this.findViewById(R.id.svChatView);
        svChatView.scrollTo(0, lsvChat.getHeight());
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