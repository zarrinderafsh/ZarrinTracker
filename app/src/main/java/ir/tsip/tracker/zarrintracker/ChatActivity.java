package ir.tsip.tracker.zarrintracker;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    Button btnSend;
    EditText txtMessage;
    static Handler scrollHandler = new Handler();
    static Context context;
    static String gpID;
    TextView txtGpName;
    ImageView imgGroupPhoto;
    static LinearLayout lsvChat;
    static ChatActivity _this;
    static TextView txtGeneratedJoinCode;
    static AlertDialog av;
    static ImageView imgLoading;
    ImageView inInvite;
    static ScrollView svChatView;
    static Boolean IsChatActivityShowing = false;
    static Boolean AnswerLastGetMessage = true;
    boolean _isOwner = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                 super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_chat);

        IsChatActivityShowing = true;
        _this = null;
        _this = this;
        svChatView = (ScrollView) _this.findViewById(R.id.svChatView);
        ImageView imgLeave = (ImageView) findViewById(R.id.imgLeaveGroup);
        context = getApplicationContext();
        inInvite = (ImageView) findViewById(R.id.ivInvite);
        _isOwner = getIntent().getBooleanExtra("myGroup", false);
        if (_isOwner) {

            inInvite.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Invite Others");
                    LayoutInflater inflate = ChatActivity.this.getLayoutInflater();
                    View view = inflate.inflate(R.layout.activity_invate, null);
                    txtGeneratedJoinCode = (TextView) view.findViewById(R.id.txtGeneratedJOinCode);
                    imgLoading = (ImageView) view.findViewById(R.id.imgLoading);

                    WebServices w = new WebServices(ChatActivity.this);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("imei", Tools.GetImei(getApplicationContext()));
                    w.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", 0, params, "GenerateJoinKey",1);
                    w = null;
                    builder.setView(view);
                    builder.setPositiveButton(getResources().getString(R.string.send), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Tools.ShareText(EditProfileActivity.getName(_this) + " " + _this.getResources().getString(R.string.InvationMessage) + txtGeneratedJoinCode.getText().toString(), _this);

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

                    av = builder.create();
//                    av.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    av.show();
                }
            });
            inInvite.setVisibility(View.VISIBLE);
            imgLeave.setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txtInvitetoGroup)).setVisibility(View.VISIBLE);
        } else {
            imgLeave.setVisibility(View.VISIBLE);
            imgLeave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                    builder.setTitle("Leave group");
                    builder.setMessage(R.string.LeaveGroupMessage);
                    builder.setPositiveButton(getApplicationContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                HashMap<String, String> params;
                                params = new HashMap<>();
                                params.put("imei", Tools.GetImei(getApplicationContext()));
                                params.put("gpID", gpID);
                                WebServices W = new WebServices(getApplicationContext());
                                W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", -10, params, "LeaveGroup");
                                W = null;
                            } catch (Exception ex) {

                            }
                        }
                    });
                    builder.setNegativeButton(getApplicationContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });

                    AlertDialog av = builder.create();
                    av.show();
                }
            });
            inInvite.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.txtInvitetoGroup)).setVisibility(View.INVISIBLE);
        }

        txtGpName = (TextView) findViewById(R.id.txtGroupName);
        imgGroupPhoto = (ImageView) findViewById(R.id.imgGroupPhoto);

        gpID = getIntent().getStringExtra("gpID");
        setGroupInfo();


        txtMessage = (EditText) findViewById(R.id.txtSendMessage);
        lsvChat = (LinearLayout) findViewById(R.id.lsvChats);

        btnSend = (Button) findViewById(R.id.btnSendMessage);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.this.sendMessage(txtMessage.getText().toString());
            }
        });

        ShowMessages();


        //To clear focus and prevent to show virtual keyboard at first.
        txtMessage.clearFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    ListView lsvMembers;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    private void initializeDrawer(String[] members) {
        lsvMembers = (ListView) findViewById(R.id.lsvPersons);
        final MenuItemsAdapter adapter = new MenuItemsAdapter(this);
        Objects.MenuItem m;
        int indexer = -1;
        m = new Objects().new MenuItem();
        m.id = indexer;
        m.image = ((BitmapDrawable) imgGroupPhoto.getDrawable()).getBitmap();
        m.text = txtGpName.getText().toString();
        adapter.AddItem(m);
        indexer++;
        for (String s : members) {
            if (s.equals("+"))
                continue;
            if (!s.contains("-"))
                continue;
            m = new Objects().new MenuItem();
            m.id = indexer;
            if(s.replace("+", "").split("-").length >= 2) {
                m.text = s.replace("+", "").split("-")[1];
                m.customTag = s.split("-")[0];
                adapter.AddItem(m);
            }
            indexer++;
        }
        lsvMembers.setAdapter(adapter);

        lsvMembers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            final CharSequence[] items = {_this.getResources().getString(R.string.delete),
                    _this.getResources().getString(R.string.cancel)};

            @Override
            public void onItemClick(AdapterView<?> parent, final View v, final int position, long id) {
                if (!_isOwner)
                    return;
                final AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setTitle("");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals(_this.getResources().getString(R.string.delete))) {


                            Persons p = new Persons();
                            p.GetData(Integer.valueOf(v.getTag(R.string.DontTranslate2).toString()));
                            AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                            builder.setTitle("");
                            builder.setMessage(_this.getResources().getString(R.string.KickUser));
                            builder.setPositiveButton(_this.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    WebServices w = new WebServices(_this);
                                    w.addQueue("", 0, v.getTag(R.string.DontTranslate2).toString() + "," + gpID, "KickUser");
                                    w = null;
                                    GroupsActivity.GetGroups(_this, false);
                                    Toast.makeText(ChatActivity.this, _this.getResources().getString(R.string.wait), Toast.LENGTH_LONG).show();
                                    adapter.RemoveItem(position);
                                    dialog.cancel();
                                }
                            });
                            builder.setNegativeButton(_this.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        } else if (items[item].equals(_this.getResources().getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        mDrawerLayout = (android.support.v4.widget.DrawerLayout) findViewById(R.id.mDrawerLayout);
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

        ((ImageView) findViewById(R.id.imgSwipetoright)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });

    }
    /********************************************************************DrawerLayout Methods*/
    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(lsvMembers);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override

    public void setTitle(CharSequence title) {
//        mTitle = title;
//        getSupportActionBar().setTitle(mTitle);
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

    private void sendMessage(String msg) {
        if (msg == null || msg == "" || msg == " " || msg.length() < 1)
            return;
        Persons p = new Persons();
        if (!p.FindDeviceOwner()) {
            p.ID = -1;
        }
        //[C||E||G](date) [from] : message
        String message = p.ID + "~~~~~ME~~[C]!" + gpID + "!(" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", new Locale("en")).format(new Date()) + ") [" + EditProfileActivity.getName(_this) + "] : " + msg;
        InsertMessages(new String[]{message});
        HashMap<String, String> params;
        params = new HashMap<>();
        params.put("message", msg);
        params.put("imei", Tools.GetImei(getApplicationContext()));
        params.put("gpID", gpID);
        params.put("msgtype", "1");
        WebServices W = new WebServices(getApplicationContext());
        W.addQueue("ir.tsip.tracker.zarrintracker.ChatActivity", -1, params, "SetMessage");
        // InsertMessages(txtMessage.getText().toString().split(String.valueOf((char) 26)));
        txtMessage.setText("");
        W = null;
    }

    public static void backWebServicesError(int ObjectCode, String ClassName) {
        AnswerLastGetMessage = true;
    }

    public static void backWebServices(int ObjectCode, String Data) {

        if (ObjectCode == 1) {
            AnswerLastGetMessage = true;
            if (_this == null || Data == "null")
                return;
            //if(Tools.HasCredit)
            _this.InsertMessages(Data.split(","));
        } else if (ObjectCode == 0) {
            if (Data != ("0")) {
                if (Data.startsWith("-1")) {
                    Intent myIntent = new Intent(_this, PurchaseActivity.class);
                    myIntent.putExtra("msg", "You can not Invite more than " + Data.split(",")[1] + " people.");
                    _this.startActivity(myIntent);
                    _this.finish();
                } else {

                    imgLoading.setVisibility(View.INVISIBLE);
                    txtGeneratedJoinCode.setText(Data);
                    //   av.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                }

            } else if (Data.split(",")[0] == "-1") {
                Intent myIntent = new Intent(context, PurchaseActivity.class);
                myIntent.putExtra("msg", "You can not invite more than " + Data.split(",")[1] + " persons.");
                context.startActivity(myIntent);
            }
        } else if (ObjectCode == -10) {
            if (Data != null) {
                DatabaseHelper dh = new DatabaseHelper(_this);
                SQLiteDatabase db = dh.getReadableDatabase();
                //Delete item from database
                if (db.delete(DatabaseContracts.Groups.TABLE_NAME, DatabaseContracts.Groups.COLUMN_NAME_ID + " in (" + Data + ")", null) > 0) {
                }
                //Delete all the messages of the group
                if (db.delete(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_Group + " in (" + Data + ")", null) > 0) {
                }
                db.close();
                dh.close();
                dh = null;
                //Send left message to other
                _this.sendMessage("Goodbye friends.");
                //close the activity
                _this.setResult(1);//1 means group activity will be finished.
                _this.finish();
            } else if (ObjectCode == -1 && Data.equals("-1"))
                Toast.makeText(_this, _this.getResources().getString(R.string.groupNoCharge), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(_this, _this.getResources().getString(R.string.somethingWrong), Toast.LENGTH_SHORT).show();
        }
    }


    private void setGroupInfo() {

        DatabaseHelper dbh1 = null;
        SQLiteDatabase readabledb1 = null;
        if (dbh1 == null)
            dbh1 = new DatabaseHelper(getApplicationContext());
        if (readabledb1 == null)
            readabledb1 = dbh1.getReadableDatabase();

        String[] columns = {DatabaseContracts.Groups.COLUMN_NAME_ID, DatabaseContracts.Groups.COLUMN_NAME_Image, DatabaseContracts.Groups.COLUMN_NAME_Name, DatabaseContracts.Groups.COLUMN_NAME_Members};
        Cursor c = readabledb1.query(DatabaseContracts.Groups.TABLE_NAME, columns, DatabaseContracts.Groups.COLUMN_NAME_ID + "=?", new String[]{String.valueOf(gpID)}, "", "", "");
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (true) {
                txtGpName.setText(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Name)).replace(";;;", ""));
                try {
                    imgGroupPhoto.setImageBitmap(Tools.LoadImage(c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Image)), 96));
                } catch (Exception er) {
                    Toast.makeText(ChatActivity.this, getResources().getString(R.string.imageDidntLoad), Toast.LENGTH_SHORT).show();
                }
                initializeDrawer(c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Groups.COLUMN_NAME_Members)).split(";"));
                if (c.isLast())
                    break;
                c.moveToNext();
            }
        }
        c.close();
        dbh1.close();
        readabledb1.close();
    }

    DatabaseHelper dbh;
    SQLiteDatabase db;

    private void InsertMessages(String[] messages) {
        if (messages == null)
            return;
        if (dbh == null)
            dbh = new DatabaseHelper(context);
        if (db == null)
            db = dbh.getWritableDatabase();
        String gpCode;
        ContentValues Data;
        Persons p;
        for (String msg : messages) {
            p = new Persons();
            p.ID = Integer.valueOf(msg.split("~~~")[0].replace("[\"", ""));
            msg = msg.split("~~~")[1];
            if (!p.GetData(p.ID) && p.ID != -1) {
                p.name = msg.split("\\[")[2].split(":")[0].replace("]", "");
                p.isme = false;
                p.Save();
            } else {
                p.name = msg.split("\\[")[2].split(":")[0].replace("]", "");
                p.isme = false;
                p.update();
            }
            if (p.image == null) {
                p.GetImageFromServer();
            }
            Data = new ContentValues();

            gpCode = msg.split("!")[1];
            msg = msg.replace("!" + String.valueOf(gpCode) + "!", "");
            if (msg.contains("[C]")) {
                Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Data, msg.replace("[C]", ""));
                Data.put(DatabaseContracts.ChatLog.COLUMN_NAME_Group, gpCode);
                Data.put(DatabaseContracts.ChatLog.COLUMN_Person_Id, p.ID);
                long id = db.insert(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_ID, Data);

                if (gpID == null || (!gpID.equals(gpCode) && IsChatActivityShowing) || !IsChatActivityShowing)
                    MessageEvent.InsertMessage(context, context.getResources().getString(R.string.NewMessage) + " " + ((p.name == null || p.name == "") ? getResources().getString(R.string.someone) : p.name), p.image, MessageEvent.NEW_MESSAGE_EVENT);
                else if (gpID.equals(gpCode))
                    CreateGroupLayer(new Date(), msg, p.image, 0, (int) id, p.ID);
                msg = msg.split("\\[")[2].split(":")[1].replace("]", "");
                GroupsActivity.UpdateGroup(Integer.valueOf(gpCode), null, new Date().toString(), msg, null, null);
            } else if (msg.contains("[E-area]")) {
                MessageEvent.InsertMessage(context, p.name + ": " + msg.replace("[E-area]", "").split(":")[3].replace("\"", "").replace("]", ""), p.image, MessageEvent.AREA_EVENT);
            } else if (msg.contains("[E-sos]")) {
                MessageEvent.InsertMessage(context, p.name + ": " + msg.replace("[E-sos]", "").split(":")[3].replace("\"", "").replace("]", ""), p.image, MessageEvent.SOS_EVENT);
            } else if (msg.contains("[E-pause]")) {
                MessageEvent.InsertMessage(context, p.name + ": " + msg.replace("[E-pause]", "").split(":")[3].replace("\"", "").replace("]", ""), p.image, MessageEvent.Pause_Event);
            } else if (msg.contains("[E-gps]")) {
                MessageEvent.InsertMessage(context, p.name + ": " + msg.replace("[E-gps]", "").split(":")[3].replace("\"", "").replace("]", ""), p.image, MessageEvent.GPS_EVENT);
            } else if (msg.contains("[E]")) {
                MessageEvent.InsertMessage(context, p.name + ": " + msg.replace("[E]", "").split(":")[3].replace("\"", "").replace("]", ""), p.image, MessageEvent.NEW_MESSAGE_EVENT);
            } else if (msg.contains("[G]")) {
            }
            else if(msg.contains("[Q]")){
                DatabaseHelper dbh=new DatabaseHelper(_this);
                SQLiteDatabase db=dbh.getWritableDatabase();
                db.execSQL(msg.replace("[Q]",""));
                db.close();
                dbh.close();
                db=null;
                dbh=null;
            }
            Data = null;
        }
        db.close();
