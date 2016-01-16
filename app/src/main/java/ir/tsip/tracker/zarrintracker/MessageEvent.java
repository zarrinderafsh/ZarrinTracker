package ir.tsip.tracker.zarrintracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Blob;
import java.sql.Struct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 11/16/2015.
 */
public class MessageEvent {
    class Loc
    {
        public float Lat;
        public float Lon;
    };


    public final static  String AREA_EVENT="AREA";
    public final static String NEW_MESSAGE_EVENT="MESSAGE";
    public final static   String CREADIT_EVENT="CREDIT";
    public final static String  SOS_EVENT="SOS";
    public final static String  GPS_EVENT="GPS";
    public final static String  Pause_Event="PAUSE";

    static Context _Context;
    public  MessageEvent(Context pContext)
    {
        _Context = pContext;
    }
    public static void InsertMessage(Context context, String Message,String eventType)
    {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Events.COLUMN_NAME_Data,Message);
        V.put(DatabaseContracts.Events.COLUMN_NAME_Date,new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
        V.put(DatabaseContracts.Events.COLUMN_NAME_Lat,LocationListener.CurrentLat);
        V.put(DatabaseContracts.Events.COLUMN_NAME_Lon,LocationListener.CurrentLon);
        V.put(DatabaseContracts.Events.COLUMN_type,eventType);


        Bitmap B = ProfileActivity.getProfileImage(96, _Context);
        if(B!=null) {
            byte[] b = Tools.getBytesFromBitmap(B);
            V.put(DatabaseContracts.Events.COLUMN_NAME_Image, b);
        }
        db.insert(DatabaseContracts.Events.TABLE_NAME,DatabaseContracts.Events.COLUMN_NAME_ID,V);
        db.close();
        dbh.close();
    }public static void InsertMessage(Context context, String Message,Bitmap image,String eventType)
    {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Events.COLUMN_NAME_Data,Message);
        V.put(DatabaseContracts.Events.COLUMN_NAME_Date,new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
        V.put(DatabaseContracts.Events.COLUMN_NAME_Lat,LocationListener.CurrentLat);
        V.put(DatabaseContracts.Events.COLUMN_NAME_Lon,LocationListener.CurrentLon);
        V.put(DatabaseContracts.Events.COLUMN_type,eventType);
        if(image!=null) {
            byte[] b = Tools.getBytesFromBitmap(image);
            V.put(DatabaseContracts.Events.COLUMN_NAME_Image, b);
        }
        db.insert(DatabaseContracts.Events.TABLE_NAME,DatabaseContracts.Events.COLUMN_NAME_ID,V);
        db.close();
        dbh.close();
    }

    public static void DeleteMessage(Context context, int id)
    {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        db.delete(DatabaseContracts.Events.TABLE_NAME,"id="+String.valueOf(id),null);
        db.close();
        dbh.close();
    }

