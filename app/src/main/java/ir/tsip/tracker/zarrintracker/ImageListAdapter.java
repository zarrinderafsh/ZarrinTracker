package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;

import java.security.PublicKey;
import java.util.ArrayList;

/**
 * Created by ali on 12/28/15.
 */
public class ImageListAdapter extends ArrayAdapter<Objects.MarkerItem> {

    ArrayList<Objects.MarkerItem> items;
    Activity _activity;
    LayoutInflater inflater;

    public ImageListAdapter(Activity activity ){
        super(activity, R.layout.marker_item_layout );
        _activity=activity;
        items=new ArrayList<>();
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
            convertView = inflater.inflate(R.layout.marker_item_layout, null);
        TextView txtname=(TextView)convertView.findViewById(R.id.txtName);
        ImageView imgphoto=(ImageView)convertView.findViewById(R.id.imgPhoto);
        final Objects.MarkerItem marker=(Objects.MarkerItem)getItem(position);

        if(marker._image==null)
            marker._image= BitmapFactory.decodeResource(_activity.getResources(), R.drawable.sample_user);
        convertView.setTag(marker._id);
        imgphoto.setImageBitmap(marker._image);
        click= new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tools.GoogleMapObj.animateCamera(CameraUpdateFactory.newLatLngZoom(Tools.markers.get(marker._id).getPosition(), 16.0f));
            }
        };
        if(UseDefaultClickListener)
        imgphoto.setOnClickListener(click);
        txtname.setText(marker._name);
        return  convertView;

    }

    private View.OnClickListener click;
    public Boolean UseDefaultClickListener=true;

    public void AddMarker(Objects.MarkerItem markerItem){
        this.items.add(markerItem);
    }

    public Objects.MarkerItem GetItemByID(int id){
        for (Objects.MarkerItem m:items
             ) {
            if(m._id==id)
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
    public Objects.MarkerItem getItem(int position) {
        return items.get(position);
    }
}
