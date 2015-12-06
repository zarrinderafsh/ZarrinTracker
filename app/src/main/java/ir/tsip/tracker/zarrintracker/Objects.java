package ir.tsip.tracker.zarrintracker;

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

        public GeofenceItem(String Name,int id,double latitude,double longitude,double radius){
            this.name=Name;
            this.id=id;
            this.latitude=latitude;
            this.longitude=longitude;
            this.radius=radius;
        }
    }
}