    public Date FirstDate,Lastdate;
    public void ShowMessage(final LinearLayout scroll , Date pFirstDate, Date pLastDate,Boolean isFromService)
    {

        Date date;
        if(pFirstDate == null)
        {
            pFirstDate = new Date();
        }
        if(pLastDate == null)
        {
            pLastDate = new Date();
        }
        DatabaseHelper dbh = new DatabaseHelper(_Context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c;
        String Data="";
        String conditionOperator="OR";
        //if service wants newest notification
        if(isFromService)
            conditionOperator="AND";
        c = db.query(DatabaseContracts.Events.TABLE_NAME, null,
                DatabaseContracts.Events.COLUMN_NAME_Date + " > '"+ new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(pFirstDate)+"'  "+conditionOperator+" "+
                DatabaseContracts.Events.COLUMN_NAME_Date + " < '"+ new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(pLastDate)+"'"
                ,
                null, "","", DatabaseContracts.Events.COLUMN_NAME_Date+" DESC" , "");
        if(c.moveToFirst())
        {
            if(!Tools.Mute)
            Tools.PlayAlert(_Context);

            int id;
            do {
                Data = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Data));
//when from server we do not need to show event, just a simple notification;
                if(!isFromService) {
                    String eventType = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_type));


                    String DateTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Date));
                    DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        date = iso8601Format.parse(DateTime);
                    } catch (ParseException e) {
                        e.toString();
                        date = null;
                    }
                    byte[] Image = c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Image));

                    id = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_ID));

                    float Lat = c.getFloat(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Lat));
                    float Lon = c.getFloat(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Lon));

                    LayoutInflater inflater = (LayoutInflater) _Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view;
                    view = inflater.inflate(R.layout.event_message, null);

                    view.setId(100000 + id);
                    if (date.compareTo(pFirstDate) > 0) {
                        pFirstDate = date;
                        scroll.addView(view, 0);
                    }
                    if (date.compareTo(pLastDate) < 0) {
                        pLastDate = date;
                        scroll.addView(view);
                    }

                    if (date != null) {
                        String d = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
                        ((TextView) view.findViewById(R.id.tvDateEvent)).setText(d);
                    }
                    ((TextView) view.findViewById(R.id.tvMessageEvent)).setText(Data);

                    View.OnClickListener ClickDelete = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MessageEvent.DeleteMessage(_Context, (int) v.getTag());
                            DeleteLayout(scroll, 100000 + (int) v.getTag());
                        }
                    };

                    View.OnClickListener ClickLocation = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (v.getTag().getClass().equals(Loc.class)) {
                                Loc L = (Loc) v.getTag();
                                if (L.Lon > 0 && L.Lat > 0) {
                                    Tools.locationMarker = Tools.GoogleMapObj.addMarker(new MarkerOptions().position(new LatLng(L.Lat, L.Lon)));
                                    Tools.GoogleMapObj.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(L.Lat, L.Lon), 16.0f));

                                 MainActivity.Base.lytEventsAndProfileparams = (RelativeLayout.LayoutParams)  MainActivity.Base.lytEventsAndProfile.getLayoutParams();
                                    MainActivity.Base.    lytEventsAndProfileparams.setMargins(0,  MainActivity.Base.height -  MainActivity.Base.lytProfile.getHeight()- MainActivity.Base.lytHeaderTop.getHeight()-15, 0, 0);

                                    MainActivity.Base.lytEventsAndProfile.setLayoutParams( MainActivity.Base.lytEventsAndProfileparams);
                                }
                            } else if (v.getTag().getClass().equals(Integer.class)) {
                                Integer i = (Integer) v.getTag();
                                if (i == 0) {
                                    Intent myIntent = new Intent(MainActivity.Base, GroupsActivity.class);
                                    MainActivity.Base.startActivity(myIntent);
                                } else if (i == 1) {
                                    Intent myIntent = new Intent(MainActivity.Base, PurchaseActivity.class);
                                    myIntent.putExtra("msg", "Recharge your account.");
                                    MainActivity.Base.startActivity(myIntent);
                                }
                            }
                        }
                    };

                    Loc L;
                    ((TextView) view.findViewById(R.id.tvDeleteEvent)).setTag(id);
                    ((TextView) view.findViewById(R.id.tvDeleteEvent)).setOnClickListener(ClickDelete);
                    switch (eventType) {
                        case NEW_MESSAGE_EVENT:
                            ((TextView) view.findViewById(R.id.tvLocationEvent)).setTag(0);
                            ((TextView) view.findViewById(R.id.tvLocationEvent)).setText(view.getResources().getString(R.string.openChat));
                        //    view.setBackgroundColor(Color.parseColor("#8CC739"));
                            break;
                        case SOS_EVENT:
                            L = new Loc();
                            L.Lat = Lat;
                            L.Lon = Lon;
                            ((TextView) view.findViewById(R.id.tvLocationEvent)).setTag(L);
                          //  ((TextView) view.findViewById(R.id.tvMessageEvent)).setTextColor(Color.WHITE);
                          //  view.setBackgroundColor(Color.parseColor("#550000"));
                            break;
                        case AREA_EVENT:
                            L = new Loc();
                            L.Lat = Lat;
                            L.Lon = Lon;
                            ((TextView) view.findViewById(R.id.tvLocationEvent)).setTag(L);
                           // ((TextView) view.findViewById(R.id.tvMessageEvent)).setTextColor(Color.WHITE);
                          //  view.setBackgroundColor(Color.parseColor("#4D658D"));
                            break;
                        case CREADIT_EVENT:
                            ((TextView) view.findViewById(R.id.tvLocationEvent)).setTag(1);
                            ((TextView) view.findViewById(R.id.tvLocationEvent)).setText(view.getResources().getString(R.string.charge));
                       //     view.setBackgroundColor(Color.parseColor("#D9D372"));
                            break;
                        case Pause_Event:
                        case GPS_EVENT:
                            L = new Loc();
                            L.Lat = Lat;
                            L.Lon = Lon;
                            ((TextView) view.findViewById(R.id.tvLocationEvent)).setTag(L);
                         //   view.setBackgroundColor(Color.parseColor("#21BEDE"));
                            break;
                        default:
                            break;
                    }
                    ((TextView) view.findViewById(R.id.tvLocationEvent)).setOnClickListener(ClickLocation);

                    if (Image != null && Image.length > 10) {
                        Tools.LoadImage((ImageView) view.findViewById(R.id.ivImageEvent), Image, 96);
                    }
                }
            }
            while(c.moveToNext());
        }
        if(Data.length()>1)
        Tools.Notificationm(_Context, "TsTracker Events", Data,_Context.getPackageName(),1,R.drawable.ic_launcher);
        c.close();
        db.close();
        dbh.close();
        FirstDate = pFirstDate;
        Lastdate = pLastDate;
    }

    public void DeleteLayout(LinearLayout scroll, int id)
    {
        scroll.removeView(scroll.findViewById(id));
    }
}
