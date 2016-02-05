package ir.tsip.tracker.zarrintracker;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_help);

        int index=getIntent().getIntExtra("index",-1);
        ((ImageView)findViewById(R.id.imgHelpPic)).setImageBitmap(BitmapFactory.decodeResource(this.getResources(),SetbITMAP(index)));

    }


    private int SetbITMAP(int i){
        switch (i){
            case 0://groups
                return R.drawable.groupshelp;
            case 1://offlinemap
                return R.drawable.arm;//offlinehelp;
            case 2://map placeshelp activity
                return R.drawable.placeshelp;
            default:
                return R.drawable.arm;
        }
    }

}
