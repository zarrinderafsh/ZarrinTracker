package ir.tsip.tracker.zarrintracker;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;

import java.util.ArrayList;

/**
 * Created by ali on 12/29/15.
 */
public class MenuItemsAdapter extends BaseAdapter {

    ArrayList<Objects.MenuItem> items;
    Activity _activity;
    LayoutInflater inflater;


    public MenuItemsAdapter(Activity activity ){
        _activity=activity;
        items=new ArrayList<Objects.MenuItem>();
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.menu_item, null);


        final  Objects.MenuItem item=(Objects.MenuItem)this.getItem(position);
        LinearLayout lyt=(LinearLayout)convertView.findViewById(R.id.lytImageAndText);
        if(item.type==0) {
            TextView txtText = (TextView) convertView.findViewById(R.id.txtText);
            ImageView imgphoto = (ImageView) convertView.findViewById(R.id.imgPhoto);

            if (item.customTag != null)
                convertView.setTag(R.string.DontTranslate2, item.customTag);
            //item=-1 means item is title of menu
            if (item.id == -1) {
                convertView.setBackgroundColor(Color.parseColor("#ff0a86cd"));
                ViewGroup.LayoutParams layoutParams = imgphoto.getLayoutParams();
                layoutParams.height = 64;
                layoutParams.width = 64;
                imgphoto.setLayoutParams(layoutParams);
            }
            if (item.image == null)
                item.image = BitmapFactory.decodeResource(_activity.getResources(), R.drawable.sample_user);
            imgphoto.setImageBitmap(item.image);

            txtText.setText(item.text);

            ((Button)convertView.findViewById(R.id.btnMenuItem)).setVisibility(View.GONE);
            ((Switch)convertView.findViewById(R.id.swch)).setVisibility(View.GONE);
            ((RadioGroup)convertView.findViewById(R.id.rdpgMenuItem)).setVisibility(View.GONE);
        }
        else if(item.type==1){//Switcher
            Switch swch=(Switch)convertView.findViewById(R.id.swch);
            swch.setText(item.text);
            swch.setOnClickListener(item.clickEvent);
            if(item.checked==1)
                swch.setChecked(true);
            else
                swch.setChecked(false);
            swch.setVisibility(View.VISIBLE);
            lyt.setVisibility(View.GONE);
            ((Button)convertView.findViewById(R.id.btnMenuItem)).setVisibility(View.GONE);
            ((RadioGroup)convertView.findViewById(R.id.rdpgMenuItem)).setVisibility(View.GONE);

        }
        else if(item.type==2)//button
        {
            Button b=(Button)convertView.findViewById(R.id.btnMenuItem);
            b.setText(item.text);
            b.setOnClickListener(item.clickEvent);
            b.setVisibility(View.VISIBLE);
            lyt.setVisibility(View.GONE);

            ((Switch)convertView.findViewById(R.id.swch)).setVisibility(View.GONE);
            ((RadioGroup)convertView.findViewById(R.id.rdpgMenuItem)).setVisibility(View.GONE);

        }
        else if(item.type==3)//radioGroup
        {
            RadioGroup rdpg=(RadioGroup)convertView.findViewById(R.id.rdpgMenuItem);
        rdpg.removeAllViews();
          int i=0;
            for (String s:item.radiosTexts               ) {
                RadioButton r = new RadioButton(this._activity);
                r.setTextColor(Color.WHITE);
                r.setText(s);
                r.setId(i);
                if (i == item.checked) {
                    rdpg.setTag(i);
                    r.setChecked(true);
                }
                rdpg.addView(r);
                i++;

            }

            rdpg.setOnCheckedChangeListener(item.checkedChangeListener);
            rdpg.setOrientation(LinearLayout.HORIZONTAL);
            rdpg.setVisibility(View.VISIBLE);
            lyt.setVisibility(View.GONE);
            ((Button)convertView.findViewById(R.id.btnMenuItem)).setVisibility(View.GONE);
            ((Switch)convertView.findViewById(R.id.swch)).setVisibility(View.GONE);


        }
        return  convertView;

    }

    public void AddItem(Objects.MenuItem markerItem){
        this.items.add(markerItem);
    }

    public void RemoveItem(int position)
    {
        this.items.remove(position);
        notifyDataSetChanged();
    }

    public Objects.MenuItem GetItemByID(int id){
        for (Objects.MenuItem m:items
                ) {
            if(m.id==id)
                return m;
        }
        return  null;
    }

    public void Clear(){
        items.clear();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }
}

