package ir.tsip.tracker.zarrintracker;


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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    ImageView ivPause;
    ImageView ivHelp, ivGps;
    ImageView ivPersonImage;
    GoogleMap googleMap;
    ImageButton ibtnChat, ibtnRoutes, ibtnUp, ibtnDown, ibtnGeofences, ibtnOfflineTracking;
    TextView tvPersonName;
    Timer _TimerMain;
    int height;
    TextView tvPause;
    MessageEvent MEvent;
    LinearLayout lytEventsAndProfile, lytProfile, lytHeaderTop;
    RelativeLayout.LayoutParams lytEventsAndProfileparams;
    public static MainActivity Base;
    // nav drawer title
    private CharSequence mDrawerTitle;
    ListView lsvtest;
    // used to store app title
    private CharSequence mTitle;
    android.support.v4.widget.DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    MapFragment mMapFragment;
    private HashMap params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_main);
        Base = this;
        if(Persons.con==null){
            if(LocationListener.mContext==null)
                Persons.con=MainActivity.Base;
            else
                Persons.con=LocationListener.mContext;
        }

        if (Tools.markers != null) {
            Tools.markers.clear();
        }
        if (ChatActivity._this == null) {
            ChatActivity._this = new ChatActivity();
            ChatActivity.context = getApplicationContext();
        }
        Tools.lsvMarkers = (ListView) MainActivity.Base.findViewById(R.id.lsvMarkers);

        Tools.setTitleColor(this);
        checkRegistration();

        GroupsActivity.GetGroups(Base, false);
        ShowMessage();
        StartServices();


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

        // ivHelp = (ImageView) findViewById(R.id.ivHelp);
        ((LinearLayout) findViewById(R.id.lytSos)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                HelpDialog();
            }
        });


        lytProfile = (LinearLayout) findViewById(R.id.lytProfile);
        lytHeaderTop = (LinearLayout) findViewById(R.id.lytTopHeader);

        lytEventsAndProfile = ((LinearLayout) MainActivity.this.findViewById(R.id.lytEventsAndProfile));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //int width=dm.widthPixels;
        height = dm.heightPixels;

        ibtnUp = (ImageButton) findViewById(R.id.ibtnUp);
        ibtnUp.setVisibility(View.GONE);
        ibtnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lytEventsAndProfileparams = (RelativeLayout.LayoutParams) lytEventsAndProfile.getLayoutParams();

                if (lytEventsAndProfileparams.topMargin <= (int) (height / 2) - lytProfile.getHeight()) {
                    lytEventsAndProfileparams.setMargins(0, 0, 0, 0);
                    ibtnUp.setVisibility(View.GONE);
                    ((TextView)MainActivity.this.findViewById(R.id.txtMapHint)).setVisibility(View.VISIBLE);
                    ibtnDown.setVisibility(View.VISIBLE);
                } else if (lytEventsAndProfileparams.topMargin != 0 && lytEventsAndProfileparams.topMargin <= height - lytProfile.getHeight() - lytHeaderTop.getHeight() - 15) {
                    ibtnUp.setVisibility(View.VISIBLE);
                    ibtnDown.setVisibility(View.VISIBLE);
                    lytEventsAndProfileparams.setMargins(0, (int) (height / 2) - lytProfile.getHeight() - 50, 0, 0);
                }
                lytEventsAndProfile.setLayoutParams(lytEventsAndProfileparams);
            }
        });
        ibtnDown = (ImageButton) findViewById(R.id.ibtnDown);
        ibtnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lytEventsAndProfileparams = (RelativeLayout.LayoutParams) lytEventsAndProfile.getLayoutParams();
                if (lytEventsAndProfileparams.topMargin == 0) {//TOP
                    ibtnUp.setVisibility(View.VISIBLE);
                    ibtnDown.setVisibility(View.VISIBLE);
                    ((TextView)MainActivity.this.findViewById(R.id.txtMapHint)).setVisibility(View.GONE);
                    lytEventsAndProfileparams.setMargins(0, (int) (height / 2) - lytProfile.getHeight(), 0, 0);
                } else if (lytEventsAndProfileparams.topMargin <= (int) (height / 2) - lytProfile.getHeight()) {//midle
                    ibtnUp.setVisibility(View.VISIBLE);
                    ibtnDown.setVisibility(View.INVISIBLE);
                    lytEventsAndProfileparams.setMargins(0, height - lytProfile.getHeight() - lytHeaderTop.getHeight() - 15, 0, 0);
                }
                lytEventsAndProfile.setLayoutParams(lytEventsAndProfileparams);
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


        ivGps = (ImageView) findViewById(R.id.ivGPS);
        View.OnClickListener GPSClick = new View.OnClickListener() {
            public void onClick(View v) {
                Tools.turnGPSOnOff(Base);
            }
        };
        ivGps.setOnClickListener(GPSClick);

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.llMapLoad, mMapFragment);
        fragmentTransaction.commit();

