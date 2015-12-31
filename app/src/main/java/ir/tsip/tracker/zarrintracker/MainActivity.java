package ir.tsip.tracker.zarrintracker;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    LinearLayout llTop;
    LinearLayout llDown;
    public static LinearLayout llMain;
    LinearLayout llmapLayout;
    ImageView ivPause;
    ImageView ivHelp;
    ImageView ivPersonImage;
    ImageView ivGPS;
    ImageView ivNetLocation;
    ImageView ivBattery;
    ImageView ivArrowDown;
    GoogleMap googleMap;
    ImageView ivCloseMap;
    ImageButton ibtnChat;
    TextView tvPersonName;
    Timer _TimerMain;
    TextView tvHelp;
    TextView tvPause;
    MessageEvent MEvent;
    public  LinearLayout.LayoutParams lpTop;
    public   LinearLayout.LayoutParams lpDown;
   public static Activity Base;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_main);
        Base = this;

        if(Tools.markers!=null) {
            Tools.markers.clear();
        }
        if(ChatActivity._this==null) {
            ChatActivity._this = new ChatActivity();
            ChatActivity.context = getApplicationContext();
        }
        Tools.lsvMarkers = (HorizontalListView) MainActivity.Base.findViewById(R.id.lsvMarkers);

        Tools.setTitleColor(this);
        checkRegistration();

        GroupsActivity.GetGroups(Base, false);
        ShowMessage();
        StartServices();
        llTop = (LinearLayout) findViewById(R.id.llTop);
        llDown = (LinearLayout) findViewById(R.id.llDown);
        llMain = (LinearLayout) findViewById(R.id.llMain);
        llmapLayout = (LinearLayout) findViewById(R.id.mapLayout);
        LinearLayout.LayoutParams llD = (LinearLayout.LayoutParams) llDown.getLayoutParams();
        llD.height = Tools.GetDesktopSize(Base).y - ((LinearLayout.LayoutParams) llTop.getLayoutParams()).height;
        llDown.setLayoutParams(llD);

        lpTop = (LinearLayout.LayoutParams) llTop.getLayoutParams();
        lpDown = (LinearLayout.LayoutParams) llDown.getLayoutParams();

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
                                              lpTop = (LinearLayout.LayoutParams) llTop.getLayoutParams();
                                              lpDown = (LinearLayout.LayoutParams) llDown.getLayoutParams();

                                              lpTop.topMargin = 0;
                                              lpDown.topMargin = 0;
                                              llMain.setAlpha(1);
                                              llTop.setLayoutParams(lpTop);
                                              llDown.setLayoutParams(lpDown);
                                              llMain.setVisibility(View.VISIBLE);
                                              if (Tools.locationMarker != null)
                                                  Tools.locationMarker.remove();
                                              Tools.locationMarker = null;
                                          }

                                          ;
                                      }
        );

        llTop.setOnTouchListener(this);
        llMain.setOnTouchListener(this);

        ivArrowDown = (ImageView) findViewById(R.id.ivArrowDown);
        ivArrowDown.setOnClickListener(new View.OnClickListener() {
                                           public void onClick(View v) {
                                               lpTop = (LinearLayout.LayoutParams) llTop.getLayoutParams();
                                               lpDown = (LinearLayout.LayoutParams) llDown.getLayoutParams();

                                               lpTop.topMargin = -lpTop.height;
                                               lpDown.topMargin = Tools.GetDesktopSize(Base).y + lpTop.height / 2;
                                               llMain.setVisibility(View.INVISIBLE);
                                           }

                                           ;
                                       }
        );

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
        fragmentTransaction.add(R.id.llMapLoad, mMapFragment);
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
initializeDrawer();

