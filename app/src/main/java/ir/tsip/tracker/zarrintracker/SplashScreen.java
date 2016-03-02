package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import java.io.File;
import java.util.Locale;

/**
 * Created by vamsikrishna on 12-Feb-15.
 */
public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
                 super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.splash);



        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    Intent intent;
                    if(CheckRegister()) {
                        intent = new Intent(SplashScreen.this, MainActivity.class);
                    }
                    else
                    {
                         intent = new Intent(SplashScreen.this, IntroductionActivity.class);
                        DatabaseHelper s=new DatabaseHelper(SplashScreen.this);
                        s.onDowngrade(s.getWritableDatabase(),0,1);
                    }

                    Tools.Mute=Tools.getMute(SplashScreen.this);
                    Locale locale=new Locale(Tools.getLocale(SplashScreen.this));
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

    private boolean CheckRegister()
    {
        File f=this.getDatabasePath(DatabaseHelper.DATABASE_NAME);
        return f.exists();
    }

}