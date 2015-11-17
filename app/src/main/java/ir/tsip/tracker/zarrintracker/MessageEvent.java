package ir.tsip.tracker.zarrintracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

    static Context _Context;
    public  MessageEvent(Context pContext)
    {
        _Context = pContext;
    }
    public static void InsertMessage(Context context, String Message)
    {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Events.COLUMN_NAME_Data,Message);
        V.put(DatabaseContracts.Events.COLUMN_NAME_Date,new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()));
        V.put(DatabaseContracts.Events.COLUMN_NAME_Lat,LocationListener.CurrentLat);
        V.put(DatabaseContracts.Events.COLUMN_NAME_Lon,LocationListener.CurrentLon);

        Bitmap B = ProfileActivity.getProfileImage(96, _Context);
        if(B!=null) {
            byte[] b = Tools.getBytesFromBitmap(B);
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
    public void ShowMessage(final LinearLayout scroll , Date pFirstDate, Date pLastDate)
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
        c = db.query(DatabaseContracts.Events.TABLE_NAME, null,
                DatabaseContracts.Events.COLUMN_NAME_Date + " > '"+ new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(pFirstDate)+"' OR "+
                DatabaseContracts.Events.COLUMN_NAME_Date + " < '"+ new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(pLastDate)+"'"
                ,
                null, "","", DatabaseContracts.Events.COLUMN_NAME_Date+" DESC" , "");
        if(c.moveToFirst())
        {
            Tools.PlayAlert(_Context);

            int id;
            do {
                String Data = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Data));

                String DateTime = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Date));
                DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    date = iso8601Format.parse(DateTime);
                } catch (ParseException e) {
                    e.toString();
                    date=null;
                }
                byte[] Image = c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Image));

                id = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_ID));

                float Lat = c.getFloat(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Lat));
                float Lon = c.getFloat(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Lon));

                LayoutInflater inflater = (LayoutInflater) _Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.event_message, null);
                view.setId(100000 + id);
                if(date.compareTo(pFirstDate) > 0) {
                    pFirstDate = date;
                    scroll.addView(view,0);
                }
                if(date.compareTo(pLastDate) < 0 ) {
                    pLastDate = date;
                    scroll.addView(view);
                }

                if(date!=null) {
                    String d = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date);
                    ((TextView) view.findViewById(R.id.tvDateEvent)).setText(d);
                }
                ((TextView) view.findViewById(R.id.tvMessageEvent)).setText(Data);

                View.OnClickListener ClickDelete = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageEvent.DeleteMessage(_Context,(int)v.getTag());
                        DeleteLayout(scroll,100000 + (int)v.getTag());
                    }
                };

                View.OnClickListener ClickLocation = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Loc L = (Loc)v.getTag();
                        if(L.Lon > 0 && L.Lat >0)
                        {

                        }
                    }
                };

                ((TextView) view.findViewById(R.id.tvDeleteEvent)).setTag(id);
                ((TextView) view.findViewById(R.id.tvDeleteEvent)).setOnClickListener(ClickDelete);

                Loc L = new Loc();
                L.Lat = Lat;
                L.Lon = Lon;
                ((TextView) view.findViewById(R.id.tvLocationEvent)).setTag(L);
                ((TextView) view.findViewById(R.id.tvLocationEvent)).setOnClickListener(ClickLocation);

                if(Image != null && Image.length > 10)
                {
                    Tools.LoadImage((ImageView)view.findViewById(R.id.ivImageEvent),Image,96);
                }
            }
            while(c.moveToNext());
        }
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
