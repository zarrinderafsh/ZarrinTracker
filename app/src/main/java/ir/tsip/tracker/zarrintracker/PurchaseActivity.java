package ir.tsip.tracker.zarrintracker;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PurchaseActivity extends AppCompatActivity {

    Button btnPurchase;
    TextView txtMessage;
HashMap<String,String> products=new HashMap<>();
     com.android.vending.billing.IInAppBillingService mService;
     ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);


        //ir.tsip.tracker.zarrintracker.PurchaseActivity
        Intent serviceIntent = new Intent( "ir.cafebazaar.pardakht.InAppBillingService.BIND");
        serviceIntent.setPackage("com.farsitel.bazaar");
        PurchaseActivity.this.bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

        btnPurchase = (Button) findViewById(R.id.btnPurchase);
        txtMessage = (TextView) findViewById(R.id.txtmsg);
        txtMessage.setText(getIntent().getStringExtra("msg"));
        /******************************************************************* THREAD */
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //List of items
                ArrayList skuList = new ArrayList();
                skuList.add("OneMonth");
                skuList.add("Family");
                Bundle querySkus = new Bundle();
                querySkus.putStringArrayList("Family", skuList);
                //get list id of products
                try {
                    Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "subs", querySkus);
                    //get products details
                    int response = skuDetails.getInt("RESPONSE_CODE");
                    if (response == 0) {
                        ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                        for (String thisResponse1 : responseList) {
                            try {
                                JSONObject object = new JSONObject(thisResponse1);
                                if (!products.containsKey(object.getString("productId")))
                                    products.put(object.getString("productId"), object.getString("price"));
                            } catch (Exception er) {
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                btnPurchase.setEnabled(true);
                                btnPurchase.setText(getResources().getString(R.string.btnPurchaseWaitMessage));
                            }
                        });
                    }
                } catch (Exception er) {
                }
            }
        });

        /******************************************************************* THREAD */

        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //subscription
                try {
                    Bundle bundle = mService.getBuyIntent(3,   PurchaseActivity.this.getPackageName(), "Family", "subs", "developerPayload");

                    PendingIntent pendingIntent = bundle.getParcelable("BUY_INTENT");
                    if (bundle.getInt("RESPONSE_CODE") == 0) {
                        // Start purchase flow (this brings up the Google Play UI).
                        // Result will be delivered through onActivityResult().
                       PurchaseActivity.this. startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));

                    }
                } catch (Exception er) {
int vs=0;
                }


            }
        });


    }

    private static  JSONObject jo;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            HashMap<String, String> params = new HashMap<>();
            if (resultCode == RESULT_OK) {
                try {
                    jo= new JSONObject(purchaseData);
                  //  String sku = jo.getString("productId");
                    params.put("gateway", "bazar");
                    params.put("Data", purchaseData);

                    WebServices w=new WebServices(this);
                    w.addQueue("ir.tsip.tracker.zarrintracker.PurchaseActivity",0,params,"Purchase");
                    w=null;

                    Toast.makeText(PurchaseActivity.this, "You have bought the \" + sku + \". Excellent choice, adventurer!", Toast.LENGTH_SHORT).show();
                }
                catch (JSONException e) {
                    Toast.makeText(PurchaseActivity.this, "Failed to parse purchase data.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchase, menu);
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
