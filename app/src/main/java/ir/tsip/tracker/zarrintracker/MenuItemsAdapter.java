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
import android.widget.ImageView;
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

        TextView txtText=(TextView)convertView.findViewById(R.id.txtText);
        ImageView imgphoto=(ImageView)convertView.findViewById(R.id.imgPhoto);
        final  Objects.MenuItem item=(Objects.MenuItem)this.getItem(position);
        //item=-1 means item is title of menu
        if(item.id==-1){
            convertView.setBackgroundColor(Color.parseColor("#ff0a86cd"));
            ViewGroup.LayoutParams layoutParams=imgphoto.getLayoutParams();
            layoutParams.height=64;
            layoutParams.width=64;
            imgphoto.setLayoutParams(layoutParams);
        }
        if(item.image==null)
            item.image= BitmapFactory.decodeResource(_activity.getResources(), R.drawable.sample_user);
        imgphoto.setImageBitmap(item.image);

        txtText.setText(item.text);
        return  convertView;

    }

    public void AddItem(Objects.MenuItem markerItem){
        this.items.add(markerItem);
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

