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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import augm_reality_module.GMapV2Direction;

public class MapManager {
	private static MapManager instance = null;
	protected LocationManager locationManager;
	protected Criteria criteria;
	private Location location;
	private String provider;
	private HashMap<String, String> markers;
	private List<Location> invisibleLocations;
	private GoogleMap map;
	private Location drawnEnd = null;
	private Polyline linie = null;
	private float maxDist = 5000;
	public int mapMode = 0;
	private BookViewerActivity linkedActivity;

	protected MapManager() {

	}

	public Location getLocation() {
		return this.location;
	}

	public static MapManager getInstance() {
		if (instance == null) {
			instance = new MapManager();
		}
		return instance;
	}

	public void setMapModeAndActivity(int mapMode, BookViewerActivity activivity) {
		this.mapMode = mapMode;
		this.linkedActivity = activivity;
	}

	public void setLocation(Location l) {
		this.location = l;
	}

	public void linkWith(LocationManager lm, GoogleMap map,
			BookViewerActivity activity) {
		this.locationManager = lm;
		this.map = map;
		this.map.setMyLocationEnabled(true);
		criteria = new Criteria();
		provider = lm.getBestProvider(criteria, true);
		location = lm.getLastKnownLocation(provider);
		markers = new HashMap<String, String>();
		invisibleLocations = new ArrayList<Location>();
		if (location != null) {
			new LocationChangesRequestAsyncTask(activity).execute(new String[] {
					activity.getCurrentSessionID(),
					activity.getCurrentBookID(),
					String.valueOf(location.getLatitude()),
					String.valueOf(location.getLongitude()) });
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					location.getLatitude(), location.getLongitude()), 14));

		}
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

		Set set = markers.entrySet();
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
			String directions = "<html><body>";
			GMapV2Direction mvd = new GMapV2Direction();
			directions += mvd.getInstructions(doc);
			directions = directions + "</body></html>";
			if (mapMode == 2) {
				showIndications(directions);
			}
			for (int i = 0; i < directionPoint.size(); i++) {
				response.add(directionPoint.get(i));
			}

			return response;
		}
	}

	private void showIndications(final String directions) {
		this.linkedActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(MapManager.getInstance().linkedActivity);
				alert.setTitle("Indications");

				WebView wv = new WebView(MapManager.getInstance().linkedActivity);
				wv.loadData(directions, "text/html; charset=UTF-8", null);

				alert.setView(wv);
				alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
				alert.show();
			}
		});
		
	}
	/*
	 * private LinkedList<LinkedList<LatLng>> getXMLPolygons() throws
	 * ParserConfigurationException, SAXException, IOException{
	 * LinkedList<LinkedList<LatLng>> polygons = new
	 * LinkedList<LinkedList<LatLng>>();
	 * 
	 * //eroare la linia de cod de mai jos
	 * 
	 * InputStream is = getResources().openRawResource(R.raw.localitati);
	 * 
	 * DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	 * DocumentBuilder dBuilder = dbFactory.newDocumentBuilder(); Document doc =
	 * dBuilder.parse(is);
	 * 
	 * doc.getDocumentElement().normalize();
	 * 
	 * NodeList nList = doc.getElementsByTagName("localitate");
	 * 
	 * int length = nList.getLength();
	 * 
	 * for(int i=0;i<length;i++){ Node nNode = nList.item(i).getLastChild();
	 * LinkedList<LatLng> polygon = new LinkedList<LatLng>();
	 * 
	 * NodeList coordList = nNode.getChildNodes(); int len =
	 * coordList.getLength();
	 * 
	 * for(int j=0;j<len;j++){ LatLng latLng;
	 * 
	 * Node latNode = coordList.item(j).getFirstChild(); Node lngNode =
	 * coordList.item(j).getLastChild();
	 * 
	 * latLng = new
	 * LatLng(Double.parseDouble(latNode.getTextContent()),Double.parseDouble
	 * (lngNode.getTextContent()));
	 * 
	 * polygon.add(latLng); }
	 * 
	 * polygons.add(polygon); }
	 * 
	 * return polygons; }
	 * 
	 * private void drawPolygons(LinkedList<LinkedList<LatLng>> polygons){
	 * 
	 * int length = polygons.size();
	 * 
	 * for(int i=0;i<5;i++){ LinkedList<LatLng> polygon = polygons.get(i);
	 * 
	 * PolygonOptions polygonOptions = new PolygonOptions()
	 * .addAll(polygon).strokeColor(Color.YELLOW) .fillColor(Color.LTGRAY)
	 * .strokeWidth(3);
	 * 
	 * Polygon polyGon = this.map.addPolygon(polygonOptions); } }
	 * 
	 * private void showMap(LatLng latLng) throws ParserConfigurationException,
	 * SAXException, IOException {
	 * 
	 * LinkedList<LinkedList<LatLng>> polygons = new
	 * LinkedList<LinkedList<LatLng>>();
	 * 
	 * polygons = this.getXMLPolygons(); this.drawPolygons(polygons);
	 * 
	 * @SuppressWarnings("unused") Marker kiel = map.addMarker(new
	 * MarkerOptions().position(latLng) .title("Iasi").snippet("Iasi is cool")
	 * 
	 * //.icon(BitmapDescriptorFactory .fromResource(R.drawable.ic_launcher)) );
	 * 
	 * // map.moveCamera(CameraUpdateFactory.newLatLngZoom(IASI,10));
	 * 
	 * // map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
	 * 
	 * CameraPosition cameraPosition = new CameraPosition.Builder()
	 * .target(latLng).zoom(10).bearing(0).tilt(30).build();
	 * 
	 * map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	 * }
	 */

}
