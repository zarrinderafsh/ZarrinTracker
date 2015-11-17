package ir.tsip.tracker.zarrintracker;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import ir.tsip.tracker.zarrintracker.util.MapActivity;


public class MainActivity extends ActionBarActivity implements View.OnTouchListener {

    LinearLayout llTop;
    LinearLayout llDown;
    LinearLayout llMain;
    LinearLayout llmapLayout;
    ImageView ivPause;
    ImageView ivHelp;
    ImageView ivPersonImage;
    ImageView ivGPS;
    ImageView ivNetLocation;
    ImageView ivBattery;
    ImageView inInvite;
    GoogleMap googleMap;
    ImageView ivCloseMap;
    ImageView imgGroupJoin;
    ImageButton ibtnChat;
    TextView tvPersonName;
    Timer _TimerMain;
    TextView tvHelp;
    TextView tvPause;
    MessageEvent MEvent;

    Activity Base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_main);
        checkRegistration();
        Base = this;
        ShowMessage();
        StartServices();

        llTop = (LinearLayout) findViewById(R.id.llTop);
        llDown = (LinearLayout) findViewById(R.id.llDown);
        llMain = (LinearLayout) findViewById(R.id.llMain);
        llmapLayout = (LinearLayout) findViewById(R.id.mapLayout);
        LinearLayout.LayoutParams llD = (LinearLayout.LayoutParams) llDown.getLayoutParams();
        llD.height = Tools.GetDesktopSize(Base).y - ((LinearLayout.LayoutParams) llTop.getLayoutParams()).height;
        llDown.setLayoutParams(llD);

        ivPause = (ImageView) findViewById(R.id.ivPause);
        ivPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PauseDialog();
            }
        });

        tvPause = (TextView) findViewById(R.id.tvPause);
        tvPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PauseDialog();
            }
        });

        ivHelp = (ImageView) findViewById(R.id.ivHelp);
        ivHelp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelpDialog();
            }
        });

        tvHelp = (TextView) findViewById(R.id.tvHelp);
        tvHelp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelpDialog();
            }
        });

        //ivwIfI = (ImageView) findViewById(R.id.ivWiFi);
        ivGPS = (ImageView) findViewById(R.id.ivGPS);
        ivNetLocation = (ImageView) findViewById(R.id.ivNetLocation);
        ivBattery = (ImageView) findViewById(R.id.ivBattery);

        ivCloseMap = (ImageView) findViewById(R.id.ivCloseMap);
        ivCloseMap.setOnClickListener(new View.OnClickListener() {
                                          public void onClick(View v) {
                                              LinearLayout.LayoutParams lpTop = (LinearLayout.LayoutParams) llTop.getLayoutParams();
                                              LinearLayout.LayoutParams lpDown = (LinearLayout.LayoutParams) llDown.getLayoutParams();

                                              lpTop.topMargin = 0;
                                              lpDown.topMargin = 0;
                                              llMain.setAlpha(1);
                                              llTop.setLayoutParams(lpTop);
                                              llDown.setLayoutParams(lpDown);
                                              llMain.setVisibility(View.VISIBLE);
                                          }

                                          ;
                                      }
        );

        llTop.setOnTouchListener(this);
        llMain.setOnTouchListener(this);

        inInvite = (ImageView) findViewById(R.id.ivInvite);
        inInvite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, Invite.class);
                Base.startActivity(myIntent);
            }
        });

        View.OnClickListener ShowProfile = new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, ProfileActivity.class);
                Base.startActivity(myIntent);
            }
        };
        ivPersonImage = (ImageView) findViewById(R.id.ivPersonImage);
        ivPersonImage.setOnClickListener(ShowProfile);
        tvPersonName = (TextView) findViewById(R.id.tvPersonName);
        tvPersonName.setOnClickListener(ShowProfile);

        View.OnClickListener GPSClick = new View.OnClickListener() {
            public void onClick(View v) {
                Tools.turnGPSOnOff(Base);
            }
        };
        ivGPS.setOnClickListener(GPSClick);
        ivNetLocation.setOnClickListener(GPSClick);
        ivBattery.setOnClickListener(GPSClick);

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapLayout, mMapFragment);
        fragmentTransaction.commit();
        llmapLayout.setGravity(android.view.Gravity.BOTTOM);

        ibtnChat = (ImageButton) findViewById(R.id.ibtnChat);
        ibtnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, GroupsActivity.class);
                Base.startActivity(myIntent);
            }
        });

        imgGroupJoin = (ImageView) findViewById(R.id.ivGroup);
        imgGroupJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, JoinGroupActivity.class);
                Base.startActivity(myIntent);
            }
        });
        /**********************************************************Set DrawLayout*/
        lsvtest = (ListView) findViewById(R.id.lsvtest);

        lsvtest.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawerlistlayout, new String[]{"Map", "Invite", "Groups", "Chat", "About"}));

        lsvtest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                switch(position){
                    case 0:
                        //onTouch(lsvtest,new MotionEvent() );
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
        });

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.drawer_layout);
        // enabling action bar app icon and behaving it as toggle button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {

            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    // nav drawer title
    private CharSequence mDrawerTitle;
    ListView lsvtest;
    // used to store app title
    private CharSequence mTitle;
    android.support.v4.widget.DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    MapFragment mMapFragment;
    int StartTouchX = 0;
    int StartTouchY = 0;


    private HashMap params;
    private DatabaseHelper dh;
    private SQLiteDatabase db;
    private static RequestQueue queue;

    private void checkRegistration() {

        params = new HashMap<>();
        // the POST parameters:
        params.put("pData", Tools.GetImei(this) + "/");// "351520060796671");

        JSONObject jo1 = new JSONObject(params);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "http://tstracker.ir/services/webbasedefineservice.asmx/CheckRegistration", jo1, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    String data = response.getString("d");
                    JSONObject jo = new JSONObject(data);
                    String key = jo.getString("key");
                    String logo = jo.getString("logo");
                    String site = jo.getString("site");
                    String tell = jo.getString("tell");

                    dh = new DatabaseHelper(getApplicationContext());
                    if (key != null) {
                        db = dh.getWritableDatabase();

                        // Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_ID, 1);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_key, key);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_days, "0,1,2,3,4,5,6");
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_endTime, "14");
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_fromTime, "07");
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_logo, logo);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_site, site);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_tell, tell);
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_Accurate, "h");
                        values.put(DatabaseContracts.Settings.COLUMN_NAME_interval, 5000);
                        // Insert the new row, returning the primary key value of the new row

                        long newRowId = db.insert(DatabaseContracts.Settings.TABLE_NAME, "", values);
                        if (newRowId > 0) {
                        }
                        db.close();
                        dh.close();
                    }
                } catch (Exception er) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        });
        if (queue == null)
            queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(jsObjRequest);
    }

    /********************************************************************DrawerLayout Methods*/
    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(lsvtest);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override

    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * ***************************************************************
     */


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        LinearLayout.LayoutParams lpTop = (LinearLayout.LayoutParams) llTop.getLayoutParams();
        LinearLayout.LayoutParams lpDown = (LinearLayout.LayoutParams) llDown.getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (-lpTop.topMargin >= lpTop.height)
                    lpDown.topMargin = Tools.GetDesktopSize(this).y - lpTop.height;

                StartTouchX = x;
                StartTouchY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (lpDown.topMargin > 45) {
                    lpTop.topMargin = -lpTop.height;
                    lpDown.topMargin = Tools.GetDesktopSize(this).y + lpTop.height / 2;
                    llMain.setVisibility(View.INVISIBLE);
                } else {
                    lpTop.topMargin = 0;
                    lpDown.topMargin = 0;
                    llMain.setVisibility(View.VISIBLE);
                }
            case MotionEvent.ACTION_MOVE:
                Point p = Tools.GetDesktopSize(this);

                int distancY = (StartTouchY - y);

                if (lpTop.topMargin >= 0 && distancY >= 0)
                    return false;

                if (lpDown.topMargin >= p.y && distancY <= 0)
                    return false;

                lpTop.topMargin = lpTop.topMargin + (distancY);
                lpDown.topMargin = lpDown.topMargin - (distancY) * 2;
                llMain.setAlpha(llMain.getAlpha() + (float) (((distancY * 100.0) / p.y) / 100.0) * 2);


                if (lpTop.topMargin > 0) {
                    lpTop.topMargin = 0;
                    lpDown.topMargin = 0;
                }

                llTop.setLayoutParams(lpTop);
                llDown.setLayoutParams(lpDown);

                StartTouchX = x;
                StartTouchY = y;
                break;
            default:
                break;
        }
        return true;
        // if you want to consume the behavior then return true else retur false
    }

    private void StartServices() {
        Timer _Timer = new Timer(true);
        _Timer.schedule(new TimerTask() {
            @Override
            public void run() {

                // Start Location Service
                ServiceManager.StartService(getBaseContext(), SendDataService.class);
                ServiceManager.StartService(getBaseContext(), LocationListener.class);

                Base.runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });
            }

        }, 0, 10000);
    }

    private void ShowMessage() {
        if(_TimerMain == null) {
            _TimerMain = new Timer(true);
        }
        else
            return;
        if(MEvent == null) {
            MEvent = new MessageEvent(Base);
        }
        _TimerMain.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (mMapFragment != null)
                                Tools.setUpMap(mMapFragment.getMap(), getApplicationContext());

                            String Mes = MessageManager.GetMessage();
                            if (Mes.length() > 0) {
                                Toast.makeText(getBaseContext(), Mes, Toast.LENGTH_LONG).show();
                            }

                            Date date= new Date();
                            TextView TV = (TextView) findViewById(R.id.tvPause);
                            if(LocationListener.PauseDate!=null && date.before(LocationListener.PauseDate))
                            {
                                long Second = (LocationListener.PauseDate.getTime() - date.getTime()) / 1000;
                                String S = (Second / 3600)+":"+
                                (Second % 3600) / 60 +":"+ (Second % 3600) % 60;
                                TV.setText(S);
                            }
                            else
                            {
                                TV.setText("Pause");
                            }
                            date = null;

                            if (LocationListener.isGPSEnabled) {
                                //ivGPS.setVisibility(View.VISIBLE);
                                ivBattery.setImageResource(R.drawable.battery_caution);
                                ivGPS.setImageResource(R.drawable.satellite_48_hot);
                            } else {
                                //ivGPS.setVisibility(View.INVISIBLE);
                                ivBattery.setImageResource(R.drawable.battery);
                                ivGPS.setImageResource(R.drawable.satellite_cancel);
                            }

                            if (LocationListener.isNetworkEnabled) {
                                //ivNetLocation.setVisibility(View.VISIBLE);
                                ivNetLocation.setImageResource(R.drawable.rss);
                            } else {
                                //ivNetLocation.setVisibility(View.INVISIBLE);
                                ivNetLocation.setImageResource(R.drawable.antenna_delete);
                            }

                            LinearLayout SV = (LinearLayout) findViewById(R.id.llinSroll);
                            MEvent.ShowMessage(SV,MEvent.FirstDate,MEvent.Lastdate);
                        }
                    });
                } catch (Exception ex) {
                    ex.toString();
                }
            }

        }, 0, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (googleMap == null)
            googleMap = Tools.initGoogleMap(mMapFragment);
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
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView) findViewById(R.id.tvPersonName)).setText(EditProfileActivity.getName(this.getBaseContext()));
        ProfileActivity.setProfileImage(ivPersonImage, 96, Base);
        ShowMessage();
    }

    @Override
    public void onStop() {
        super.onStart();
        _TimerMain.cancel();
        _TimerMain=null;
    }

    private void PauseDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pause GPS");

        final RadioGroup RG = new RadioGroup(this);

        final RadioButton R1 = new RadioButton(this);
        final RadioButton R3 = new RadioButton(this);
        final RadioButton R6 = new RadioButton(this);
        final RadioButton R12 = new RadioButton(this);

        R1.setText("1 houre");
        R1.setTag(1);
        R3.setText("3 houre");
        R3.setTag(3);
        R6.setText("6 houre");
        R6.setTag(6);
        R12.setText("12 houre");
        R12.setTag(12);

        RG.addView(R1);
        RG.addView(R3);
        RG.addView(R6);
        RG.addView(R12);

        builder.setView(RG);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int id = RG.getCheckedRadioButtonId();
                    int hour = (int) ((RadioButton) RG.findViewById(id)).getTag();
                    LocationListener.StartPause(hour);
                }
                catch (Exception ex)
                {

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocationListener.StartPause(0);
                dialog.cancel();
            }
        });

        builder.show();
    }

    int HelpCount = 0;
    private void HelpDialog()
    {
        HelpCount = 0;
        final Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(false);
        builder.setContentView(R.layout.helpdialog);

        final Timer _Timer = new Timer(true);

        final TextView tvShowTime = (TextView)builder.findViewById(R.id.tvHelpCounter);
        Button button = (Button)builder.findViewById(R.id.btCloseHelp);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _Timer.cancel();
                builder.dismiss();
            }
        });

        final Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        _Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HelpCount++;
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(HelpCount < 6) {
                                tvShowTime.setText(String.valueOf(HelpCount));
                                v.vibrate(500);
                            }
                            if(HelpCount >=6)
                            {
                                EventManager E = new EventManager(getApplicationContext());
                                E.SendSOS();
                                _Timer.cancel();
                                builder.dismiss();
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.toString();
                }
            }

        }, 0, 1000);

        builder.show();
    }

}
