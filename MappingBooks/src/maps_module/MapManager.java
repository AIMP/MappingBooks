package maps_module;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.project.mappingbooks.R;

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
	private List<Location> markers;
	private List<Location> invisibleLocations;
	private GoogleMap map;
	private Location drawnEnd = null;
	private Polyline linie = null;
	private float maxDist = 5000;

	protected MapManager() {
		criteria = new Criteria();
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

	public void linkWith(LocationManager lm, GoogleMap map) {
		this.locationManager = lm;
		provider = lm.getBestProvider(criteria, true);
		location = this.locationManager.getLastKnownLocation(provider);
		this.map = map;
	}

	public void setup() {
		if (location != null) {
			Entities entities = new Entities();
			markers = entities.getLocations();
			invisibleLocations = entities.getInvisibleLocations();

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
						Location[] arr = getWaypoints(start, end);
						drawRoute(start, end, arr);
					}

					return true;
				}

			});
		}
	}

	/*
	 * Methods for the map from module 6
	 */

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
					waypoints += "|";
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

	private void addMarkersToMap() {
		map.clear();
		for (Location key : markers) {
			drawMarker(key, "Locatiune", true);
			Log.v("TAG/Marker", key.getLatitude() + ", " + key.getLongitude());
		}
	}

	private void drawRoute(final Location start, final Location end,
			final Location[] waypoints) {
		AsyncRouteDraw task = new AsyncRouteDraw();
		Location[] arr;
		if (waypoints != null) {
			arr = new Location[2 + waypoints.length];
		} else {
			arr = new Location[2];
		}

		arr[0] = start;
		arr[1] = end;

		Log.v("TAG/Start", arr[0].toString());
		Log.v("TAG/End", arr[1].toString());

		if (waypoints != null) {
			int index = 2;
			for (int i = 0; i < waypoints.length; i++) {
				arr[index + i] = waypoints[i];
				Log.v("TAG/Waypoints", arr[index + i].toString());
			}
		}
		task.execute(arr);
	}

	private void drawMarker(Location location, String message, boolean zoom) {
		LatLng currentPosition = new LatLng(location.getLatitude(),
				location.getLongitude());
		map.addMarker(new MarkerOptions()
		.position(currentPosition)
		.snippet(
				"Lat:" + location.getLatitude() + "Lng:"
						+ location.getLongitude())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
								.title(message));
		if (zoom == true) {
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(
					currentPosition, 15));
		}
	}

	// private void drawMarkerToAdress(String vAdress){
	//
	// Geocoder geocoder = new Geocoder(this, Locale.getDefault());
	// try {
	// List<Address> locations = geocoder.getFromLocationName(vAdress, 5);
	// if (locations == null) {
	// return;
	// }
	// Address location = locations.get(0);
	// double latitude = location.getLatitude();
	// double longitude = location.getLongitude();
	// googleMap.addMarker(new MarkerOptions()
	// .position(new LatLng(latitude, longitude))
	// .title(vAdress));
	// } catch(Exception e){
	//
	// }
	// }

	private Location[] getWaypoints(Location start, Location end) {
		Location[] arr = new Location[1];
		int index = 0;
		for (Location counter : invisibleLocations) {
			float distanceFrom = start.distanceTo(new Location(counter));
			float distanceTo = end.distanceTo(new Location(counter));

			Location midPoint = midPoint(start, end);
			float distanceMid = midPoint.distanceTo(new Location(counter));

			if (distanceFrom < maxDist) {
				arr[index] = counter;
				Log.v("TAG/Distanta", String.valueOf(distanceFrom));
			} else if (distanceTo < maxDist) {
				arr[index] = counter;
				Log.v("TAG/Distanta", String.valueOf(distanceTo));
			} else if (distanceMid < maxDist) {
				arr[index] = counter;
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

	private LinkedList<LinkedList<LatLng>> getXMLPolygons() throws ParserConfigurationException, SAXException, IOException{
		LinkedList<LinkedList<LatLng>> polygons = new LinkedList<LinkedList<LatLng>>();

		//eroare la linia de cod de mai jos
		
		InputStream is = getResources().openRawResource(R.raw.localitati);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);

		doc.getDocumentElement().normalize();

		//System.out.println("Root element :" + doc.getDocumentElement().getNodeName() + "\n");

		NodeList nList = doc.getElementsByTagName("localitate");

		//System.out.println("<---------------------------->");

		int length = nList.getLength();

		for(int i=0;i<length;i++){
			Node nNode = nList.item(i).getLastChild();
			LinkedList<LatLng> polygon = new LinkedList<LatLng>();

			//Node fNode = nList.item(i).getFirstChild();

			//System.out.println("Nume Element :" + fNode.getTextContent());

			NodeList coordList = nNode.getChildNodes();
			int len = coordList.getLength();

			for(int j=0;j<len;j++){
				LatLng latLng;

				Node latNode = coordList.item(j).getFirstChild();
				Node lngNode = coordList.item(j).getLastChild();

				//System.out.println("\nLatitudine Element :" + latNode.getNodeName() + " " + latNode.getTextContent());
				//System.out.println("Longitudine Element :" + lngNode.getNodeName() + " " + lngNode.getTextContent() + "\n");

				latLng = new LatLng(Double.parseDouble(latNode.getTextContent()),Double.parseDouble(lngNode.getTextContent()));

				polygon.add(latLng);
			}

			//System.out.println(i + "-->)Polygon Length :" + polygon.size());

			polygons.add(polygon);
		}

		return polygons;
	}

	private void drawPolygons(LinkedList<LinkedList<LatLng>> polygons){

		int length = polygons.size();

		for(int i=0;i<5;i++){
			LinkedList<LatLng> polygon = polygons.get(i);

			PolygonOptions polygonOptions = new PolygonOptions()
			.addAll(polygon).strokeColor(Color.YELLOW)
			.fillColor(Color.LTGRAY)
			.strokeWidth(3);

			Polygon polyGon = this.map.addPolygon(polygonOptions);
		}
	}

	private void showMap(LatLng latLng) throws ParserConfigurationException, SAXException, IOException {

		LinkedList<LinkedList<LatLng>> polygons = new LinkedList<LinkedList<LatLng>>();

		polygons = this.getXMLPolygons();
		this.drawPolygons(polygons);

		@SuppressWarnings("unused")
		Marker kiel = map.addMarker(new MarkerOptions().position(latLng)
				.title("Iasi").snippet("Iasi is cool")
				/*
				 * .icon(BitmapDescriptorFactory .fromResource(R.drawable.ic_launcher))
				 */);

		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(IASI,10));

		// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(latLng).zoom(10).bearing(0).tilt(30).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

}