dbh.close();
        db=null;
        dbh=null;
    }

    SQLiteDatabase readabledb;

    private void ShowMessages() {

        //if (msgs.isEmpty())
        {
            if (dbh == null)
                dbh = new DatabaseHelper(getApplicationContext());
            if (readabledb == null)
                readabledb = dbh.getReadableDatabase();

            String[] columns = {DatabaseContracts.ChatLog.COLUMN_NAME_ID, DatabaseContracts.ChatLog.COLUMN_NAME_Group, DatabaseContracts.ChatLog.COLUMN_NAME_Data, DatabaseContracts.ChatLog.COLUMN_Person_Id};
            Cursor c = readabledb.query(DatabaseContracts.ChatLog.TABLE_NAME, columns, DatabaseContracts.ChatLog.COLUMN_NAME_Group + "=?", new String[]{String.valueOf(gpID)}, "", "", DatabaseContracts.ChatLog.COLUMN_NAME_ID);
            if (c.getCount() > 0) {
                c.moveToFirst();
                String data;
                DateFormat iso8601Format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

                while (true) {
                    Persons p = new Persons();
                    data = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.ChatLog.COLUMN_NAME_Data));
                    p.GetData(c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.ChatLog.COLUMN_Person_Id)));
                    try {
                        CreateGroupLayer(iso8601Format.parse(data.substring(data.indexOf("("), data.indexOf(")")).replace("(", "").replace("\\/", "-")),
                                data,
                                p.image,
                                0,
                                c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.ChatLog.COLUMN_NAME_ID)),
                                p.ID);
                    } catch (Exception er) {
                        String e = er.getMessage();
                    }
                    if (c.isLast())
                        break;
                    c.moveToNext();
                }
            }
            c.close();
            ScrollListTOEnd();

        }
    }

    private static void CreateGroupLayer(Date date, String Message, Bitmap img, int State, int ID, int Pcode) {
        if (context == null)
            return;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_list, null);
        boolean isowner = false;
        if (Message.contains("~~ME~~")) {
            view = inflater.inflate(R.layout.chat_list_owner, null);
            isowner = true;
        }
        view.setTag(R.string.DontTranslate1, String.valueOf(ID));
        view.setTag(R.string.DontTranslate2, Pcode);
        view.setId(new Random().nextInt());
        lsvChat.addView(view);
        TextView tvLastChatMessage = (TextView) view.findViewById(R.id.tvLastChatMessage);
        TextView txtUsername = (TextView) view.findViewById(R.id.txtUsername);
        TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
        ImageView imgPic = (ImageView) view.findViewById(R.id.ivChatPic);

        String s = Message.substring(Message.indexOf(" ["));
        txtUsername.setText(s.substring(2, s.indexOf("]")));
        txtDate.setText(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date));

        String[] msgs = Message.replace("[", "").replace("]", "").split(" : ");
        Message = msgs[msgs.length - 1].replace("\"", "");
        tvLastChatMessage.setText(Message);

        ImageView ivChatPic = (ImageView) view.findViewById(R.id.ivChatPic);
        if (img != null) {
            ivChatPic.setImageBitmap(img);
            imgPic.setImageBitmap(img);
        } else if (isowner) {
            ivChatPic.setImageBitmap(ProfileActivity.getProfileImage(96, _this));
        }
        view.setOnLongClickListener(new View.OnLongClickListener() {
            final CharSequence[] items = {
                    _this.getResources().getString(R.string.details),
                    _this.getResources().getString(R.string.delete),
                    _this.getResources().getString(R.string.copy),
                    _this.getResources().getString(R.string.cancel)};

            @Override
            public boolean onLongClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                builder.setTitle("");

                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals(_this.getResources().getString(R.string.delete))) {
                            lsvChat.removeView(v);
                            _this.deleteMessage(Integer.valueOf(v.getTag(R.string.DontTranslate1).toString()));
                        } else if (items[item].equals(_this.getResources().getString(R.string.copy))) {
                            ClipboardManager clipboard = (ClipboardManager) _this.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Message", ((TextView) v.findViewById(R.id.tvLastChatMessage)).getText());
                            clipboard.setPrimaryClip(clip);

                        } else if (items[item].equals(_this.getResources().getString(R.string.cancel))) {
                            dialog.dismiss();
                        } else if (items[item].equals(_this.getResources().getString(R.string.details))) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(_this);
                            builder.setTitle(_this.getResources().getString(R.string.userProfile));
                            LayoutInflater inflate = _this.getLayoutInflater();
                            View view = inflate.inflate(R.layout.person_details, null);
                            Persons p = new Persons();
                            p.GetData(Integer.valueOf(v.getTag(R.string.DontTranslate2).toString()));
                            ((ImageView) view.findViewById(R.id.imgUserPhoto)).setImageBitmap(p.image);
                            ((TextView) view.findViewById(R.id.txtName)).setText(p.name);
                            builder.setView(view);
                            builder.setNegativeButton(_this.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                }
                            });

                            builder.show();
                        }
                    }
                });
                AlertDialog ad = builder.create();
                ad.show();
                return false;
            }
        });
        ScrollListTOEnd();
    }

    private static void ScrollListTOEnd() {

        if (svChatView != null)
            scrollHandler.post(new Runnable() {
                @Override
                public void run() {
                    svChatView.smoothScrollTo(0, lsvChat.getBottom());
                }
            });
    }

    private void deleteMessage(int id) {

        DatabaseHelper dh = new DatabaseHelper(_this);
        SQLiteDatabase db = dh.getReadableDatabase();
        //Delete item from database
        if (db.delete(DatabaseContracts.ChatLog.TABLE_NAME, DatabaseContracts.ChatLog.COLUMN_NAME_ID + " in (" + id + ")", null) > 0) {
        }
        db.close();
        dh.close();
        dh = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IsChatActivityShowing = false;
    }
}