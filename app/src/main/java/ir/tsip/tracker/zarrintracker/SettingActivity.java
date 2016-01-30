package ir.tsip.tracker.zarrintracker;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.Locale;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {
Button btnClearEvents;
    Switch swchMute,swchVisibilityState;
    RadioGroup rdgp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


      swchVisibilityState=(Switch)findViewById(R.id.swchVisibilityState);
        swchVisibilityState.setChecked(Tools.VisibleToOwnGroupMembers);
        swchVisibilityState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebServices w=new WebServices(SettingActivity.this);
                w.addQueue("",0,Tools.GetImei(SettingActivity.this),"VisibilityState");
                w=null;
                if (Tools.VisibleToOwnGroupMembers) {
                    Tools.VisibleToOwnGroupMembers = false;
                } else {
                    Tools.VisibleToOwnGroupMembers = true;
                }
            }
        });

        btnClearEvents=(Button)findViewById(R.id.btnClearAllEvents);
        btnClearEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper dbh = new DatabaseHelper(SettingActivity.this);
                SQLiteDatabase db = dbh.getReadableDatabase();
                db.delete(DatabaseContracts.Events.TABLE_NAME, "", null);
                db.close();
                dbh.close();
                ((LinearLayout) MainActivity.Base.findViewById(R.id.llinSroll)).removeAllViews();
            }
        });

        swchMute=(Switch)findViewById(R.id.swchMute);
        swchMute.setChecked(Tools.Mute);
        swchMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Tools.Mute) {
                    Tools.Mute = false;
                    Tools.SetMute(false);
                } else {
                    Tools.Mute = true;
                    Tools.SetMute(true);
                }
            }
        });

        rdgp=(RadioGroup)findViewById(R.id.rdbgLanguage);
        RadioButton r = new RadioButton(this);
        r.setText("فارسی");
        r.setId(0);
        rdgp.addView(r);
        RadioButton r1 = new RadioButton(this);
        r1.setText("English");
        r1.setId(1);
        rdgp.addView(r1);
        String loe=Tools.getLocale(this);
        if(loe.equals("fa") ) {
            r.setChecked(true);
            rdgp.setTag(0);
        }
        if(loe.equals("en") ) {
            r1.setChecked(true);
            rdgp.setTag(1);
        }
        rdgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (group.getTag().equals(checkedId))
                    return;
                group.setTag(checkedId);
                Locale locale;
                if (checkedId == 0)//persian
                {
                    Tools.SetLocale("fa");
                    locale = new Locale("fa");
                } else//english
                {

                    Tools.SetLocale("en");
                    locale = new Locale("en");
                }
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());

                SettingActivity.this.finish();
                Intent intent = MainActivity.Base.getIntent();
                MainActivity.Base.finish();
                MainActivity.Base.startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
