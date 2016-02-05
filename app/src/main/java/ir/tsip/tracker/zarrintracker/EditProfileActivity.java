package ir.tsip.tracker.zarrintracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class EditProfileActivity extends ActionBarActivity {


    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
                 super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_edit_profile);
        mContext = this;

        String Data = ShareSettings.getValue("Profile");
        String[] DataP = Data.split(";;;");
        if(DataP.length > 3) {
            EditText tvName = (EditText) findViewById(R.id.etName);
            EditText tvPhone = (EditText) findViewById(R.id.etPhone);
            EditText tvEmail = (EditText) findViewById(R.id.etEmail);
            EditText tvActiveCode = (EditText) findViewById(R.id.etActiveCode);

            tvName.setText(DataP[1]);
            tvPhone.setText(DataP[2]);
            tvEmail.setText(DataP[3]);
            try {
                tvActiveCode.setText(DataP[4]);
            } catch(Exception er) {

            }
        }

        Button clickButton = (Button) findViewById(R.id.btSaveProfile);
        clickButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveProfile();
            }
        });
    }

    public static String getName(Context mContext)
    {
        String Data = ShareSettings.getValue("Profile");
        String[] DataP = Data.split(";;;");
        if(DataP.length >1)
        {
            return DataP[1];
        }
        return "Your name";
    }

    public static String getPhone(Context mContext)
    {
        String Data = ShareSettings.getValue("Profile");
        String[] DataP = Data.split(";;;");
        if(DataP.length >2)
        {
            return DataP[2];
        }
        return "0000 0000 000";
    }

    public void SaveProfile()
    {
        EditText tvName = (EditText) findViewById(R.id.etName);
        EditText tvPhone = (EditText) findViewById(R.id.etPhone);
        EditText tvEmail = (EditText) findViewById(R.id.etEmail);
        EditText tvActiveCode = (EditText) findViewById(R.id.etActiveCode);

        if(tvName.getText().toString().contains(",") ||
                tvName.getText().toString().contains("|")||
                tvName.getText().toString().contains("!")||
                tvName.getText().toString().contains("~")||
                tvName.getText().toString().contains("#"))
        {
            Toast.makeText(EditProfileActivity.this,mContext.getResources().getString(R.string.dontusesymbol) , Toast.LENGTH_SHORT).show();
            return;
        }


        String Data =
                Tools.GetImei(mContext)+";;;"+
                tvName.getText()+";;;"+
                tvPhone.getText()+";;;"+
                tvEmail.getText()+";;;"+
                tvActiveCode.getText();

        ShareSettings.SetValue("Profile", Data);
        SendData(Data);
        this.finish();
    }

    private void SendData(String Data)
    {
        WebServices W = new WebServices(getApplicationContext());
       try {
           W.addQueue("ir.tsip.tracker.zarrintracker.EditProfileActivity", 0, Data, "SaveProfile");
       }
       catch (  Exception er){
       }
           W=null;
   }

    public static void backWebServices (int ObjectCode, String Data)
    {
        try {
            Toast.makeText(mContext, mContext.getResources().getString(R.string.successful), Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {

        }
    }



}
