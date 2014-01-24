package interface_module;

import interface_module.slinding_menu.NavDrawerItem;
import interface_module.slinding_menu.NavDrawerListAdapter;

import java.util.ArrayList;
import java.util.List;

import maps_module.Directions;
import maps_module.Entities;

import org.w3c.dom.Document;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.project.mappingbooks.R;

@SuppressLint("NewApi")
public class BookViewerActivity extends FragmentActivity implements
		LocationListener {
	private DrawerLayout mDrawerLayout;
	private ListView mLeftListView;
	private View mRightView;
	private GoogleMap map_module6;
	private GoogleMap map_module7;
	private Location location;
	private String provider;
	private Criteria criteria;
	private Polyline linie = null;
	private Location drawnEnd = null;
	private List<Location> markers;
	private List<Location> invisibleLocations;
	private float maxDist = 5000;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private boolean isTablet = false;
	private boolean isPhone = false;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	String popUpContents[];
	PopupWindow popupWindow;
	int currentOptionChoosed;
	int fontSize;
	int proximity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.init(this);
		setContentView(R.layout.activity_book_viewer);
		LocationManager lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		Log.v("TAG",
				"GPS:" + String.valueOf(gps) + " Net:" + String.valueOf(net));
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
		TextView t = (TextView) findViewById(R.id.book_text);
		t.append("Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt! Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!");
		t.setMovementMethod(new ScrollingMovementMethod());
		String deviceType = getResources().getString(R.string.device);
		buildLeftSlidingMenu();
		if (deviceType.equalsIgnoreCase("Smartphone")) {
			isPhone = true;
			buildRightSlidingMenu();
		} else {
			isTablet = true;
			mRightView = findViewById(R.id.leftView);
			map_module6 = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map_module6)).getMap();
			map_module7 = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map_module7)).getMap();
		}

		criteria = new Criteria();
		provider = lm.getBestProvider(criteria, true);
		location = lm.getLastKnownLocation(provider);

		Entities entities = new Entities();
		markers = entities.getLocations();
		invisibleLocations = entities.getInvisibleLocations();

		addMarkersToMap();

		map_module7
				.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

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
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

	}

	private void buildLeftSlidingMenu() {
		mLeftListView = (ListView) findViewById(R.id.list_slidermenu);
		mLeftListView.setAdapter(new DrawerAdapter(BookViewerActivity.this));
		mLeftListView.setOnItemClickListener(new SlideMenuClickListener());

		mTitle = getTitle();
		setmDrawerTitle("Menu");
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);// load
																				// slide
																				// menu
																				// items
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);// nav drawer icons
															// from resources
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons
				.getResourceId(0, -1))); // Proximity
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons
				.getResourceId(1, -1))); // Font
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons
				.getResourceId(2, -1))); // Save
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons
				.getResourceId(3, -1))); // Exit
		navMenuIcons.recycle();// Recycle the typed array
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);// setting the nav drawer list adapter
		mLeftListView.setAdapter(adapter);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.app_name, R.string.app_name) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(getTitle());
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
				mDrawerToggle.syncState();
			}

			public void onDrawerOpened(View view) {
				getActionBar().setTitle(getString(R.string.app_name));
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
				mDrawerToggle.syncState();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private void buildRightSlidingMenu() {
		mRightView = findViewById(R.id.leftView);
		map_module6 = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_module6)).getMap();
		map_module7 = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map_module7)).getMap();
		map_module7.setMyLocationEnabled(true);

	}

	private void showMap(LatLng latLng) {

		@SuppressWarnings("unused")
		Marker kiel = map_module6.addMarker(new MarkerOptions()
				.position(latLng).title("Iasi").snippet("Iasi is cool")
		/*
		 * .icon(BitmapDescriptorFactory .fromResource(R.drawable.ic_launcher))
		 */);

		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(IASI,10));

		// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latLng).zoom(10).bearing(90).tilt(30).build();

		map_module6.animateCamera(CameraUpdateFactory
				.newCameraPosition(cameraPosition));
	}

	public void createPopupWindow(int position, View parentView) {
		popupWindow = new PopupWindow(this);
		currentOptionChoosed = position;
		ListView listView = new ListView(this);
		ArrayList<String> items = new ArrayList<String>();
		if (position == 0) {
			items.add("500");
			items.add("1000");
			items.add("1500");
			items.add("2000");
			items.add("5000");
			items.add("10000");
		} else if (position == 1) {
			items.add("Small");
			items.add("Medium");
			items.add("Large");
		}
		// convert to simple array
		popUpContents = new String[items.size()];
		items.toArray(popUpContents);
		// set our adapter and pass our pop up window contents
		listView.setAdapter(popupAdapter(popUpContents));
		// set the item click listener
		listView.setOnItemClickListener(new DropdownOnItemClickListener());
		// some other visual settings
		popupWindow.setFocusable(true);
		popupWindow.setWidth(parentView.getWidth());
		popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		// set the list view as pop up window content
		popupWindow.setContentView(listView);
	}

	public class DropdownOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
			switch (currentOptionChoosed) {
			case 0:
				proximity = Integer.parseInt(popUpContents[arg2]);
				break;
			case 1:
				fontSize = Integer.parseInt(popUpContents[arg2]);
				break;
			default:
				break;
			}
			// get the context and main activity to access variables
			Context mContext = v.getContext();
			BookViewerActivity mainActivity = ((BookViewerActivity) mContext);

			// add some animation when a list item was clicked
			Animation fadeInAnimation = AnimationUtils.loadAnimation(
					v.getContext(), android.R.anim.fade_in);
			fadeInAnimation.setDuration(10);
			v.startAnimation(fadeInAnimation);

			// dismiss the pop up
			mainActivity.popupWindow.dismiss();

		}

	}

	private ArrayAdapter<String> popupAdapter(String dogsArray[]) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dogsArray) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				// setting the ID and text for every items in the list

				String text = getItem(position);

				// visual settings for the list item
				TextView listItem = new TextView(BookViewerActivity.this);

				listItem.setText(text);
				listItem.setTag(position);
				listItem.setTextSize(22);
				listItem.setPadding(10, 10, 10, 10);
				listItem.setTextColor(Color.WHITE);

				return listItem;
			}
		};

		return adapter;
	}

	/**
	 * Menu for previous/next pages
	 * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
			case 1:
				createPopupWindow(position, view);
				popupWindow.showAsDropDown(view, -5, 0);
				break;
			case 2:// TODO: asynctask for saving preferences
				break;
			case 3:
				finish();
			default:
				break;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// Handle action bar actions click
		switch (item.getItemId()) {
		case android.R.id.home:
			if (isPhone) {
				if (mDrawerLayout.isDrawerOpen(mRightView)) {
					mDrawerLayout.setDrawerLockMode(
							DrawerLayout.LOCK_MODE_UNLOCKED, mRightView);
					mDrawerLayout.closeDrawer(mRightView);
				}
			}
			if (mDrawerLayout.isDrawerOpen(mLeftListView)) {
				mDrawerLayout.closeDrawer(mLeftListView);
			} else {
				mDrawerLayout.openDrawer(mLeftListView);
			}

			return true;
		case R.id.action_settings:
			return true;
		case R.id.action_previous:
			if (mDrawerLayout.isDrawerOpen(mLeftListView)) {
				mDrawerLayout.closeDrawer(mLeftListView);
			}
			if (isPhone) {
				if (mDrawerLayout.isDrawerOpen(mRightView)) {
					mDrawerLayout.setDrawerLockMode(
							DrawerLayout.LOCK_MODE_UNLOCKED, mRightView);
					mDrawerLayout.closeDrawer(mRightView);
				} else {
					mDrawerLayout.openDrawer(mRightView);
					mDrawerLayout.setDrawerLockMode(
							DrawerLayout.LOCK_MODE_LOCKED_OPEN, mRightView);
				}
			}
			return true;
		case R.id.action_next:
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public boolean isTablet() {
		return isTablet;
	}

	public void setTablet(boolean isTablet) {
		this.isTablet = isTablet;
	}

	public boolean isPhone() {
		return isPhone;
	}

	public void setPhone(boolean isPhone) {
		this.isPhone = isPhone;
	}

	public CharSequence getmDrawerTitle() {
		return mDrawerTitle;
	}

	public void setmDrawerTitle(CharSequence mDrawerTitle) {
		this.mDrawerTitle = mDrawerTitle;
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
			linie = map_module7.addPolyline(result);
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
		map_module7.addMarker(new MarkerOptions()
				.position(currentPosition)
				.snippet(
						"Lat:" + location.getLatitude() + "Lng:"
								+ location.getLongitude())
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.title(message));
		if (zoom == true) {
			map_module7.animateCamera(CameraUpdateFactory.newLatLngZoom(
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

	/*
	 * GPS Sensor Functions
	 */
	@Override
	public void onLocationChanged(Location l) {
		location = l;
		Log.v("TAG",
				String.format("Location changed: Long:%f, Lat:%f",
						(float) l.getLongitude(), (float) l.getLatitude()));
	}

	@Override
	public void onProviderDisabled(String txt) {
		Log.v("TAG", String.format("Provider disabled: %s", txt));
	}

	@Override
	public void onProviderEnabled(String txt) {
		Log.v("TAG", String.format("Provider enabled: %s", txt));
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		Log.v("TAG", "Status Changed");
	}

}
