package ir.tsip.tracker.zarrintracker;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.HashMap;
import java.util.Map;


public class EditProfileActivity extends ActionBarActivity {

    private static com.android.volley.RequestQueue queue;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mContext = this;

        String Data = ShareSettings.getValue(mContext,"Profile");
        String[] DataP = Data.split(";;;");
        if(DataP.length == 5)
        {
            EditText tvName = (EditText) findViewById(R.id.etName);
            EditText tvPhone = (EditText) findViewById(R.id.etPhone);
            EditText tvEmail = (EditText) findViewById(R.id.etEmail);
            EditText tvActiveCode = (EditText) findViewById(R.id.etActiveCode);

            tvName.setText(DataP[1]);
            tvPhone.setText(DataP[2]);
            tvEmail.setText(DataP[3]);
            tvActiveCode.setText(DataP[4]);
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
        String Data = ShareSettings.getValue(mContext,"Profile");
        String[] DataP = Data.split(";;;");
        if(DataP.length == 5)
        {
            return DataP[1];
        }
        return "Your name";
    }

    public static String getPhone(Context mContext)
    {
        String Data = ShareSettings.getValue(mContext,"Profile");
        String[] DataP = Data.split(";;;");
        if(DataP.length == 5)
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

        String Data =
                Tools.GetImei(mContext)+";;;"+
                tvName.getText()+";;;"+
                tvPhone.getText()+";;;"+
                tvEmail.getText()+";;;"+
                tvActiveCode.getText();

        ShareSettings.SetValue(mContext,"Profile",Data);
        SendData(Data);
    }

    private void SendData(String Data)
    {
        WebServices W = new WebServices(getApplicationContext());
        W.addQueue("ir.tsip.tracker.zarrintracker.EditProfileActivity",0,Data,"SaveProfile");
        Toast.makeText(mContext, "Send save profile ... .", Toast.LENGTH_SHORT).show();
    }

    public static void backWebServices (int ObjectCode, String Data)
    {
        try {
            Toast.makeText(mContext, "Send save success.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_profile, menu);
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

        return super.onOptionsItemSelected(item);
    }

}
