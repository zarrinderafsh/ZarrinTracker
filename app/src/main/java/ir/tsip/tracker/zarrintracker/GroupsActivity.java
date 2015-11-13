package ir.tsip.tracker.zarrintracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class GroupsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
//
WebServices ws=new  WebServices(this);
        ws.addQueue("GroupsActivity",0,Tools.GetImei(this),"http://tstracker.ir/services/webbasedefineservice.asmx/GroupsList");
        lsvGroups=(ListView)findViewById(R.id.lsvGroups);
        lsvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent myIntent = new Intent(GroupsActivity.this, ChatActivity.class);
                myIntent.putExtra("gpID", ((EditText)view).getText().toString().split("-")[0]);
                GroupsActivity.this.startActivity(myIntent);
            }
        });
    }
    ListView lsvGroups;

    public  void backWebServices(int objectcode,String data){
                lsvGroups.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawerlistlayout, data.split(",")));
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
