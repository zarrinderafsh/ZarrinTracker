package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Invite extends ActionBarActivity {

    private ImageView inInvite;
    private static Activity Base;
    private static com.android.volley.RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invate);

        Base = this;
        inInvite = (ImageView) findViewById(R.id.ivSendInvation);
        inInvite.setOnClickListener(new View.OnClickListener(){
                final String url = "http://tstracker.ir/services/webbasedefineservice.asmx/GenerateJoinKey" +
                        "";
                @Override
                public void onClick(View v) {

                    Map<String, String> params = new HashMap<>();
                    params.put("imei", Tools.GetImei(getApplicationContext()));
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,
                            new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String data = response.getString("d");
                                if (data!=("0")) {
                                    Tools.ShareText(data, Base);
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
        getMenuInflater().inflate(R.menu.menu_invate, menu);
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
