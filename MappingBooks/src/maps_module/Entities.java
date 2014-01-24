package maps_module;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radu on 17/12/13.
 */
public class Entities {
    public List<Location> locations;
    public List<Location> invisibleLocations;

    public Entities() {
        locations = new ArrayList<Location>();
        invisibleLocations = new ArrayList<Location>();
    }

    public List<Location> getLocations() {
        for(int index = 0; index < 1; index++) {
            LatLng l = new LatLng(47.157739, 27.586795);
            Location loc = new Location("Locatiune");
            loc.setLatitude(l.latitude);
            loc.setLongitude(l.longitude);
            locations.add(loc);
        }

        return locations;
    }

    public List<Location> getInvisibleLocations() {
        for(int index = 0; index < 1; index++) {
            LatLng l = new LatLng(47.186213, 27.560551);
            Location loc = new Location("Locatiune");
            loc.setLatitude(l.latitude);
            loc.setLongitude(l.longitude);
            invisibleLocations.add(loc);
        }

        return invisibleLocations;
    }
}
