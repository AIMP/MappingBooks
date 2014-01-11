package geography_module;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.mappingbooks.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class GeographyEngine extends FragmentActivity {

	
	private GoogleMap map;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);

		Intent intent  = getIntent();
		double[] latLng = intent.getDoubleArrayExtra("EXTRA_MESSAGE");
		
		this.showMap(new LatLng(latLng[0],latLng[1]));
	}

	private void showMap(LatLng latLng){

		map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

		@SuppressWarnings("unused")
		Marker kiel = map.addMarker(new MarkerOptions()
		.position(latLng)
		.title("Iasi")
		.snippet("Iasi is cool")
		/*.icon(BitmapDescriptorFactory
        			.fromResource(R.drawable.ic_launcher))*/);

		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(IASI,10));

		//map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(latLng)      
		.zoom(10)                  
		.bearing(90)               
		.tilt(30)                   
		.build();                  

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
