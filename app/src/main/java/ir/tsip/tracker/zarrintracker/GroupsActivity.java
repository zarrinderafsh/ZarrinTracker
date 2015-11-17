package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class GroupsActivity extends AppCompatActivity {

    static LinearLayout lsvGroups;
    static Activity context;
    static List<Integer> GroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        if(GroupList == null)
            GroupList = new ArrayList<Integer>();
        context = this;
        lsvGroups = (LinearLayout) findViewById(R.id.lsvGroups);
        WebServices ws = new WebServices(this);
        GetGroups();
        ws.addQueue("ir.tsip.tracker.zarrintracker.GroupsActivity", 0, Tools.GetImei(this), "GroupsList");
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
                CreateGroupLayer(id,Name,"",Message,Tools.LoadImage(Image,96));
            }
            while (c.moveToNext());
        }
        c.close();
        db.close();
        dbh.close();
    }

    public static void backWebServices(int ObjectCode, String Data) {
        for (String s : Data.split(",")
                ) {
            try {
                if (s.length() > 2) {

                    Integer gpID = Integer.valueOf(s.split("~")[0]);
                    if(GroupList.indexOf(gpID)>=0) {
                    }
                    else {
                        GroupList.add(gpID);
                        InsertGroup(gpID, s.split("~")[1], "", "", null);
                        CreateGroupLayer(gpID, s.split("~")[1], "", "", null);
                    }
                }
            } catch (Exception ex) {
                ex.toString();
            }
        }
    }

    private static void CreateGroupLayer(Integer gpID,String Name,String Time,String LastMessage,Bitmap img)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.group_list, null);
        view.setId(new Random().nextInt());
        lsvGroups.addView(view);
        TextView tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
        tvGroupName.setText(Name);

        TextView tvLastGroupMessage = (TextView) view.findViewById(R.id.tvLastGroupMessage);
        tvLastGroupMessage.setText("");

        View.OnClickListener ClickOpenGroup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent myIntent = new Intent(context, ChatActivity.class);
                myIntent.putExtra("gpID", String.valueOf((int) v.getTag()));
                context.startActivity(myIntent);
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

}