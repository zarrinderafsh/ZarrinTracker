package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class ProfileActivity extends ActionBarActivity {


    private  ImageView ivGetPersonImage;
    private  LinearLayout llEditProfile;
    private  TextView tvName;
    private  TextView tvPhone;
    private  Timer _Timer;

    private static Activity Base;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Base = this;

        ivGetPersonImage =(ImageView) findViewById(R.id.ivGetPersonImage);
        ivGetPersonImage.hasOnClickListeners();
        ivGetPersonImage.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                selectImage();
            }
        });

        llEditProfile = (LinearLayout) findViewById(R.id.llEditProfileShow);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhneNumber);

        View.OnClickListener EditProfile = new View.OnClickListener(){
            public void onClick(View v)
            {
                ShowEditProfile();
            }
        };

        llEditProfile.setOnClickListener(EditProfile);
        tvName.setOnClickListener(EditProfile);
        tvPhone.setOnClickListener(EditProfile);

        //StartServices();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 0);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            1);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);

                    // bm = Bitmap.createScaledBitmap(bm, 70, 70, true);
                    ivGetPersonImage.setImageBitmap(bm);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri selectedImageUri = data.getData();

                String tempPath = selectedImageUri.getPath();//selectedImageUri, ProfileActivity.this);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                ivGetPersonImage.setImageBitmap(bm);
            }
        }
    }

    private void ShowEditProfile()
    {
        Intent myIntent = new Intent(Base , EditProfileActivity.class);
        Base.startActivity(myIntent);
    }

    private void StartServices() {
        if(_Timer != null)
        {
            _Timer.cancel();
            _Timer = null;
        }
        _Timer = new Timer(true);
        _Timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Base.runOnUiThread(new Runnable() {
                    public void run() {

                        if (Tools.isOnline(getApplicationContext()))
                            ((TextView)findViewById(R.id.tvWiFi)).setText("Connected");
                        else
                            ((TextView)findViewById(R.id.tvWiFi)).setText("Not Connected");

                        ((TextView)findViewById(R.id.tvBattery)).setText(String.valueOf((int)Tools.getBatteryLevel(Base)));
                        ((TextView)findViewById(R.id.tvAcc)).setText(String.valueOf(LocationListener.CurrentAccuracy));
                        ((TextView)findViewById(R.id.tvSpeed)).setText(String.valueOf(LocationListener.CurrentSpeed));
                        ((TextView)findViewById(R.id.tvLat)).setText(String.valueOf(LocationListener.CurrentLat));
                        ((TextView)findViewById(R.id.tvLon)).setText(String.valueOf(LocationListener.CurrentLon));
                        ((TextView)findViewById(R.id.tvSignal)).setText(String.valueOf((int)LocationListener.CurrentSignal));
                        ((TextView)findViewById(R.id.tvUserActivity)).setText(String.valueOf(SendDataService.CountPoint));

                    }
                });
            }

        }, 0, 1000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        StartServices();
    }
    @Override
    public void onRestart()
    {
        super.onRestart();
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
    public void onPause()
    {
        super.onPause();
        _Timer.cancel();
    }
    @Override
    public void onStop()
    {
        super.onStop();
        _Timer.cancel();
    }

}
