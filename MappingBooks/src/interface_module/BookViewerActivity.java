package interface_module;

import interface_module.slinding_menu.NavDrawerItem;
import interface_module.slinding_menu.NavDrawerListAdapter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.mappingbooks.R;

@SuppressLint("NewApi")
public class BookViewerActivity extends FragmentActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mLeftListView;
	private View mRightView;
	private GoogleMap map;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

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
		setContentView(R.layout.activity_book_viewer);

		TextView t = (TextView) findViewById(R.id.book_text);
		t.append("Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt! Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!");
		t.setMovementMethod(new ScrollingMovementMethod());
		
		buildLeftSlidingMenu();
		buildRightSlidingMenu();

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

	}

	private void buildLeftSlidingMenu() {
		mLeftListView = (ListView) findViewById(R.id.list_slidermenu);
		mLeftListView.setAdapter(new DrawerAdapter(BookViewerActivity.this));
		mLeftListView.setOnItemClickListener(new SlideMenuClickListener());
		
		mTitle = getTitle();
        mDrawerTitle = "Menu";
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
		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

	}

	private void showMap(LatLng latLng) {

		map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		@SuppressWarnings("unused")
		Marker kiel = map.addMarker(new MarkerOptions().position(latLng)
				.title("Iasi").snippet("Iasi is cool")
		/*
		 * .icon(BitmapDescriptorFactory .fromResource(R.drawable.ic_launcher))
		 */);

		// map.moveCamera(CameraUpdateFactory.newLatLngZoom(IASI,10));

		// map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(latLng).zoom(10).bearing(90).tilt(30).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
			if(mDrawerLayout.isDrawerOpen(mRightView)) {
				mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mRightView);
				mDrawerLayout.closeDrawer(mRightView);
			} else if (mDrawerLayout.isDrawerOpen(mLeftListView)) {
				mDrawerLayout.closeDrawer(mLeftListView);
			} else {
				mDrawerLayout.openDrawer(mLeftListView);
			}
			
		case R.id.action_settings:
			return true;
		case R.id.action_previous:
			if (mDrawerLayout.isDrawerOpen(mLeftListView)) {
				mDrawerLayout.closeDrawer(mLeftListView);
			}
			if (mDrawerLayout.isDrawerOpen(mRightView)) {
				mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, mRightView);
				mDrawerLayout.closeDrawer(mRightView);
			} else {
				mDrawerLayout.openDrawer(mRightView);
				mDrawerLayout.setDrawerLockMode(
						DrawerLayout.LOCK_MODE_LOCKED_OPEN, mRightView);
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

	public GoogleMap getMap() {
		return map;
	}

	public void setMap(GoogleMap map) {
		this.map = map;
	}

}
