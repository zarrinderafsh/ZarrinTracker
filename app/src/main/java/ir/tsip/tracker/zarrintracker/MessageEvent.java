package ir.tsip.tracker.zarrintracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.sql.Blob;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 11/16/2015.
 */
public class MessageEvent {
    Context _Context;
    public  MessageEvent(Context pContext)
    {
        _Context = pContext;
    }
    public static void AddMessage(Context context, String Message)
    {
        DatabaseHelper dbh = new DatabaseHelper(context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ContentValues V = new ContentValues();
        V.put(DatabaseContracts.Events.COLUMN_NAME_Data,Message);
        V.put(DatabaseContracts.Events.COLUMN_NAME_Date,new Date().toString());
        db.insert(DatabaseContracts.Events.TABLE_NAME,DatabaseContracts.Events.COLUMN_NAME_ID,V);
        db.close();
        dbh.close();
    }

    public int ShowMessage(LinearLayout scroll , int LastGetId)
    {
        if(LastGetId == 0)
        {
            //LastGetId = Integer.MAX_VALUE;
        }
        DatabaseHelper dbh = new DatabaseHelper(_Context);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Cursor c;
        c = db.query(DatabaseContracts.Events.TABLE_NAME, null, " Id > "+LastGetId, null, "", "", " 1 ");
        if(c.moveToFirst())
        {
            String Data = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Data));

            String DateTime= c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Date));
            Date date;
            DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = iso8601Format.parse(DateTime);
            } catch (ParseException e) {
            }

            byte[] Image= c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_Image));

            int id= c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Events.COLUMN_NAME_ID));

            LayoutInflater inflater = (LayoutInflater)_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.event_message, null);
            scroll.addView(view,0);

            ((TextView) view.findViewById(R.id.tvDateEvent)).setText(DateTime);
            ((TextView) view.findViewById(R.id.tvMessageEvent)).setText(Data);

            c.close();
            db.close();
            dbh.close();

            return id;
        }
        c.close();
        db.close();
        dbh.close();
        return LastGetId;
    }
}
