package ir.tsip.tracker.zarrintracker;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ali on 11/15/15.
 */


public class GroupListAdapter extends BaseAdapter {
    ArrayList<Objects.GroupListItem> items;
    Activity activity;
    private LayoutInflater inflater;

    public GroupListAdapter(Activity activity,ArrayList<Objects.GroupListItem> Items){
   super();
        this.items=Items;
        this.activity=activity;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.groupitemlayout, null);

       Objects.GroupListItem item=(Objects.GroupListItem)this.getItem(position);

        ((TextView)convertView.findViewById(R.id.txtGroupNameListItem)).setText(item.name +" "+convertView.getResources().getString( R.string.GroupLabel));
        ((TextView)convertView.findViewById(R.id.txtCountGroupListItem)).setText(item.MemberCount);
        //((ImageView)findViewById(R.id.imgAvatarGroupListItem)).set(item.name);
        return  convertView;
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