package ir.tsip.tracker.zarrintracker;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;
import com.google.android.gms.games.GameRef;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {

    Button btnSend;
    EditText txtMessage;
    static Context context;
    String gpID;
    TextView txtGpName;
    ImageView imgGroupPhoto;
    static LinearLayout lsvChat;
    static ChatActivity _this;
    static TextView txtGeneratedJoinCode;
    static AlertDialog av;
    static ImageView imgLoading;
    ImageView inInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        _this = null;
        _this = this;
        context = getApplicationContext();
        inInvite = (ImageView) findViewById(R.id.ivInvite);

        if (getIntent().getBooleanExtra("myGroup", false)) {

            inInvite.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Invite Others");
                    LayoutInflater inflate = ChatActivity.this.getLayoutInflater();
                    View view = inflate.inflate(R.layout.activity_invate, null);
                    txtGeneratedJoinCode = (TextView) view.findViewById(R.id.txtGeneratedJOinCode);
                    imgLoading = (ImageView) view.findViewById(R.id.imgLoading);

                    WebServices w = new WebServices(ChatActivity.this);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("imei", Tools.GetImei(getApplicationContext()));
                    w.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 0, params, "GenerateJoinKey");
                    w = null;
                    builder.setView(view);
                    builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
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

                    av = builder.create();
                    av.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    av.show();
                }
            });
            inInvite.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txtInvitetoGroup)).setVisibility(View.VISIBLE);
        } else {
            ImageView imgLeave = (ImageView) findViewById(R.id.imgLeaveGroup);
            imgLeave.setVisibility(View.VISIBLE);
            imgLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Leave group");
                    builder.setMessage(R.string.LeaveGroupMessage);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                HashMap<String, String> params;
                                params = new HashMap<>();
                                params.put("imei", Tools.GetImei(getApplicationContext()));
                                params.put("gpID", gpID);
                                WebServices W = new WebServices(getApplicationContext());
                                W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", -10, params, "LeaveGroup");
                                W = null;
                            } catch (Exception ex) {

                            }
                        }
                    });
                    builder.setNegativeButton("Oh. No!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    AlertDialog av = builder.create();
                    av.show();
                }
            });
            inInvite.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.txtInvitetoGroup)).setVisibility(View.INVISIBLE);
        }

        txtGpName = (TextView) findViewById(R.id.txtGroupName);
        imgGroupPhoto = (ImageView) findViewById(R.id.imgGroupPhoto);

        gpID = getIntent().getStringExtra("gpID");
        setGroupInfo();


        txtMessage = (EditText) findViewById(R.id.txtSendMessage);
        lsvChat = (LinearLayout) findViewById(R.id.lsvChats);

        btnSend = (Button) findViewById(R.id.btnSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.sendMessage(txtMessage.getText().toString());
            }
        });

        ShowMessages();
        svChatView = (ScrollView) _this.findViewById(R.id.svChatView);
        svChatView.fullScroll(ScrollView.FOCUS_DOWN);
        txtMessage.clearFocus();
    }

    ScrollView svChatView;

    private void sendMessage(String msg) {

        HashMap<String, String> params;
        params = new HashMap<>();
        params.put("message", msg);
        params.put("imei", Tools.GetImei(getApplicationContext()));
        params.put("gpID", gpID);
        WebServices W = new WebServices(getApplicationContext());
        W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", -1, params, "SetMessage");
        // InsertMessages(txtMessage.getText().toString().split(String.valueOf((char) 26)));
        txtMessage.setText("");
        W = null;
    }

    public static void backWebServices(int ObjectCode, String Data) {
        if (ObjectCode == 1) {
            if (_this == null || Data == "null")
                return;
            _this.InsertMessages(Data.split(","));
        } else if (ObjectCode == 0) {
            if (Data != ("0")) {
                imgLoading.setVisibility(View.INVISIBLE);
                txtGeneratedJoinCode.setText(Data);
                av.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            }
            else if(Data.split(",")[0]=="-1"){
                Intent myIntent = new Intent(context, PurchaseActivity.class);
                myIntent.putExtra("msg", "You can not invite more than " + Data.split(",")[1] + " persons.");
                context.startActivity(myIntent);
           }
        } else if (ObjectCode == -10) {
            if (Data != null) {
                DatabaseHelper dh = new DatabaseHelper(_this);
                SQLiteDatabase db = dh.getReadableDatabase();
                //Delete item from database
                if (db.delete(DatabaseContracts.Groups.TABLE_NAME, DatabaseContracts.Groups.COLUMN_NAME_ID + " in (" + Data + ")", null) > 0) {
                }
                //Delete all the messages of the group
                if (db.delete(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_Group + " in (" + Data + ")", null) > 0) {
                }
                db.close();
                dh.close();
                dh = null;
                //Send left message to other
                _this.sendMessage("Goodbye friends.");
                //close the activity
                _this.setResult(1);//1 means group activity will be finished.
                _this.finish();
            } else
                Toast.makeText(_this, "Something wrong happened.", Toast.LENGTH_SHORT).show();
        }
    }


    private void setGroupInfo() {

        DatabaseHelper dbh1 = null;
        SQLiteDatabase readabledb1 = null;
        if (dbh1 == null)
            dbh1 = new DatabaseHelper(getApplicationContext());
        if (readabledb1 == null)
            readabledb1 = dbh1.getReadableDatabase();

        String[] columns = {DatabaseContracts.Groups.COLUMN_NAME_ID, DatabaseContracts.Groups.COLUMN_NAME_Image, DatabaseContracts.Groups.COLUMN_NAME_Name};
        Cursor c = readabledb1.query(DatabaseContracts.Groups.TABLE_NAME, columns, DatabaseContracts.Groups.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(gpID)}, "", "", "");
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (true) {
                txtGpName.setText(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Name)).replace(";;;", ""));
                try {
                    imgGroupPhoto.setImageBitmap(Tools.LoadImage(c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Image)), 20));
                } catch (Exception er) {
                    Toast.makeText(ChatActivity.this, "Image didn't load.", Toast.LENGTH_SHORT).show();
                }
                if (c.isLast())
                    break;
                c.moveToNext();
            }
        }
        c.close();
        dbh1.close();
        readabledb1.close();
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
            if (msg.contains("[C]")) {
                Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Data, msg.replace("[C]", ""));
                Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Group, gpID);
                long id = db.insert(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_ID, Data);
                CreateGroupLayer(new Date(), msg, null, 0, (int) id);
                MessageEvent.InsertMessage(context, "New message!");
            } else if (msg.contains("[E]")) {
                MessageEvent.InsertMessage(context, msg.replace("[E]", ""));
            } else if (msg.contains("[G]")) {
                String[] data = msg.replace("[G]", "").split("~");
                LatLng latLng = new LatLng(Double.valueOf(data[0].split(",")[0]), Double.valueOf(data[0].split(",")[1]));
                Circle circle = Tools.GoogleMapObj.addCircle(new CircleOptions().center(latLng).fillColor(Color.RED).strokeColor(Color.RED).strokeWidth(1).radius(Integer.valueOf(data[1])));
                try {
                    LocationListener.locationManager.addProximityAlert(circle.getCenter().latitude, circle.getCenter().longitude, (float) circle.getRadius(), -1,   PendingIntent.getBroadcast(LocationListener.mContext, 0, new Intent("ir.tstracker.activity.proximity"), 0));
                } catch (SecurityException er) {

                }

                ContentValues Val = new ContentValues();
                DatabaseHelper dbh = new DatabaseHelper(ChatActivity.this);
                SQLiteDatabase db = dbh.getWritableDatabase();
                try {
                    Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, latLng.toString());
                    Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, data[2]);
                    Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, data[1]);
                    db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val);
                } catch (Exception er) {

                }
                db.close();
                dbh.close();
            }
            Data = null;
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
            Cursor c = readabledb.query(DatabaseContracts.ChatLog.TABLE_NAME, columns, DatabaseContracts.ChatLog.COLUMN_NAME_Group + "=?", new String[]{String.valueOf(gpID)}, "", "", DatabaseContracts.ChatLog.COLUMN_NAME_ID);
            if (c.getCount() > 0) {
                c.moveToFirst();
                while (true) {

                    CreateGroupLayer(new Date(),
                            c.getString(c.getColumnIndexOrThrow(DatabaseContracts.ChatLog.COLUMN_NAME_Data)),
                            null,
                            0,
                            c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.ChatLog.COLUMN_NAME_ID)));

                    svChatView.fullScroll(ScrollView.FOCUS_DOWN);
                    if (c.isLast())
                        break;
                    c.moveToNext();
                }
            }
            c.close();

        }
    }

    private static void CreateGroupLayer(Date date, String Message, Bitmap img, int State, int ID) {
        if (context == null)
            return;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_list, null);
        if (Message.contains("~~ME~~")) {
            Message = Message.replace("~~ME~~", "");
            view = inflater.inflate(R.layout.chat_list_owner, null);
        }
        view.setTag(String.valueOf(ID));
        view.setId(new Random().nextInt());
        lsvChat.addView(view);
        TextView tvLastChatMessage = (TextView) view.findViewById(R.id.tvLastChatMessage);
        TextView txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        TextView txtDate = (TextView) view.findViewById(R.id.txtDate);


        String s = Message.substring(Message.indexOf(" ["));
        txtUsername.setText(s.substring(2, s.indexOf("]")));
        txtDate.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date));
        tvLastChatMessage.setText(Message.replace("[", "").replace("]", "").split(" : ")[1].replace("\"", ""));
        if (img != null) {
            ImageView ivChatPic = (ImageView) view.findViewById(R.id.ivChatPic);
            ivChatPic.setImageBitmap(img);
        }
        view.setOnLongClickListener(new View.OnLongClickListener() {
            final CharSequence[] items = {"Delete", "Copy", "Cancel"};

            @Override
            public boolean onLongClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setTitle("");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Delete")) {
                            lsvChat.removeView(v);
                            _this.deleteMessage(Integer.valueOf(v.getTag().toString()));
                        } else if (items[item].equals("Copy")) {
                            ClipboardManager clipboard = (ClipboardManager) _this.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Message", ((TextView) v.findViewById(R.id.tvLastChatMessage)).getText());
                            clipboard.setPrimaryClip(clip);

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
                return false;
            }
        });
    }

    private void deleteMessage(int id) {

        DatabaseHelper dh = new DatabaseHelper(_this);
        SQLiteDatabase db = dh.getReadableDatabase();
        //Delete item from database
        if (db.delete(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_ID + " in (" + id + ")", null) > 0) {
        }
        db.close();
        dh.close();
        dh = null;
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