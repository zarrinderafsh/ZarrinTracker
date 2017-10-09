package ir.tsip.tracker.zarrintracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.util.Locale;

/**
 * Created by ali heidari
 */
public class SplashScreen extends Activity {

    public static Boolean isnotFirstTIme;

    //    String[] permissions = {
//           // Manifest.permission.RECEIVE_BOOT_COMPLETED,
//permissions[0].equals(Manifest.permission.READ_PHONE_STATE
//           // , Manifest.permission.INTERNET
//            , Manifest.permission.ACCESS_COARSE_LOCATION
//           // , Manifest.permission.ACCESS_FINE_LOCATION
//          //  , Manifest.permission.ACCESS_NETWORK_STATE
//            , Manifest.permission.WRITE_EXTERNAL_STORAGE
//          //  , "com.google.android.providers.gsf.permission.READ_GSERVICES"
//          //  , Manifest.permission.VIBRATE
//         //   , "android.permission.INTERACT_ACROSS_USERS_FULL"
//           // , "com.farsitel.bazaar.permission.PAY_THROUGH_BAZAAR"
//    };
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    int permissionCount = 0;





    public boolean checkPermissions(String permission) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permission != null) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions( new String[]{Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        return true;
    }




        @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            for (int i = 0; i < permissions.length; i++) {
                switch (permissions[i]) {
                    case Manifest.permission.READ_PHONE_STATE:
//                        checkPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
                        break;
                    case Manifest.permission.ACCESS_COARSE_LOCATION:
//                        checkPermissions( Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        break;
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
//                        StartApp();
                        break;
                }
            }
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.splash);
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            checkPermissions( Manifest.permission.READ_PHONE_STATE);
//        } else
        StartApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    Boolean appstarted = false;

    private void StartApp() {

        if (Build.VERSION.SDK_INT >= 23)
            if(!checkPermissions( Manifest.permission.READ_PHONE_STATE)) return;

        Thread timerThread = new Thread() {
            public void run() {
                isnotFirstTIme = CheckRegister();

                try {
                    ProfileActivity.GetImageFromServer(SplashScreen.this);
                    ProfileActivity.GetProfileFromServer(SplashScreen.this);
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent intent;
                    if (isnotFirstTIme) {
                        intent = new Intent(SplashScreen.this, MainActivity.class);
                    } else {
                        intent = new Intent(SplashScreen.this, IntroductionActivity.class);
                        DatabaseHelper s = new DatabaseHelper(SplashScreen.this);
                        s.onDowngrade(s.getWritableDatabase(), 0, 1);
                    }

                    Tools.Mute = Tools.getMute(SplashScreen.this);
                    Locale locale = new Locale(Tools.getLocale(SplashScreen.this));
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());

                    startActivity(intent);
                    overridePendingTransition(R.anim.slid_in, R.anim.slid_out);
                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private boolean CheckRegister() {
        File f = this.getDatabasePath(DatabaseHelper.DATABASE_NAME);
        return f.exists();
    }

}