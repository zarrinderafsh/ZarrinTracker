package ir.tsip.tracker.zarrintracker;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JoinGroupActivity extends AppCompatActivity {

    private static com.android.volley.RequestQueue queue;
    EditText txtCode,txtDeviceName;
    ImageButton ibtnAddToGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);

        InitializeWidgets();
    }

    private  void InitializeWidgets() {
        /*********************************************************txtCode*/
        txtCode = (EditText) findViewById(R.id.txtJoinCode);
        //When control lose or reach focus.
        txtCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String ed_text = txtCode.getText().toString().trim();

                if((ed_text.isEmpty() || ed_text.length() == 0 || ed_text.equals("") || ed_text == null) && !hasFocus)
                    txtCode.setText("ENTER CODE HERE!");
                else if (txtCode.getText().toString().contains("ENTER CODE HERE!") && hasFocus)
                    txtCode.setText("");
            }
        });
        /*********************************************************txtDeviceName*/
        txtDeviceName = (EditText) findViewById(R.id.txtJoinDeviceName);
        //When control lose or reach focus.
        txtDeviceName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String ed_text = txtDeviceName.getText().toString().trim();

                if((ed_text.isEmpty() || ed_text.length() == 0 || ed_text.equals("") || ed_text == null) && !hasFocus)
                    txtDeviceName.setText("ENTER A NAME!");
                else if (txtDeviceName.getText().toString().contains("ENTER A NAME!") && hasFocus)
                    txtDeviceName.setText("");
            }
        });
        /*********************************************************ibtnAddToGroup*/
        ibtnAddToGroup = (ImageButton) findViewById(R.id.ibtnAddToGroup);
        //Raise when clicked
        ibtnAddToGroup.setOnClickListener(new View.OnClickListener() {
            final String url = "http://tstracker.ir/services/webbasedefineservice.asmx/AddDevice";
            @Override
            public void onClick(View v) {

                Map<String, String> params = new HashMap<>();
                params.put("key", txtCode.getText().toString());
                params.put("imei", Tools.GetImei(getApplicationContext()));
                params.put("name", txtDeviceName.getText().toString());
                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                        new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String data = response.getString("d");
                            if (data.contains("1")) {
                                Toast.makeText(JoinGroupActivity.this, "Your device registered.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception er) {
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                if (queue == null)
                    queue = Volley.newRequestQueue(getApplicationContext());
                queue.add(jsObjRequest);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_join_group, menu);
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