//        ibtnGeofences = (ImageButton) findViewById(R.id.ibtnGeofences);
        ((LinearLayout) findViewById(R.id.lytPlaces)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, Places.class);
                Base.startActivity(myIntent);
            }
        });
        //   ibtnOfflineTracking = (ImageButton) findViewById(R.id.ibtnOfflineTracking);
        ((LinearLayout) findViewById(R.id.lytTracking)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, OfflineMap.class);
                Base.startActivity(myIntent);
            }
        });
        //    ibtnChat = (ImageButton) findViewById(R.id.ibtnChat);
        ((LinearLayout) findViewById(R.id.lytChat)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, GroupsActivity.class);
                Base.startActivity(myIntent);
            }
        });
        //   ibtnRoutes = (ImageButton) findViewById(R.id.ibtnRoutes);
        ((LinearLayout) findViewById(R.id.lytRoutes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Base, RoutesActivity.class);
                Base.startActivity(myIntent);
            }
        });
        initializeDrawer();

//        IntentFilter filter = new IntentFilter("ir.tstracker.activity.proximity");
//        registerReceiver(new ProximityIntentReceiver(), filter);
        initializeInviteButton();

        lytEventsAndProfileparams = (RelativeLayout.LayoutParams) lytEventsAndProfile.getLayoutParams();
        if (lytEventsAndProfileparams.topMargin == 0) {//TOP
            ibtnUp.setVisibility(View.VISIBLE);
            ibtnDown.setVisibility(View.VISIBLE);
            ((TextView)MainActivity.this.findViewById(R.id.txtMapHint)).setVisibility(View.GONE);
            lytEventsAndProfileparams.setMargins(0, (int) (height / 2) - lytProfile.getHeight(), 0, 0);
        }
        lytEventsAndProfile.setLayoutParams(lytEventsAndProfileparams);


        ImageButton ibtnHelp=(ImageButton)findViewById(R.id.ibtnHelp);
        ibtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, HelpActivity.class);
                i.putExtra("index", 3);
                startActivity(i);
            }
        });

    }

    private void initializeDrawer() {

        /**********************************************************Set DrawLayout*/
        lsvtest = (ListView) findViewById(R.id.lsvtest);
        MenuItemsAdapter adapter = new MenuItemsAdapter(this);
        Objects.MenuItem menuTitle = new Objects().new MenuItem();
        menuTitle.id = -1;
        menuTitle.text = getResources().getString(R.string.appShortDescribe);
        menuTitle.image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        adapter.AddItem(menuTitle);
        Objects.MenuItem m0 = new Objects().new MenuItem();
        m0.id = 0;
        m0.text = EditProfileActivity.getName(this);
        m0.image = ProfileActivity.getProfileImage(96, this);
        adapter.AddItem(m0);
        Objects.MenuItem m1 = new Objects().new MenuItem();
        m1.id = 1;
        m1.text = getResources().getString(R.string.map);
        m1.image = BitmapFactory.decodeResource(getResources(), R.drawable.map);
        adapter.AddItem(m1);
        Objects.MenuItem m2 = new Objects().new MenuItem();
        m2.id = 2;
        m2.text = getResources().getString(R.string.groupsToChat);
        m2.image = BitmapFactory.decodeResource(getResources(), R.drawable.groups);
        adapter.AddItem(m2);
        Objects.MenuItem m3 = new Objects().new MenuItem();
        m3.id = 3;
        m3.text = getResources().getString(R.string.placesAndAreas);
        m3.image = BitmapFactory.decodeResource(getResources(), R.drawable.places);
        adapter.AddItem(m3);
        Objects.MenuItem m4 = new Objects().new MenuItem();
        m4.id = 4;
        m4.text = getResources().getString(R.string.chargeMyAccount);
        m4.image = BitmapFactory.decodeResource(getResources(), R.drawable.money);
        adapter.AddItem(m4);
        Objects.MenuItem m6 = new Objects().new MenuItem();
        m6.id = 6;
        m6.text = getResources().getString(R.string.routes);
        m6.image = BitmapFactory.decodeResource(getResources(), R.drawable.route);
        adapter.AddItem(m6);
        Objects.MenuItem m7 = new Objects().new MenuItem();
        m7.id = 7;
        m7.text = getResources().getString(R.string.offlinemap);
        m7.image = BitmapFactory.decodeResource(getResources(), R.drawable.offlie_map);
        adapter.AddItem(m7);
        Objects.MenuItem m8 = new Objects().new MenuItem();
        m8.id = 8;
        m8.text = getResources().getString(R.string.setting);
        m8.image = BitmapFactory.decodeResource(getResources(), R.drawable.setting);
        adapter.AddItem(m8);
        Objects.MenuItem m9 = new Objects().new MenuItem();
        m9.id = 9;
        m9.text = getResources().getString(R.string.introduction);
        m9.image = BitmapFactory.decodeResource(getResources(), R.drawable.introduction);
        adapter.AddItem(m9);
        Objects.MenuItem m5 = new Objects().new MenuItem();
        m5.id = 5;
        m5.text = getResources().getString(R.string.about);
        m5.image = BitmapFactory.decodeResource(getResources(), R.drawable.about);
        adapter.AddItem(m5);
        lsvtest.setAdapter(adapter);


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

            boolean isclose = true;

            public void onDrawerClosed(View view) {
                isclose = false;
                //getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                isclose = true;
                // getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        lsvtest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent myIntent;
                switch (position) {
                    case 1:
                        myIntent = new Intent(Base, ProfileActivity.class);
                        Base.startActivity(myIntent);
                        break;
                    case 2:

                    {
                        lytEventsAndProfileparams = (RelativeLayout.LayoutParams) lytEventsAndProfile.getLayoutParams();
                        lytEventsAndProfileparams.setMargins(0, height - lytProfile.getHeight() - lytHeaderTop.getHeight() - 15, 0, 0);
                        try {
                            ibtnUp.setVisibility(View.VISIBLE);
                            ibtnDown.setVisibility(View.INVISIBLE);
                        } catch (Exception ex) {

                        }
                        lytEventsAndProfile.setLayoutParams(lytEventsAndProfileparams);

                        break;
                    }
                    case 3:
                        myIntent = new Intent(Base, GroupsActivity.class);
                        Base.startActivity(myIntent);
                        break;
                    case 4:
                        myIntent = new Intent(Base, Places.class);
                        Base.startActivity(myIntent);
                        break;
                    case 5:
                        myIntent = new Intent(Base, PurchaseActivity.class);
                        myIntent.putExtra("msg", "Charge Account");
                        Base.startActivity(myIntent);
                        break;
                    case 6:
                        myIntent = new Intent(Base, RoutesActivity.class);
                        Base.startActivity(myIntent);
                        break;
                    case 7:
                        myIntent = new Intent(Base, OfflineMap.class);
                        Base.startActivity(myIntent);
                        break;
                    case 8:
                        myIntent = new Intent(Base, SettingActivity.class);
                        Base.startActivity(myIntent);
                        break;
                    case 9:
                        myIntent = new Intent(Base, IntroductionActivity.class);
                        myIntent.putExtra("sm",false);
                        Base.startActivity(myIntent);
                        break;
                    case 10:
                        myIntent = new Intent(Base, about.class);
                        Base.startActivity(myIntent);
                        break;
                }

            }
        });
        ((ImageView) findViewById(R.id.imgSwipetoright)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

    }

    private void initializeInviteButton() {
        ImageView inInvite = (ImageView) findViewById(R.id.ivInvite);

        inInvite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.InviteBuddies));
                LayoutInflater inflate = getLayoutInflater();
                View view = inflate.inflate(R.layout.activity_invate, null);
                ChatActivity.txtGeneratedJoinCode = (TextView) view.findViewById(R.id.txtGeneratedJOinCode);
                ChatActivity.imgLoading = (ImageView) view.findViewById(R.id.imgLoading);

                WebServices w = new WebServices(MainActivity.this);
                HashMap<String, String> params = new HashMap<>();
                params.put("imei", Tools.GetImei(getApplicationContext()));
                w.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 0, params, "GenerateJoinKey",1);
                w = null;
                builder.setView(view);
                builder.setPositiveButton(getResources().getString(R.string.send), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Tools.ShareText(EditProfileActivity.getName(Base) + " " + MainActivity.Base.getResources().getString(R.string.InvationMessage) + ChatActivity.txtGeneratedJoinCode.getText().toString(), MainActivity.this);

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
    }


    public static void backWebServices(int ObjectCode, String Data) throws JSONException {
        if (ObjectCode == 1) {
            insertDevice(Data);
        } else if (ObjectCode == 5)//purchasedetails
        {
            if (Float.valueOf(Data.split(",")[0]) > 0) {
                String msg =
                        MainActivity.Base.getResources().getString(R.string.creditAmount) + " " + Data.split(",")[2].replace(".0000","") +" "+Base.getResources().getString(R.string.rial) +"\n" +
                                Data.split(",")[1] + " " + MainActivity.Base.getResources().getString(R.string.groupCOunts) + "\n";
                msg += (Data.split(",")[0].equals("Infinity")) ? "" :String.valueOf(Math.round( Float.valueOf(Data.split(",")[0]))) + " " + MainActivity.Base.getResources().getString(R.string.DaysToRecharge);
                MessageEvent.InsertMessage(MainActivity.Base, msg, MessageEvent.CREADIT_EVENT);
                Tools.HasCredit = true;
            } else {
                MessageEvent.InsertMessage(MainActivity.Base, MainActivity.Base.getResources().getString(R.string.NoCredit), MessageEvent.CREADIT_EVENT);
                Tools.HasCredit = false;
            }
        } else if (ObjectCode == 4) {
            if (Data.length() > 1) {
                try {
                    //add new area to database
                    ContentValues Val = new ContentValues();
                    DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
                    SQLiteDatabase db = dbh.getReadableDatabase();
                    db.delete(DatabaseContracts.Geogences.TABLE_NAME, "", null);
                    db = dbh.getWritableDatabase();

                    String[] geos = Data.split("\\|");
                    //                               Name~Points~radius~clientAreaCode~0 Or 1|
                    for (String g : geos) {
                        Val = new ContentValues();
                        Val.clear();
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, g.split("~")[0]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, g.split("~")[1]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, g.split("~")[2]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_ID, g.split("~")[3]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_isOwner, Integer.valueOf(g.split("~")[4]));
                        Val.put(DatabaseContracts.Geogences.COLUMN_OwnerCOde, Integer.valueOf(g.split("~")[5]));
                        if (db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val) > 0) {

                        }
                    }
                    Tools.setupGeofences(Base);
                    db.close();
                    dbh.close();
                    db = null;
                    dbh = null;
                } catch (Exception er) {
                    String e = "";
                }
            }
        }
    }

    private void checkRegistration() {

        if(!Tools.isOnline(MainActivity.Base))
            return;
        //check if this device registered already
        params = new HashMap<>();
        // the POST parameters:
        params.put("pData", Tools.GetImei(this) + "/");// "351520060796671");
        WebServices WS = new WebServices(getApplicationContext());
        WS.addQueue("ir.tsip.tracker.zarrintracker.MainActivity", 1, params, "CheckRegistration",1);
        ProfileActivity.GetImageFromServer(this);
        ProfileActivity.GetProfileFromServer(this);
        getGeofencesFromServer();
        WS = null;
    }

    public void getGeofencesFromServer() {

        HashMap<String, String> params;
        params = new HashMap<>();
        params.put("imei", Tools.GetImei(getApplicationContext()));
        params.put("center", "0");
        params.put("radius", "0");
        params.put("name", "0");
        params.put("clientCode", "0");
        params.put("operation", "4");
        WebServices W = new WebServices(getApplicationContext());
        W.addQueue("ir.tsip.tracker.zarrintracker.MainActivity", 4, params, "GeofenceOperations",1);
        W = null;
    }

    public static void insertDevice(String Data) throws JSONException {

        String key = "";
        String logo = "Ts";
        String site = "tstracker.ir";
        String tell = "";
        String purchaseMsg="";
        if (Data != "null") {
            JSONObject jo = new JSONObject(Data);
            key = jo.getString("key");
            logo = jo.getString("logo");
            site = jo.getString("site");
            tell = jo.getString("tell");
            purchaseMsg= jo.getString("pmsg");
            if(jo.getString("visibility").equals("1"))
                Tools.VisibleToOwnGroupMembers=true;
            else
                Tools.VisibleToOwnGroupMembers=false;
        }
        DatabaseHelper dh;
        SQLiteDatabase db;
        dh = new DatabaseHelper(Base.getApplicationContext());
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
            values.put(DatabaseContracts.Settings.Column_purchase_message, purchaseMsg);
            values.put(DatabaseContracts.Settings.COLUMN_locale, "fa");
            values.put(DatabaseContracts.Settings.COLUMN_NAME_Accurate, "h");
            values.put(DatabaseContracts.Settings.COLUMN_NAME_interval, 5000);
            // Insert the new row, returning the primary key value of the new row

            long newRowId = db.insert(DatabaseContracts.Settings.TABLE_NAME, "", values);
            if (newRowId > 0) {
            }
            db.close();
            dh.close();
        }
    }

    /***************************************ext*****************************DrawerLayout Methods*/
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

    int counter = 0;
    Boolean isfirst = true;

    private void ShowMessage() {
        if (_TimerMain == null) {
            _TimerMain = new Timer(true);
        } else
            return;
        if (MEvent == null) {
            MEvent = new MessageEvent(Base);
        }

        _TimerMain.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if ((counter + 1) % 30 == 0) {
                                if (mMapFragment != null)
                                    Tools.setUpMap(mMapFragment.getMap(), getApplicationContext(), isfirst);
                                if (isfirst)
                                    isfirst = false;
                            }
                            //every 30 minutes
                            if (counter == 30 * 60) {
                                //get geofences
                                getGeofencesFromServer();
                            }
                            //every hour
                            if (counter == 0) {
                                //Update credit
                                if(!Tools.isOnline(MainActivity.Base))
                                    return;
                                WebServices ws = new WebServices(MainActivity.this);
                                ws.addQueue("ir.tsip.tracker.zarrintracker.MainActivity", 5, Tools.GetImei(MainActivity.this), "PurhaseDetails",1);
                                ws = null;
                                //Update persons images
                                Persons.UpdateImages();
                            }
                            //Counter repeat every one hour
                            if (counter >= 60 * 60 * 60) {
                                counter = 0;
                            }
                            counter++;
                            String Mes = MessageManager.GetMessage();
                            if (Mes.length() > 0) {
                                Toast.makeText(getBaseContext(), Mes, Toast.LENGTH_LONG).show();
                            }

                            Date date = new Date();
                            TextView TV = (TextView) findViewById(R.id.tvPause);
                            if (LocationListener.PauseDate != null && date.before(LocationListener.PauseDate)) {
                                long Second = (LocationListener.PauseDate.getTime() - date.getTime()) / 1000;
                                String S = (Second / 3600) + ":" +
                                        (Second % 3600) / 60 + ":" + (Second % 3600) % 60;
                                TV.setText(S);
                            } else {
                                TV.setText(getResources().getString(R.string.pause));
                            }
                            date = null;

                            if (!LocationListener.isGPSEnabled && !LocationListener.isNetworkEnabled) {
                                ivGps.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_off));
                            } else {
                                ivGps.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.gps_on));
                            }

                            LinearLayout SV = (LinearLayout) findViewById(R.id.llinSroll);
                            MEvent.ShowMessage(SV, MEvent.FirstDate, MEvent.Lastdate, false);
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
        if (googleMap == null) {
            googleMap = Tools.initGoogleMap(mMapFragment);
            //setupGeofences();
        }
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
        Toast.makeText(this, getResources().getString(R.string.clickBackAgain), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static Boolean IsPaused = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        IsPaused = !hasFocus;
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
        super.onStop();
        _TimerMain.cancel();
        _TimerMain = null;
    }

    private void PauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(MainActivity.this.getResources().getString(R.string.pausetracking));

        final RadioGroup RG = new RadioGroup(this);

        final RadioButton R1 = new RadioButton(this);
        final RadioButton R3 = new RadioButton(this);
        final RadioButton R6 = new RadioButton(this);
        final RadioButton R12 = new RadioButton(this);

        R1.setText("1 " + getResources().getString(R.string.hour));
        R1.setTag(1);
        R3.setText("3 " + getResources().getString(R.string.hour));
        R3.setTag(3);
        R6.setText("6 " + getResources().getString(R.string.hour));
        R6.setTag(6);
        R12.setText("12 " + getResources().getString(R.string.hour));
        R12.setTag(12);

        RG.addView(R1);
        RG.addView(R3);
        RG.addView(R6);
        RG.addView(R12);

        builder.setView(RG);

        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    int id = RG.getCheckedRadioButtonId();
                    int hour = (int) ((RadioButton) RG.findViewById(id)).getTag();
                    LocationListener.StartPause(hour);
                    (new EventManager(MainActivity.Base)).AddEvevnt(MainActivity.Base.getResources().getString(R.string.pausetracking), "-4", MessageEvent.Pause_Event);
                    MainActivity.this.ivPause.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_pause));
                } catch (Exception ex) {

                }

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocationListener.StartPause(0);
                dialog.cancel();
                MainActivity.this.ivPause.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pause_off));
            }
        });

        builder.show();
    }

    int HelpCount = 0;

    private void HelpDialog() {
        HelpCount = 0;
        final Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(false);
        builder.setContentView(R.layout.helpdialog);

        final Timer _Timer = new Timer(true);

        final TextView tvShowTime = (TextView) builder.findViewById(R.id.tvHelpCounter);
        Button button = (Button) builder.findViewById(R.id.btCloseHelp);
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
                            if (HelpCount < 6) {
                                tvShowTime.setText(String.valueOf(HelpCount));
                                v.vibrate(100);
                            }
                            if (HelpCount >= 6) {
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
