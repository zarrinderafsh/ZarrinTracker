package ir.tsip.tracker.zarrintracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.widget.Toast;

import java.util.Locale;
import java.util.Set;


public class SettingActivity extends AppCompatActivity {
Button btnClearEvents;
    Switch swchMute,swchVisibilityState,swchmygroupmembercantseeeachother;
    RadioGroup rdgp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
                 super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_setting);


      swchVisibilityState=(Switch)findViewById(R.id.swchVisibilityState);
        swchVisibilityState.setChecked(Tools.VisibleToOwnGroupMembers);
        swchVisibilityState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebServices w = new WebServices(SettingActivity.this);
                w.addQueue("ir.tsip.tracker.zarrintracker.SettingActivity", 0, Tools.GetImei(SettingActivity.this), "VisibilityState", 1);
                w = null;
                Tools.VisibleToOwnGroupMembers = !Tools.VisibleToOwnGroupMembers;
                Tools.SetBoleanColumn(DatabaseContracts.Settings.TABLE_NAME,DatabaseContracts.Settings.Column_visibility,Tools.VisibleToOwnGroupMembers);
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
                Toast.makeText(SettingActivity.this, getString(R.string.eventscleared), Toast.LENGTH_SHORT).show();
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


        swchmygroupmembercantseeeachother=(Switch)findViewById(R.id.swchjustadminsee);
        swchmygroupmembercantseeeachother.setChecked(Tools.justadminsee);
        swchmygroupmembercantseeeachother.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WebServices w = new WebServices(SettingActivity.this);
                w.addQueue("ir.tsip.tracker.zarrintracker.SettingActivity", 1, Tools.GetImei(SettingActivity.this), "justadminsee", 1);
                w = null;
                Tools.justadminsee = !Tools.justadminsee;
                Tools.SetBoleanColumn(DatabaseContracts.Settings.TABLE_NAME,DatabaseContracts.Settings.COlumn_justAdminsee,Tools.justadminsee);
            }
        });

        if(!Tools.isOnline(this))
        {
            swchVisibilityState.setEnabled(false);
            swchmygroupmembercantseeeachother.setEnabled(false);
        }

        rdgp=(RadioGroup)findViewById(R.id.rdbgLanguage);
        RadioButton r = new RadioButton(this);
        r.setText("فارسی");
        r.setId(Integer.valueOf(getString(R.string.radiofa)));
        rdgp.addView(r);
        RadioButton r1 = new RadioButton(this);
        r1.setText("English");
        r.setId(Integer.valueOf(getString(R.string.radioen)));
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


//WebServices w =new WebServices();
//       byte[] k= w.EncryptData("hi");
//        byte[] v=w.DecryptData(k.toString());
//String h=v.toString();

    }


}
