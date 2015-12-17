package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;

public class GroupsActivity extends AppCompatActivity {

    ImageView imgGroupJoin;
    static LinearLayout lsvGroups;
    static Activity context;
    static List<Integer> GroupList;
    static Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

_context=this;
        imgGroupJoin = (ImageView) findViewById(R.id.ivGroup);
        imgGroupJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsActivity.this);
                builder.setTitle("Join Group");
                LayoutInflater inflate=GroupsActivity.this.getLayoutInflater();
                View view=inflate.inflate(R.layout.activity_join_group,null);
                builder.setView(view);
               final EditText txtCode = (EditText) view.findViewById(R.id.txtJoinCode);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("key", txtCode.getText().toString());
                            params.put("imei", Tools.GetImei(getApplicationContext()));
                            WebServices ws = new WebServices(GroupsActivity.this);
                            ws.addQueue("ir.tsip.tracker.zarrintracker.GroupsActivity", 1, params, "AddDevice");
                            ws=null;
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

                builder.show();
            }
        });

        if(GroupList == null)
            GroupList = new ArrayList<Integer>();
        context = this;
        lsvGroups = (LinearLayout) findViewById(R.id.lsvGroups);
        WebServices ws = new WebServices(this);
        GetGroups();
        ws.addQueue("ir.tsip.tracker.zarrintracker.GroupsActivity", 0, Tools.GetImei(this), "GroupsList");
   ws=null;
    }

    public void GetGroups() {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c;
        c = db.query(DatabaseContracts.Groups.TABLE_NAME, null, "", null, "", "", DatabaseContracts.Groups.COLUMN_NAME_LastTime + " DESC", "");
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_ID));

                Date date;
                String DateTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_LastTime));
                DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = iso8601Format.parse(DateTime);
                } catch (ParseException e) {
                    e.toString();
                    date = null;
                }

                byte[] Image = c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Image));
                String Message = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_LastMessage));
                String Name = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Name));
                if(GroupList.indexOf(id)<0)
                    GroupList.add(id);
                Bitmap B = ProfileActivity.getProfileImage(96,context.getApplicationContext());
                boolean isowner=false;
                if(Name.length()>2)
                    isowner=(Name.substring(Name.length()-3).contains(";;;"));
                Name=Name.replace(";;;","");
                if(Name=="")
                    Name="NoName";
                CreateGroupLayer(id,Name,"","",B,
                        //this condition define if group name contains ;;; . if true it means current device is owner of group
                        isowner);
           }
            while (c.moveToNext());
        }
        c.close();
        db.close();
        dbh.close();
    }

    public static void backWebServices(int ObjectCode, String Data) {
        if (ObjectCode == 0) {
            String groupname;
            boolean isowner=false;
            for (String s : Data.split(",")
                    ) {
                try {
                    if (s.length() > 1) {

                        if(s.split("~").length==1)
                            s+="NoName";
                        groupname= s.split("~")[1];
                        Integer gpID = Integer.valueOf(s.split("~")[0]);
                        if (GroupList.indexOf(gpID) >= 0) {
                            //Update group in database
                            UpdateGroup(gpID,groupname,"","",null);
                        } else {
                            GroupList.add(gpID);
isowner=false;
                            //this condition define if group name contains ;;; . if true it means current device is owner of group
                            if(groupname.length()>2)
                                isowner=(groupname.substring(groupname.length()-3).contains(";;;"));

                            InsertGroup(gpID,groupname, "", "", null);
                            groupname=groupname.replace(";;;","");
                            if(groupname=="")
                                groupname="NoName";
                            CreateGroupLayer(gpID, groupname, "", "", null, isowner );
                        }
                    }
                } catch (Exception ex) {
                    ex.toString();
                }
            }
        }
        else if (ObjectCode == 1) {
            if (Data.contains("1")) {
                Toast.makeText(_context, "Your device registered.", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(_context, "Code is not valid.", Toast.LENGTH_SHORT).show();

        }
    }

    private static void CreateGroupLayer(Integer gpID,String Name,String Time,String LastMessage,Bitmap img, final Boolean isGroupOwner)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_list, null);
        view.setId(new Random().nextInt());
        lsvGroups.addView(view);
        TextView tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
        tvGroupName.setText(Name + " "+_context.getResources().getString( R.string.GroupLabel));
        TextView tvLastGroupMessage = (TextView) view.findViewById(R.id.tvLastGroupMessage);
        if(img!=null) {
            ImageView ivImageGroup = (ImageView) view.findViewById(R.id.ivGroupPic);
            ivImageGroup.setImageBitmap(img);
        }
        tvLastGroupMessage.setText("");
        View.OnClickListener ClickOpenGroup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(context, ChatActivity.class);
                myIntent.putExtra("gpID", String.valueOf((int) v.getTag()));
                myIntent.putExtra("myGroup", isGroupOwner);
                context.startActivityForResult(myIntent, 1);
            }
        };

        LinearLayout llGroupList1 = (LinearLayout) view.findViewById(R.id.llGroupList1);
        LinearLayout llGroupList2 = (LinearLayout) view.findViewById(R.id.llGroupList2);
        LinearLayout llGroupList3 = (LinearLayout) view.findViewById(R.id.llGroupList3);
        LinearLayout llGroupList4 = (LinearLayout) view.findViewById(R.id.llGroupList4);
        LinearLayout llGroupList5 = (LinearLayout) view.findViewById(R.id.llGroupList5);

        llGroupList1.setTag(gpID);
        llGroupList2.setTag(gpID);
        llGroupList3.setTag(gpID);
        llGroupList4.setTag(gpID);
        llGroupList5.setTag(gpID);

        llGroupList1.setOnClickListener(ClickOpenGroup);
        llGroupList2.setOnClickListener(ClickOpenGroup);
        llGroupList3.setOnClickListener(ClickOpenGroup);
        llGroupList4.setOnClickListener(ClickOpenGroup);
        llGroupList5.setOnClickListener(ClickOpenGroup);
    }

    private static void InsertGroup(Integer gpID,String Name,String Time,String LastMessage,Bitmap img)
    {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Groups.COLUMN_NAME_ID,gpID);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_Name,Name);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_LastTime,Time);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_LastMessage,LastMessage);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_Image,Tools.getBytesFromBitmap(img));
        db.insert(DatabaseContracts.Groups.TABLE_NAME,DatabaseContracts.Groups.COLUMN_NAME_ID,V);
        db.close();
        dbh.close();
    }

    private static void UpdateGroup(Integer gpID,String Name,String Time,String LastMessage,Bitmap img)
    {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Groups.COLUMN_NAME_ID,gpID);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_Name,Name);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_LastTime,Time);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_LastMessage,LastMessage);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_Image,Tools.getBytesFromBitmap(img));
        db.update(DatabaseContracts.Groups.TABLE_NAME, V, DatabaseContracts.Groups.COLUMN_NAME_ID+"=?",new String[]{String.valueOf(gpID)});
        db.close();
        dbh.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1)
            if(resultCode==1)
                this.finish();
    }
}