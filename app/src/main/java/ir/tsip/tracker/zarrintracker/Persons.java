package ir.tsip.tracker.zarrintracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by morteza on 2015-12-25.
 */
public class Persons {
    public int ID;
    public String name;
    public Bitmap image;
public boolean isme=false;
public static Context con;


    public Persons(){
        if(con==null){
            if(LocationListener.mContext==null)
                con=MainActivity.Base;
            else
                con=LocationListener.mContext;
        }
    }

    public boolean Save() {
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            ContentValues V = new ContentValues();
            V.put(DatabaseContracts.Persons.COLUMN_NAME_ID, ID);
            V.put(DatabaseContracts.Persons.COLUMN_NAME_name, name);

            if (image != null) {
                byte[] b = Tools.getBytesFromBitmap(image);
                V.put(DatabaseContracts.Persons.COLUMN_NAME_image, b);
            }
            V.put(DatabaseContracts.Persons.COLUMN_is_me, (isme) ? 1 : 0);

            db.insert(DatabaseContracts.Persons.TABLE_NAME, null, V);
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
            dbh.close();
        }
        return true;
    }

    public boolean update() {
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            ContentValues V = new ContentValues();
            V.put(DatabaseContracts.Persons.COLUMN_NAME_name, name);

            if (image != null) {
                byte[] b = Tools.getBytesFromBitmap(image);
                V.put(DatabaseContracts.Persons.COLUMN_NAME_image, b);
            }
            V.put(DatabaseContracts.Persons.COLUMN_is_me, (isme) ? 1 : 0);
            db.update(DatabaseContracts.Persons.TABLE_NAME, V, DatabaseContracts.Persons.COLUMN_NAME_ID + "=" + ID, null);
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
            dbh.close();
        }
        return true;
    }

    public boolean Delete() {
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            db.delete(DatabaseContracts.Persons.TABLE_NAME, DatabaseContracts.Persons.COLUMN_NAME_ID + "=" + ID, null);
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
            dbh.close();
        }
        return true;
    }

    public boolean Find(int pID) {
        return Find(pID, false);
    }

    public boolean Find(int pID, boolean pSetData) {
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db=null;
        try {
            db = dbh.getReadableDatabase();
            ContentValues V = new ContentValues();
            V.put(DatabaseContracts.Persons.COLUMN_NAME_name, name);
            if (image != null) {
                byte[] b = Tools.getBytesFromBitmap(image);
                V.put(DatabaseContracts.Persons.COLUMN_NAME_image, b);
            }

            V.put(DatabaseContracts.Persons.COLUMN_is_me, (isme)?1:0);
            Cursor c = db.query(DatabaseContracts.Persons.TABLE_NAME,
                    null,
                    DatabaseContracts.Persons.COLUMN_NAME_ID + "=" + pID,
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {
                if (pSetData) {
                    ID = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_ID));
                    name = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_name));
                    image = Tools.getBitmapFromByte(c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_image)));
               isme=c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_is_me))>0?true:false;

                }
                return true;
            }
        } catch (Exception e) {
            Log.e("position",e.getMessage());
            return false;
        } finally {
            db.close();
            dbh.close();
        }
        return false;
    }

    public boolean GetData(int pID) {
        return Find(pID, true);
    }
    public boolean FindDeviceOwner() {
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getReadableDatabase();
        try {
            ContentValues V = new ContentValues();
            V.put(DatabaseContracts.Persons.COLUMN_NAME_name, name);
            if (image != null) {
                byte[] b = Tools.getBytesFromBitmap(image);
                V.put(DatabaseContracts.Persons.COLUMN_NAME_image, b);
            }

            V.put(DatabaseContracts.Persons.COLUMN_is_me, (isme)?1:0);
            Cursor c = db.query(DatabaseContracts.Persons.TABLE_NAME,
                    null,
                    DatabaseContracts.Persons.COLUMN_is_me + "=1",
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {
                    ID = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_ID));
                    name = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_name));
                    image = Tools.getBitmapFromByte(c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_image)));
                    isme=c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_is_me))>0?true:false;

                return true;
            }
        } catch (Exception e) {
            return false;
        } finally {
            db.close();
            dbh.close();
        }
        return false;
    }


    public void GetImageFromServer() {
        WebServices W = new WebServices();
        W.addQueue("ir.tsip.tracker.zarrintracker.Persons", 1, String.valueOf(ID), "LoadImageById");
        W = null;
    }

    public void GetNameFromServer() {
        WebServices W = new WebServices();
        W.addQueue("ir.tsip.tracker.zarrintracker.Persons", 2, String.valueOf(ID), "LoadNameById");
        W = null;
    }

    public static void backWebServices(int ObjectCode, String Data) {
        if(ObjectCode == 1)
        {
            String[] D = Data.split(";;;");
            if(Data.length()>0 && D.length == 2) {
                Persons p = new Persons();
                if(p.GetData(Integer.valueOf(D[0]))) {
                    byte[] data = Base64.decode(D[1], Base64.DEFAULT);
                    Bitmap bitmap = Tools.getBitmapFromByte(data);
                    p.image = bitmap;
                    p.update();
                }
            }
        }
        if(ObjectCode == 2)
        {
            String[] D = Data.split(";;;");
            if(Data.length()>0 && D.length == 2) {
                Persons p = new Persons();
                if(p.GetData(Integer.valueOf(D[0]))) {
                    p.name = D[1];
                    p.update();
                }
            }
        }
    }

    public static void UpdateImages(){
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getReadableDatabase();
        Persons p=new Persons();
        Cursor c=null;
        try {
           c=db.query(DatabaseContracts.Persons.TABLE_NAME,
                    null,
                    "",
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {
                p.ID= c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_ID));
               p.GetImageFromServer();
            }
        } catch (Exception e) {
        } finally {
            c.close();
            db.close();
            dbh.close();
            p=null;
        }
    }
    public static   ArrayList<Persons> GetAll(){
        DatabaseHelper dbh = new DatabaseHelper(con);
        SQLiteDatabase db = dbh.getReadableDatabase();
        ArrayList<Persons> persons=new ArrayList<>();
        Cursor c =null;
        try {
            c= db.query(DatabaseContracts.Persons.TABLE_NAME,
                    null,
                    "",
                    null,
                    null,
                    null,
                    null);
            if (c.moveToFirst()) {
               do {
                   Persons p=new Persons();
                   p.ID = c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_ID));
                   p.name = c.getString(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_name));
                p.   image = Tools.getBitmapFromByte(c.getBlob(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_NAME_image)));
                p.   isme=c.getInt(c.getColumnIndexOrThrow(DatabaseContracts.Persons.COLUMN_is_me))>0?true:false;
persons.add(p);
               }while (c.moveToNext());
            }
        } catch (Exception e) {
        } finally {
            c.close();
            db.close();
            dbh.close();
        }
        return  persons;
    }
}
