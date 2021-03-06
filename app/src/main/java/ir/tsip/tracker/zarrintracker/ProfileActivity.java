package ir.tsip.tracker.zarrintracker;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class ProfileActivity extends ActionBarActivity {


    private ImageView ivGetPersonImage;
    private LinearLayout llEditProfile;
    private TextView tvName;
    private TextView tvPhone;
    private Timer _Timer;
    private static Context _context;
    private static ProfileActivity Base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Base = this;

        ivGetPersonImage = (ImageView) findViewById(R.id.ivGetPersonImage);
        ivGetPersonImage.hasOnClickListeners();
        ivGetPersonImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                selectImage();
            }
        });

        llEditProfile = (LinearLayout) findViewById(R.id.llEditProfileShow);
        tvName = (TextView) findViewById(R.id.tvName);
        tvPhone = (TextView) findViewById(R.id.tvPhonNumber);

        View.OnClickListener EditProfile = new View.OnClickListener() {
            public void onClick(View v) {
                ShowEditProfile();
            }
        };

        llEditProfile.setOnClickListener(EditProfile);
        tvName.setOnClickListener(EditProfile);
        tvPhone.setOnClickListener(EditProfile);

        //StartServices();
    }




    private void selectImage() {
        final CharSequence[] items = {
                getResources().getString(R.string.takephoto),
                getResources().getString(R.string.chooseFromLibrary),
               getResources().getString(R.string.cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getResources().getString(R.string.takephoto))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment
                            .getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 0);
                } else if (items[item].equals(getResources().getString(R.string.chooseFromLibrary))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            1);
                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bm = null;
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
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);

                    f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                try {
                    bm = getBitmapFromUri(selectedImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (bm != null) {
            ShareSettings.SetValue("ProfileImage", "");
            if (SaveBitmap(bm)) {
                SendImageServer();
            }
        }
    }


    private static Boolean SaveBitmap(Bitmap bm) {
        boolean ret = false;
        String path = ShareSettings.getValue("ProfileImage");
        if (path.length() == 0) {
            path = android.os.Environment
                    .getExternalStorageDirectory()
                    + File.separator
                    + "Pictures" + File.separator + "Ztracker"
                    + File.separator
                    + (new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-", Locale.US).format(new Date())
                    + "profilePic.jpg"
            );
        }
        File file = new File(path);
        boolean success = true;
        if (!file.getParentFile().exists()) {
            success = file.getParentFile().mkdir();
        }
        if (!success)
            return false;

        OutputStream fOut = null;
        if (file.exists())
            file.delete();
        try {
            fOut = new FileOutputStream(file);

            Bitmap resize = Bitmap.createScaledBitmap(bm, 512, 512, true);
            ret = resize.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            ShareSettings.SetValue("ProfileImage", file.getPath());
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static void setProfileImage(ImageView ivPersonImage, int Radious, Context mBase) {
        String path = ShareSettings.getValue("ProfileImage");
        if (path.length() > 0) {
            File file = new File(path);
            if (!file.exists()) {
                ShareSettings.SetValue("ProfileImage", "");
                return;
            }
            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
            try {
                ivPersonImage.setImageBitmap(
                        CircleImage.getRoundedRectBitmap(
                                Bitmap.createScaledBitmap(
                                        BitmapFactory.decodeFile(file.getAbsolutePath(), btmapOptions), Radious, Radious, true), Radious
                        )
                );
            } catch (Exception er) {
            }
        }
    }


    public static Bitmap getProfileImage(int Radious, Context mBase) {
        String path = ShareSettings.getValue("ProfileImage");
        if (path.length() > 0) {
            File file = new File(path);
            if (!file.exists()) {
                ShareSettings.SetValue("ProfileImage", "");
                return null;
            }
            BitmapFactory.Options btmapOptions = new BitmapFactory.Options();

            return CircleImage.getRoundedRectBitmap(
                    Bitmap.createScaledBitmap(
                            BitmapFactory.decodeFile(file.getAbsolutePath(), btmapOptions), Radious, Radious, true), Radious
            );
        }

        return null;
    }

    public void SendImageServer()
    {
        String path = ShareSettings.getValue("ProfileImage");
        if(path.length() > 0) {
            File file = new File(path);
            if(!file.exists()) {
                ShareSettings.SetValue("ProfileImage", "");
                return;
            }
            Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath());

            WebServices W = new WebServices(Base);
            byte[] res = Tools.getBytesFromBitmap(bm);
            byte[] imei = Tools.GetImei(Base).getBytes();

            byte[] destination = new byte[imei.length + res.length + 1];
            destination[0] = (byte)imei.length;
            System.arraycopy(imei, 0, destination, 1, imei.length);
            //destination[imei.length] = (byte)255;
            System.arraycopy(res, 0 , destination, imei.length+1 , res.length);
            W.addQueue("ir.tsip.tracker.zarrintracker.ProfileActivity",0,Base64.encode(destination,Base64.DEFAULT),"SaveImage");
            W=null;
        }
    }

    public static void backWebServices (int ObjectCode, String Data) {
        switch (ObjectCode) {
            case 0:// SaveImage
                Toast.makeText(_context,_context.getResources().getString(R.string.imageUploaded), Toast.LENGTH_SHORT).show();
                break;
            case 1:// GetProfile
                ShareSettings.SetValue("Profile", Tools.GetImei(_context) + ";;;" + Data + ";;;0");
                break;
            case 2:// GetImage
                try {
                    byte[] data = Base64.decode(Data, Base64.DEFAULT);
                    Bitmap bitmap = Tools.getBitmapFromByte(data);
                    SaveBitmap(bitmap);

                } catch (Exception er) {
                }
                break;
        }

    }

    public static void GetProfileFromServer(Context context)
    {
        _context=context;
        WebServices W = new WebServices(context);
        W.addQueue("ir.tsip.tracker.zarrintracker.ProfileActivity",1,Tools.GetImei(context),"loadprofile",1);
        W=null;
    }

    public static void GetImageFromServer(Context context)
    {
        String path = ShareSettings.getValue("ProfileImage");
        if (path.length() == 0) {
            _context = context;
            WebServices W = new WebServices(context);
            W.addQueue("ir.tsip.tracker.zarrintracker.ProfileActivity", 2, Tools.GetImei(context), "loadimage",1);
            W = null;
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

        ((TextView)findViewById(R.id.tvName)).setText(EditProfileActivity.getName(this.getBaseContext()));
        ((TextView)findViewById(R.id.tvPhonNumber)).setText(EditProfileActivity.getPhone(this.getBaseContext()));

        setProfileImage(ivGetPersonImage,256,Base);
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
