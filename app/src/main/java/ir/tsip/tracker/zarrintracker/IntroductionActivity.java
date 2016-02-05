package ir.tsip.tracker.zarrintracker;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IntroductionActivity extends AppCompatActivity {

    ImageButton ibtnNext,ibtnPrev;
    TextView txtSkip,txtcaption;
    ImageView imgPic;
    int index=0;
    boolean startMain;
    RelativeLayout lyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_introduction);

        lyt=(RelativeLayout)findViewById(R.id.lyt);
txtcaption=(TextView)findViewById(R.id.txtCpation);
        txtcaption.setText(getResources().getString(R.string.introfamily));

        startMain=getIntent().getBooleanExtra("sm",true);

        ibtnNext=(ImageButton)findViewById(R.id.ibtnnext);
        ibtnPrev=(ImageButton)findViewById(R.id.ibtnPrev);
        imgPic=(ImageView)findViewById(R.id.imgPic);
        txtSkip=(TextView)findViewById(R.id.txtSkip);

        ibtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if(index==7){
                    LunchMainActvity();
                    return;
                }
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
                txtcaption.setText(this.getResources().getString(R.string.introfamily));
                return R.drawable.familyintro;
            case 1:
                txtcaption.setText(this.getResources().getString(R.string.introschool));
                return R.drawable.schoolintro;
            case 2:
                txtcaption.setText(this.getResources().getString(R.string.introtravel));
                return R.drawable.travelingintro;
            case 3:
                txtcaption.setText(this.getResources().getString(R.string.intromountain));
                return R.drawable.mountainintro;
            case 4:
                txtcaption.setText(this.getResources().getString(R.string.intromarketing));
                return R.drawable.marketingintro;
            case 5:
                txtcaption.setText(this.getResources().getString(R.string.introdelivery));
                return R.drawable.deliveryintro;
            case 6:
                txtcaption.setText(this.getResources().getString(R.string.introother));
                return R.drawable.otherintro;
            default:
                return  R.drawable.arm;
        }
    }

}
