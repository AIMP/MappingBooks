package geography_module;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.project.mappingbooks.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class DisplayMessageActivity extends FragmentActivity {
	private GoogleMap map;
	private LinkedList<LinkedList<LatLng>> polygons;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);

		Intent intent = getIntent();
		double[] latLng = intent.getDoubleArrayExtra("currentLocation");

		try {
			this.showMap(new LatLng(latLng[0], latLng[1]));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void getXMLPolygons() throws ParserConfigurationException,
			SAXException, IOException {
		this.polygons = new LinkedList<LinkedList<LatLng>>();

		InputStream ins = getResources().openRawResource(R.raw.localitati);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(ins);

		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("localitate");

		int length = nList.getLength();

		for (int i = 0; i < length; i++) {
			Node nNode = nList.item(i).getLastChild();
			LinkedList<LatLng> polygon = new LinkedList<LatLng>();

			NodeList coordList = nNode.getChildNodes();
			int len = coordList.getLength();

			for (int j = 0; j < len; j++) {
				LatLng latLng;

				Node latNode = coordList.item(j).getFirstChild();
				Node lngNode = coordList.item(j).getLastChild();

				latLng = new LatLng(
						Double.parseDouble(latNode.getTextContent()),
						Double.parseDouble(lngNode.getTextContent()));

				polygon.add(latLng);
			}

			this.polygons.add(polygon);
		}
	}

	private void drawPolygons(double minLat, double maxLat, double minLng,
			double maxLng) {
		int length = this.polygons.size();

		for (int i = 0; i < length; i++) {
			LinkedList<LatLng> polygon = this.polygons.get(i);
			LinkedList<LatLng> newPolygon = new LinkedList<LatLng>();

			for (LatLng latLng : polygon) {
				if (latLng.latitude <= maxLat && latLng.latitude >= minLat) {
					if (latLng.longitude <= maxLng
							&& latLng.longitude >= minLng) {
						newPolygon.add(latLng);
					}
				}
			}

			/*
			 * if(polygon.size() == newPolygon.size()){ PolygonOptions
			 * polygonOptions = new PolygonOptions()
			 * .addAll(newPolygon).strokeColor(Color.YELLOW)
			 * .fillColor(Color.TRANSPARENT) .strokeWidth(3);
			 * 
			 * @SuppressWarnings("unused") Polygon polyGon =
			 * this.map.addPolygon(polygonOptions); }else{
			 */
			PolylineOptions polylineOptions = new PolylineOptions()
					.addAll(newPolygon).color(Color.YELLOW).width(3);

			@SuppressWarnings("unused")
			Polyline polyLine = this.map.addPolyline(polylineOptions);
			// }
		}
	}

	private void updateMap(LatLng latLng) {

		VisibleRegion bounds = map.getProjection().getVisibleRegion();
		double minLat, maxLat, minLng, maxLng;

		LatLng farLeft = bounds.farLeft, farRight = bounds.farRight, nearLeft = bounds.nearLeft, nearRight = bounds.nearRight;

		minLat = Math.min(
				farLeft.latitude,
				Math.min(farRight.latitude,
						Math.min(nearLeft.latitude, nearRight.latitude)));

		maxLat = Math.max(
				farLeft.latitude,
				Math.max(farRight.latitude,
						Math.max(nearLeft.latitude, nearRight.latitude)));

		minLng = Math.min(
				farLeft.longitude,
				Math.min(farRight.longitude,
						Math.min(nearLeft.longitude, nearRight.longitude)));

		maxLng = Math.max(
				farLeft.longitude,
				Math.max(farRight.longitude,
						Math.max(nearLeft.longitude, nearRight.longitude)));

		this.drawPolygons(minLat, maxLat, minLng, maxLng);
	}

	private void showMap(final LatLng latLng)
			throws ParserConfigurationException, SAXException, IOException {

		this.getXMLPolygons();

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latLng).zoom(15).bearing(0).tilt(30).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		updateMap(latLng);

		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				map.clear();

				updateMap(latLng);
			}

		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