//        IntentFilter filter = new IntentFilter("ir.tstracker.activity.proximity");
//        registerReceiver(new ProximityIntentReceiver(), filter);
initializeInviteButton();

    }

    private void initializeDrawer(){

        /**********************************************************Set DrawLayout*/
        lsvtest = (ListView) findViewById(R.id.lsvtest);
        MenuItemsAdapter adapter=new MenuItemsAdapter(this);
        Objects.MenuItem menuTitle= new Objects().new MenuItem();
        menuTitle.id=-1;
        menuTitle.text=getResources().getString(R.string.appShortDescribe);
        menuTitle.image=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        adapter.AddItem(menuTitle);
        Objects.MenuItem m0= new Objects().new MenuItem();
        m0.id=0;
        m0.text=EditProfileActivity.getName(this);
        m0.image=ProfileActivity.getProfileImage(96,this);
        adapter.AddItem(m0);
        Objects.MenuItem m1= new Objects().new MenuItem();
        m1.id=1;
        m1.text=getResources().getString(R.string.map);
        m1.image=BitmapFactory.decodeResource(getResources(), R.drawable.map);
        adapter.AddItem(m1);
        Objects.MenuItem m2= new Objects().new MenuItem();
        m2.id=2;
        m2.text=getResources().getString(R.string.groupsToChat);
        m2.image= BitmapFactory.decodeResource(getResources(), R.drawable.groups);
        adapter.AddItem(m2);
        Objects.MenuItem m3= new Objects().new MenuItem();
        m3.id=3;
        m3.text=getResources().getString(R.string.placesAndAreas);
        m3.image=BitmapFactory.decodeResource(getResources(), R.drawable.places);
        adapter.AddItem(m3);
        Objects.MenuItem m4= new Objects().new MenuItem();
        m4.id=4;
        m4.text=getResources().getString(R.string.chargeMyAccount);
        m4.image=BitmapFactory.decodeResource(getResources(),R.drawable.money);
        adapter.AddItem(m4);
        Objects.MenuItem m5= new Objects().new MenuItem();
        m5.id=5;
        m5.text=getResources().getString(R.string.about);
        m5.image=BitmapFactory.decodeResource(getResources(),R.drawable.about);
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

            boolean isclose=true;
            public void onDrawerClosed(View view) {
                isclose=false;
                //getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                isclose=true;
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
                        onTouch(lsvtest, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 360, 520, 1));
                        onTouch(lsvtest, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, 548, 906, 1));
                        onTouch(lsvtest, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 548, 906, 1));
                        break;
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
                        myIntent.putExtra("msg","Charge Account");
                        Base.startActivity(myIntent);
                        break;
                    case 6:
                        myIntent = new Intent(Base, about.class);
                        Base.startActivity(myIntent);
                        break;
                }

            }
        });
        ((ImageView)findViewById(R.id.imgSwipetoright)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

    }

    private  void initializeInviteButton(){
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
                w.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 0, params, "GenerateJoinKey");
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
        }
        else if(ObjectCode==5)//purchasedetails
        {
            if(Float.valueOf(Data.split(",")[0])>0) {
                String msg=
                        MainActivity.Base.getResources().getString(R.string.creditAmount)+" " + Data.split(",")[2] + "\n" +
                                Data.split(",")[1]+" " + MainActivity.Base.getResources().getString(R.string.groupCOunts) + "\n" ;
                     msg+=           (Data.split(",")[0].equals("Infinity"))? "":" " + MainActivity.Base.getResources().getString(R.string.DaysToRecharge);
                MessageEvent.InsertMessage(MainActivity.Base,msg, MessageEvent.CREADIT_EVENT);
                Tools.HasCredit = true;
            }
            else
            {
                MessageEvent.InsertMessage(  MainActivity.Base,MainActivity.Base.getResources().getString(R.string.NoCredit),MessageEvent.CREADIT_EVENT);
                Tools.HasCredit=false;
            }
        }
        else if (ObjectCode == 4) {
            if (Data.length() > 1) {
                try {
                    //add new area to database
                    ContentValues Val = new ContentValues();
                    DatabaseHelper dbh = new DatabaseHelper(MainActivity.Base);
                    SQLiteDatabase db=dbh.getReadableDatabase();

                    db= dbh.getWritableDatabase();

                    String[] geos = Data.split("\\|");
                    //                               Name~Points~radius~clientAreaCode~0 Or 1|
                    for (String g : geos) {
                        Val = new ContentValues();
                        Val.clear();
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_name, g.split("~")[0]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_center, g.split("~")[1]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_radius, g.split("~")[2]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_ID, g.split("~")[3]);
                        Val.put(DatabaseContracts.Geogences.COLUMN_NAME_isOwner,Integer.valueOf( g.split("~")[4]));
                        if(db.insert(DatabaseContracts.Geogences.TABLE_NAME, DatabaseContracts.Geogences.COLUMN_NAME_ID, Val)>0)
                        {

                        }
                 }
                    Tools.setupGeofences(Base);
                    db.close();
                    dbh.close();
                    db = null;
                    dbh = null;
                }
                catch (Exception er){
                    String e="";
                }
            }
        }
    }

    private void checkRegistration() {

        //check if this device registered already
        params = new HashMap<>();
        // the POST parameters:
        params.put("pData", Tools.GetImei(this) + "/");// "351520060796671");
        WebServices WS = new WebServices(getApplicationContext());
        WS.addQueue("ir.tsip.tracker.zarrintracker.MainActivity", 1, params, "CheckRegistration");
        ProfileActivity.GetImageFromServer(this);
        ProfileActivity.GetProfileFromServer(this);
        getGeofencesFromServer();
     WS=null;
    }

    public void getGeofencesFromServer(){

        HashMap<String, String> params;
        params = new HashMap<>();
        params.put("imei", Tools.GetImei(getApplicationContext()));
        params.put("center", "0");
        params.put("radius","0");
        params.put("name","0");
        params.put("clientCode", "0");
        params.put("operation", "4");
        WebServices W = new WebServices(getApplicationContext());
        W.addQueue("ir.tsip.tracker.zarrintracker.MainActivity", 4, params, "GeofenceOperations");
        W = null;
    }

    public static void insertDevice(String Data) throws JSONException {
        if(Data == "null")
            return;
        JSONObject jo = new JSONObject(Data);
        String key = jo.getString("key");
        String logo = jo.getString("logo");
        String site = jo.getString("site");
        String tell = jo.getString("tell");

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

        lpTop  = (LinearLayout.LayoutParams) llTop.getLayoutParams();
        lpDown  = (LinearLayout.LayoutParams) llDown.getLayoutParams();

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
    int counter=0;
Boolean isfirst=true;
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
                            if((counter+1) % 10==0) {
                                if (mMapFragment != null)
                                    Tools.setUpMap(mMapFragment.getMap(), getApplicationContext(), isfirst);
                                if (isfirst)
                                    isfirst = false;

                            }
                            //every 30 minutes
                            if(counter==30*60){
                                //get geofences
                                getGeofencesFromServer();
                            }
                            //every hour
                            if(counter==0)
                            {
                                //Update groups
                                WebServices ws = new WebServices(MainActivity.this);
                                ws.addQueue("ir.tsip.tracker.zarrintracker.MainActivity", 5, Tools.GetImei(MainActivity.this), "PurhaseDetails");
                                ws = null;
                                //Update persons images
                                Persons.UpdateImages();
                            }
                            //Counter repeat every one hour
                            if(counter>=60*60*60){
                                counter=0;
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
                            MEvent.ShowMessage(SV, MEvent.FirstDate, MEvent.Lastdate);
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
        Toast.makeText(this,getResources().getString(R.string.clickBackAgain), Toast.LENGTH_SHORT).show();

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
        super.onStop();
        _TimerMain.cancel();
        _TimerMain = null;
    }

    private void PauseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pause GPS");

        final RadioGroup RG = new RadioGroup(this);

        final RadioButton R1 = new RadioButton(this);
        final RadioButton R3 = new RadioButton(this);
        final RadioButton R6 = new RadioButton(this);
        final RadioButton R12 = new RadioButton(this);

        R1.setText("1 "+getResources().getString(R.string.hour));
        R1.setTag(1);
        R3.setText("3 "+getResources().getString(R.string.hour));
        R3.setTag(3);
        R6.setText("6 "+getResources().getString(R.string.hour));
        R6.setTag(6);
        R12.setText("12 "+getResources().getString(R.string.hour));
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
                    (new EventManager(MainActivity.Base)).AddEvevnt(" Paused tracking", "-4", MessageEvent.Pause_Event);
                } catch (Exception ex) {

                }

            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LocationListener.StartPause(0);
                dialog.cancel();
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
