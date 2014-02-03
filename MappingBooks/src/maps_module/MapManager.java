package maps_module;

import interface_module.BookViewerActivity;
import interface_module.Place;
import interface_module.async_tasks.LocationChangesRequestAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;

public class MapManager {
	private static MapManager instance = null;
	protected LocationManager locationManager;
	protected Criteria criteria;
	protected Location location;
	private String provider;
	private HashMap<String, String> markers;
	private List<Location> invisibleLocations;
	private GoogleMap map;
	private Location drawnEnd = null;
	private Polyline linie = null;
	private float maxDist = 5000;

	protected MapManager() {

	}

	public static MapManager getInstance() {
		if (instance == null) {
			instance = new MapManager();
		}
		return instance;
	}

	public void setLocation(Location l) {
		this.location = l;
	}

	public void linkWith(LocationManager lm, GoogleMap map, BookViewerActivity activity) {
		this.locationManager = lm;
		this.map = map;
		this.map.setMyLocationEnabled(true);
		criteria = new Criteria();
		provider = lm.getBestProvider(criteria, true);
		location = lm.getLastKnownLocation(provider);
		new LocationChangesRequestAsyncTask(activity).execute(new String[] {
				activity.getCurrentSessionID(), activity.getCurrentBookID(),
				String.valueOf(location.getLatitude()),
				String.valueOf(location.getLongitude()) });
		markers = new HashMap<String, String>();
		invisibleLocations = new ArrayList<Location>();
		if (location != null)
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 14));

	}

	public void setup() {
		addMarkersToMap();

		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker arg0) {

				Location start, end;
				start = location;

				LatLng pos = arg0.getPosition();
				end = new Location("End");
				end.setLatitude(pos.latitude);
				end.setLongitude(pos.longitude);

				if (end != drawnEnd) {
					List<Location> arr = getWaypoints(start, end);
					drawRoute(start, end, arr);
				}

				arg0.showInfoWindow();

				return true;
			}

		});
	}

	public void refreshWithData(ArrayList<Place> newPlaces) {
		map.clear();
		markers.clear();
		for (Place place : newPlaces) {
			markers.put(place.getName(), String.valueOf(place.getLatitude())
					+ "," + String.valueOf(place.getLongitude()));
		}
		setup();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addMarkersToMap() {
		Entities entities = new Entities();
		HashMap<String, String> locations = entities.getLocations();
		Set set = locations.entrySet();
		Iterator it = set.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> me = (Map.Entry<String, String>) it
					.next();
			Location location = new Location(me.getValue());
			String[] latlong = me.getValue().split(",");
			location.setLatitude(Double.parseDouble(latlong[0]));
			location.setLongitude(Double.parseDouble(latlong[1]));
			drawMarker(location, me.getKey(), false);
			me.getKey();
			me.getValue();
		}
	}

	private void drawRoute(final Location start, final Location end,
			final List<Location> waypoints) {
		AsyncRouteDraw task = new AsyncRouteDraw();
		Location[] arr;
		if (waypoints != null && waypoints.size() > 0) {
			arr = new Location[2 + waypoints.size()];
		} else {
			arr = new Location[2];
		}

		arr[0] = start;
		arr[1] = end;

		Log.v("TAG/Start", arr[0].toString());
		Log.v("TAG/End", arr[1].toString());

		if (waypoints != null && waypoints.size() > 0) {
			int index = 2;

			int i = 0;
			for (Location counter : waypoints) {
				arr[index + i] = counter;
				i++;
			}
		}
		task.execute(arr);
	}

	private void drawMarker(Location location, String message, boolean zoom) {
		LatLng currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());
		if (message.toLowerCase(Locale.UK).contains("palat")) {
			map.addMarker(new MarkerOptions()
					.position(currentPosition)
					// .snippet("")
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED))
					.title(message));
		} else if (message.toLowerCase().contains("parc")) {
			map.addMarker(new MarkerOptions()
					.position(currentPosition)
					// .snippet("Lat:" + location.getLatitude() + "Lng:" +
					// location.getLongitude())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
					.title(message));
		} else if (message.toLowerCase().contains("biserica")
				|| message.toLowerCase().contains("manastire")
				|| message.toLowerCase().contains("catedrala")) {
			map.addMarker(new MarkerOptions()
					.position(currentPosition)
					// .snippet("Lat:" + location.getLatitude() + "Lng:" +
					// location.getLongitude())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
					.title(message));
		} else {
			map.addMarker(new MarkerOptions()
					.position(currentPosition)
					// .snippet("Lat:" + location.getLatitude() + "Lng:" +
					// location.getLongitude())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
					.title(message));
		}
		if (zoom == true) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(
					currentPosition, 15));
		}
	}

	private List<Location> getWaypoints(Location start, Location end) {
		List<Location> arr = new ArrayList<Location>();

		for (Location counter : invisibleLocations) {
			float distanceFrom = start.distanceTo(new Location(counter));
			float distanceTo = end.distanceTo(new Location(counter));

			Location midPoint = midPoint(start, end);
			float distanceMid = midPoint.distanceTo(new Location(counter));

			if (distanceFrom < maxDist) {
				arr.add(counter);
				Log.v("TAG/Distanta", String.valueOf(distanceFrom));
			} else if (distanceTo < maxDist) {
				arr.add(counter);
				Log.v("TAG/Distanta", String.valueOf(distanceTo));
			} else if (distanceMid < maxDist) {
				arr.add(counter);
				Log.v("TAG/Distanta", String.valueOf(distanceMid));
			}
		}

		return arr;
	}

	public static Location midPoint(Location start, Location end) {

		double lat1 = start.getLatitude();
		double lat2 = end.getLatitude();
		double lon1 = start.getLongitude();
		double lon2 = end.getLongitude();

		double lat3 = (lat1 + lat2) / 2;
		double lon3 = (lon1 + lon2) / 2;

		Location midPoint = new Location("Mid Point");
		midPoint.setLatitude(lat3);
		midPoint.setLongitude(lon3);

		return midPoint;
	}

	private class AsyncRouteDraw extends
			AsyncTask<Location, Void, PolylineOptions> {
		@Override
		protected void onPostExecute(PolylineOptions result) {
			if (linie != null) {
				linie.remove();
			}
			linie = map.addPolyline(result);
		}

		@Override
		protected PolylineOptions doInBackground(Location... arrayLists) {
			PolylineOptions response = new PolylineOptions().width(3).color(
					Color.BLUE);

			Location start = arrayLists[0];
			Location end = arrayLists[1];

			String waypoints = "";
			for (int i = 2; i < arrayLists.length; i++) {

				if (i < arrayLists.length) {
					waypoints += arrayLists[i].getLatitude() + ","
							+ arrayLists[i].getLongitude();
				}

				if (i < arrayLists.length - 1) {
					waypoints += "%7C";
				}
			}

			Log.v("TAG/Waypoints/String", waypoints);

			Directions md = new Directions();
			Document doc = md.getDocument(start, end, waypoints,
					Directions.MODE_DRIVING);
			ArrayList<LatLng> directionPoint = md.getDirection(doc);

			for (int i = 0; i < directionPoint.size(); i++) {
				response.add(directionPoint.get(i));
			}

			return response;
		}
	}

}
