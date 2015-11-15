package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
//
        context=this;
WebServices ws=new  WebServices(this);
        ws.addQueue("ir.tsip.tracker.zarrintracker.GroupsActivity",0,Tools.GetImei(this),"GroupsList");
        lsvGroups=(ListView)findViewById(R.id.lsvGroups);
        lsvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent myIntent = new Intent(GroupsActivity.this, ChatActivity.class);
//                ((TextView) view).getText().toString().split("~")[0]

                myIntent.putExtra("gpID",gpIDs.get(position) );

                GroupsActivity.this.startActivity(myIntent);
            }
        });
    }

    static ListView lsvGroups;
static Activity context;
    static  GroupListAdapter adapter;
    static ArrayList<String> gpIDs=new ArrayList<>();

    public static  void backWebServices (int ObjectCode, String Data) {
        java.util.ArrayList<Objects.GroupListItem> Items = new ArrayList<>();
        for (String s : Data.split(",")
                ) {
            if (s.length() > 2) {
                Items.add(new Objects().new GroupListItem(s.split("~")[1],"0"));
                gpIDs.add(s.split("~")[0]);
            }
        }
        adapter=new GroupListAdapter(context,Items);
        lsvGroups.setAdapter(adapter);
      //  adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_groups, menu);
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

