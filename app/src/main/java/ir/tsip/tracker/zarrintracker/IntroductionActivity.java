package ir.tsip.tracker.zarrintracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class IntroductionActivity extends AppCompatActivity {

    ImageButton ibtnNext,ibtnPrev;
    TextView txtSkip;
    ImageView imgPic;
    int index=0;
    boolean startMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_introduction);

        startMain=getIntent().getBooleanExtra("sm",true);

        ibtnNext=(ImageButton)findViewById(R.id.ibtnnext);
        ibtnPrev=(ImageButton)findViewById(R.id.ibtnPrev);
        imgPic=(ImageView)findViewById(R.id.imgPic);
        txtSkip=(TextView)findViewById(R.id.txtSkip);

        ibtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                imgPic.setImageBitmap(BitmapFactory.decodeResource(IntroductionActivity.this.getResources(),getPhotoResourceId(index)));

                ibtnPrev.setEnabled(true);
                if(index==7){
                    LunchMainActvity();
                }
            }
        });
        ibtnPrev.setEnabled(false);
        ibtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                imgPic.setImageBitmap(BitmapFactory.decodeResource(IntroductionActivity.this.getResources(), getPhotoResourceId(index)));

                if (index <= 0) {
                    ibtnPrev.setEnabled(false);
                    return;
                }
            }
        });
        txtSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LunchMainActvity();
            }
        });
    }

    private void LunchMainActvity(){
        if(startMain) {
            Intent intent = new Intent(IntroductionActivity.this, MainActivity.class);
            startActivity(intent);
        }
        IntroductionActivity.this.finish();
    }

    private  int getPhotoResourceId(int i){
        switch (i){
            case 0:
                return R.drawable.familyintro;
            case 1:
                return R.drawable.schoolintro;
            case 2:
                return R.drawable.travelingintro;
            case 3:
                return R.drawable.mountainintro;
            case 4:
                return R.drawable.marketingintro;
            case 5:
                return R.drawable.deliveryintro;
            case 6:
                return R.drawable.otherintro;
            default:
                return  R.drawable.arm;
        }
    }

}
