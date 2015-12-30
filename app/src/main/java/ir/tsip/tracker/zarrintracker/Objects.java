package ir.tsip.tracker.zarrintracker;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by ali on 11/15/15.
 */
public class Objects {

    public  class GroupListItem{
        String name;
        String MemberCount;

        public GroupListItem(String Name,String MemberCount){
            this.name=Name;
            this.MemberCount=MemberCount;
        }
    }

    public  class GeofenceItem{
        String name;
        int id=0;
        double latitude=0;
        double longitude=0;
        double radius=0;
        Boolean isOwner=false;

        public GeofenceItem(String Name,int id,double latitude,double longitude,double radius,Boolean isOwner){
            this.name=Name;
            this.id=id;
            this.latitude=latitude;
            this.longitude=longitude;
            this.radius=radius;
            this.isOwner=isOwner;
        }
    }
    public class MarkerItem{
        int _id;
        Bitmap _image;
        String _name;
        String _lastmessage;
        LatLng _lastLocation;
        public MarkerItem(int id,Bitmap image,String name,String lastmessage,LatLng lastLocation){
            _id=id;
            _image=image;
            _name=name;
            _lastLocation=lastLocation;
            _lastmessage=lastmessage;
        }
    }

    public class MenuItem{
        public int id;
        public Bitmap image;
        public String text;
    }

}
