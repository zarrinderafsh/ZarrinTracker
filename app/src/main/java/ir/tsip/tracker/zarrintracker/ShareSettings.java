package ir.tsip.tracker.zarrintracker;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by morteza on 2015-11-12.
 */
public class ShareSettings {

    public static void SetValue(String Name, String Value)
    {
        if(MainActivity.Base == null)
            return;
        Context mContext = MainActivity.Base;
        SharedPreferences prefs = mContext.getSharedPreferences(
                "ir.tsip.tracker.zarrintracker", mContext.MODE_PRIVATE);
        prefs.edit().putString(Name, Value).apply();
    }

    public static String getValue(String Name)
    {
        if(MainActivity.Base == null)
            return "";
        Context mContext = MainActivity.Base;
        SharedPreferences prefs = mContext.getSharedPreferences(
                "ir.tsip.tracker.zarrintracker", mContext.MODE_PRIVATE);
        return prefs.getString(Name,"");
    }
}
