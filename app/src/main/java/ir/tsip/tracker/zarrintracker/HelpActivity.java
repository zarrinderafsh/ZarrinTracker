package ir.tsip.tracker.zarrintracker;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_help);

        int index=getIntent().getIntExtra("index",-1);


    }


    private void SetContent(int i){
        switch (i){
            case 0://groups
                break;
            case 1://offlinemap
                break;
            case 2://map places activity
                break;
            default:
                break;
        }
    }

}
