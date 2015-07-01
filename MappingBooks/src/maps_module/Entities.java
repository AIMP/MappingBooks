package maps_module;


//I was here
import android.location.Location;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class Entities {
    public HashMap<String,String> locations;
    public List<Location> invisibleLocations;

    public Entities() {
        locations = new HashMap<String,String>();
        invisibleLocations = new ArrayList<Location>();
    }

    public HashMap<String, String> getLocations() {
        locations.put("Palatul Culturii","47.157739,27.586795");
        locations.put("Stefan cel Mare","47.166172,27.579779");
        locations.put("Parcul Copou","47.178614,27.56699");
        return locations;
    }

    public List<Location> getInvisibleLocations() {
        /*LatLng l = new LatLng(47.186213, 27.560551);
        Location loc = new Location("Locatiune");
        loc.setLatitude(l.latitude);
        loc.setLongitude(l.longitude);
        invisibleLocations.add(loc);
        
        LatLng l2 = new LatLng(47.161824,27.582071);
        Location loc2 = new Location("Locatiune2");
        loc2.setLatitude(l2.latitude);
        loc2.setLongitude(l2.longitude);
        invisibleLocations.add(loc2);
*/
        return invisibleLocations;
    }
}
