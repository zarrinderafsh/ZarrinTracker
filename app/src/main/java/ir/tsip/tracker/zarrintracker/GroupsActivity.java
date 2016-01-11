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
    static ArrayList<Integer> GroupList= new ArrayList<Integer>();
    //static Context _context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        if (GroupList == null)
            GroupList = new ArrayList<Integer>();
        imgGroupJoin = (ImageView) findViewById(R.id.ivGroup);
        imgGroupJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsActivity.this);
                builder.setTitle(getResources().getString(R.string.joinGroups));
                LayoutInflater inflate = GroupsActivity.this.getLayoutInflater();
                View view = inflate.inflate(R.layout.activity_join_group, null);
                builder.setView(view);
                final EditText txtCode = (EditText) view.findViewById(R.id.txtJoinCode);
                builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("key", txtCode.getText().toString());
                            params.put("imei", Tools.GetImei(getApplicationContext()));
                            WebServices ws = new WebServices(GroupsActivity.this);
                            ws.addQueue("ir.tsip.tracker.zarrintracker.GroupsActivity", 1, params, "AddDevice");
                            ws = null;
                        } catch (Exception ex) {

                        }

                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        WebServices ws = new WebServices(this);
        ws.addQueue("ir.tsip.tracker.zarrintracker.GroupsActivity", 0, Tools.GetImei(this), "GroupsList");
        ws=null;
        lsvGroups = (LinearLayout) findViewById(R.id.lsvGroups);
        GetGroups(context, true);
    }

    public static void GetGroups(Context _context,Boolean createLayers) {
        DatabaseHelper dbh = new DatabaseHelper(_context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c = db.query(DatabaseContracts.Groups.TABLE_NAME, null, "", null, "", "", DatabaseContracts.Groups.COLUMN_NAME_LastTime + " DESC", "");
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
                if (GroupList.indexOf(id) < 0)
                    GroupList.add(id);
                Bitmap B=Tools.LoadImage(Image,96);
                boolean isowner = false;
                if (Name.length() > 2)
                    isowner = (Name.substring(Name.length() - 3).contains(";;;"));
                if(isowner && Image==null)
                    B = ProfileActivity.getProfileImage(96, _context.getApplicationContext());
                Name = Name.replace(";;;", "");
                if (Name == "")
                    Name = "NoName";
                if(createLayers)
                CreateGroupLayer(id, Name, "", Message, B,
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
            boolean isowner = false;
            //this condition is true if user left a group
            if(GroupList.size()>Data.split(",").length) {
                ArrayList<Integer> newList=new ArrayList<Integer>() ;
                for (Integer i : GroupList) {
                    if (!Data.contains(i + "~")) {
                        DatabaseHelper dh = new DatabaseHelper(MainActivity.Base);
                        SQLiteDatabase db = dh.getReadableDatabase();
                        //Delete item from database
                        if (db.delete(DatabaseContracts.Groups.TABLE_NAME, DatabaseContracts.Groups.COLUMN_NAME_ID + " in (" + String.valueOf(i) + ")", null) > 0) {
                        }
                        //Delete all the messages of the group
                        if (db.delete(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_Group + " in (" +String.valueOf(i)+ ")", null) > 0) {
                        }
                        db.close();
                        dh.close();
                        dh = null;
                    } else
                        newList.add(i);
                }
                GroupList=newList;
            }
            for (String s : Data.split(",")) {
                try {
                    if (s.length() > 1) {

                        if (s.split("~").length == 1)
                            s += "NoName";
                        groupname = s.split("~")[1];
                        Persons p=new Persons();
                        p.ID=Integer.valueOf(s.split("~")[2].replace(";;;",""));
                        p.GetData(p.ID);
                        Integer gpID = Integer.valueOf(s.split("~")[0]);
                        if (GroupList.indexOf(gpID) >= 0) {

                            //Update group in database
                            UpdateGroup(gpID, groupname,null, null,p.image,s.split("~")[3]);
                        } else {
                            GroupList.add(gpID);
                            isowner = false;
                            //this condition define if group name contains ;;; . if true it means current device is owner of group
                            if (groupname.length() > 2)
                                isowner = (groupname.substring(groupname.length() - 3).contains(";;;"));
                            if(isowner && p.image==null)
                            {
                                p.ID=p.ID;
                                p.image=ProfileActivity.getProfileImage(96,MainActivity.Base);
                                p.name=EditProfileActivity.getName(MainActivity.Base);
                                p.isme=true;
                                p.Save();
                            }
                             InsertGroup(gpID, groupname, "", "", p.image,s.split("~")[3]);

                            groupname = groupname.replace(";;;", "");
                            if (groupname == "")
                                groupname = "NoName";

                            CreateGroupLayer(gpID, groupname, "", "",Tools.LoadImage( p.image,96), isowner);
                        }
                    }
                } catch (Exception ex) {
                    ex.toString();
                }
            }
        } else if (ObjectCode == 1) {
            if (Data.equals("1")) {

                WebServices ws = new WebServices(context);
                ws.addQueue("ir.tsip.tracker.zarrintracker.GroupsActivity", 0, Tools.GetImei(context), "GroupsList");
                ws=null;
                Toast.makeText(context, context.getResources().getString(R.string.deviceREgistered), Toast.LENGTH_SHORT).show();
            } else if (Data.equals( "-1"))
                Toast.makeText(context, context.getResources().getString(R.string.devicesAlreadyRegistered), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context,context.getResources().getString(R.string.InvalidCOde), Toast.LENGTH_SHORT).show();

        }
    }

    private static void CreateGroupLayer(Integer gpID, String Name, String Time, String LastMessage, Bitmap img, final Boolean isGroupOwner) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_list, null);
        view.setId(new Random().nextInt());
        lsvGroups.addView(view);
        TextView tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
        tvGroupName.setText(Name + " " + context.getResources().getString(R.string.GroupLabel));
        TextView tvLastGroupMessage = (TextView) view.findViewById(R.id.tvLastGroupMessage);
        if (img != null) {
            ImageView ivImageGroup = (ImageView) view.findViewById(R.id.ivGroupPic);
            ivImageGroup.setImageBitmap(img);
        }
        tvLastGroupMessage.setText(LastMessage);
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

    private static void InsertGroup(Integer gpID, String Name, String Time, String LastMessage, Bitmap img,String Members) {
        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Groups.COLUMN_NAME_ID, gpID);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_Name, Name);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_LastTime, Time);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_LastMessage, LastMessage);
        V.put(DatabaseContracts.Groups.COLUMN_NAME_Image, Tools.getBytesFromBitmap(img));
        if(Members!=null)
            V.put(DatabaseContracts.Groups.COLUMN_NAME_Members,Members);
        db.insert(DatabaseContracts.Groups.TABLE_NAME, DatabaseContracts.Groups.COLUMN_NAME_ID, V);
        db.close();
        dbh.close();
    }

    public static void UpdateGroup(Integer gpID, String Name, String Time, String LastMessage, Bitmap img,String Members) {
        DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Groups.COLUMN_NAME_ID, gpID);
        if (Name != null)
            V.put(DatabaseContracts.Groups.COLUMN_NAME_Name, Name);
        if (Time != null)
            V.put(DatabaseContracts.Groups.COLUMN_NAME_LastTime, Time);
        if (LastMessage != null)
            V.put(DatabaseContracts.Groups.COLUMN_NAME_LastMessage, LastMessage);
        if (img != null)
            V.put(DatabaseContracts.Groups.COLUMN_NAME_Image, Tools.getBytesFromBitmap(img));
        if(Members!=null)
            V.put(DatabaseContracts.Groups.COLUMN_NAME_Members,Members);
        db.update(DatabaseContracts.Groups.TABLE_NAME, V, DatabaseContracts.Groups.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(gpID)});
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
        if (requestCode == 1)
            if (resultCode == 1)
                this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lsvGroups.removeAllViewsInLayout();
        GetGroups(context,true);
    }
}