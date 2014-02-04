package interface_module;

import geography_module.DisplayMessageActivity;
import interface_module.async_tasks.DownloadBookAsyncTask;
import interface_module.async_tasks.LocationChangesRequestAsyncTask;
import interface_module.async_tasks.ProximityRequestAsyncTask;
import interface_module.slinding_menu.NavDrawerItem;
import interface_module.slinding_menu.NavDrawerListAdapter;

import java.util.ArrayList;
import java.util.List;

import maps_module.MapManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.project.mappingbooks.R;

@SuppressLint("NewApi")
public class BookViewerActivity extends FragmentActivity implements
		LocationListener, OnMenuItemClickListener {
	private DrawerLayout mDrawerLayout;
	private ListView mLeftListView;
	private View mRightView;
	private GoogleMap map;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	private boolean isTablet = false;
	private boolean isPhone = false;
	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private TextView textview;
	private String currentSessionID;
	private String currentBookID;
	private ArrayList<Place> nearByPlaces;
	private SpannableStringBuilder stringBuilder;
	private View overlay;
	PopupMenu popup;
	int currentOptionChoosed;
	int fontSize = -1;
	private double proximity = -1;
	int mapsMenu = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		Bundle extras = intent.getBundleExtra("extra");
		if (extras != null) {
			this.setCurrentBookID(extras.getString("bookID"));
			this.setCurrentSessionID(extras.getString("sessionID"));
		}
		Utils.init(this);
		setContentView(R.layout.activity_book_viewer);
		stringBuilder = new SpannableStringBuilder();
		LocationManager lm = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		boolean gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean net = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		Log.v("TAG",
				"GPS:" + String.valueOf(gps) + " Net:" + String.valueOf(net));
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, this);
		textview = (TextView) findViewById(R.id.book_text);
		textview.setMovementMethod(new ScrollingMovementMethod());
		String deviceType = getResources().getString(R.string.device);
		buildLeftSlidingMenu();
		overlay = findViewById(R.id.overlay);
		if (deviceType.equalsIgnoreCase("Smartphone")) {
			isPhone = true;
			buildRightSlidingMenu();
		} else {
			isTablet = true;
			mRightView = findViewById(R.id.leftView);
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.setMyLocationEnabled(true);
		}
		if (map != null) {
			MapManager.getInstance().linkWith(lm, map, this);
		}
		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		new DownloadBookAsyncTask(this).execute(new String[] {
				this.getCurrentSessionID(), this.getCurrentBookID() });
		new ProximityRequestAsyncTask(this, true).execute(new String[] { this
				.getCurrentSessionID() });
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.metters500:
			new ProximityRequestAsyncTask(this, false).execute(new String[] {
					this.getCurrentSessionID(), String.valueOf(500) });
			popup.dismiss();
			return true;
		case R.id.metters1000:
			new ProximityRequestAsyncTask(this, false).execute(new String[] {
					this.getCurrentSessionID(), String.valueOf(1000) });
			popup.dismiss();
			return true;
		case R.id.metters2000:
			new ProximityRequestAsyncTask(this, false).execute(new String[] {
					this.getCurrentSessionID(), String.valueOf(2000) });
			popup.dismiss();
			return true;
		case R.id.metters5000:
			new ProximityRequestAsyncTask(this, false).execute(new String[] {
					this.getCurrentSessionID(), String.valueOf(5000) });
			popup.dismiss();
			return true;
		case R.id.metters10000:
			new ProximityRequestAsyncTask(this, false).execute(new String[] {
					this.getCurrentSessionID(), String.valueOf(10000) });
			popup.dismiss();
			return true;
		case R.id.metters100000:
			new ProximityRequestAsyncTask(this, false).execute(new String[] {
					this.getCurrentSessionID(), String.valueOf(100000) });
			popup.dismiss();
			return true;
		case R.id.small_font:
			fontSize = 1;
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
			item.setChecked(true);
			return true;
		case R.id.medium_font:
			fontSize = 2;
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			item.setChecked(true);
			return true;
		case R.id.large_font:
			fontSize = 3;
			textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
			item.setChecked(true);
			return true;
		case R.id.normal_view:
			mapsMenu = 1;
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
			item.setChecked(true);
			return true;
		case R.id.view_3d:
			mapsMenu = 2;

			Intent intent = new Intent(this, DisplayMessageActivity.class);
			double[] latLong = new double[2];
			latLong[0] = MapManager.getInstance().getLocation().getLatitude();
			latLong[1] = MapManager.getInstance().getLocation().getLongitude();
			intent.putExtra("currentLocation", latLong);
			startActivity(intent);
			return true;
		case R.id.indications:
			mapsMenu = 3;
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
			item.setChecked(true);
			return (true);
		default:
			return false;
		}
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
				.getResourceId(2, -1))); // Map Mode
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
		if (map != null)
			map.setMyLocationEnabled(true);

	}

	public void createPopupWindow(int position, View parentView) {
		currentOptionChoosed = position;
		if (position == 0) {
			popup = new PopupMenu(this, parentView);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.proximity_menu, popup.getMenu());
			popup.setOnMenuItemClickListener(this);
			if (proximity != -1) {
				if (proximity == 100) {
					popup.getMenu().getItem(0).setChecked(true);
				} else if (proximity == 500) {
					popup.getMenu().getItem(1).setChecked(true);
				} else if (proximity == 1000) {
					popup.getMenu().getItem(2).setChecked(true);
				} else if (proximity == 2000) {
					popup.getMenu().getItem(3).setChecked(true);
				} else if (proximity == 5000) {
					popup.getMenu().getItem(4).setChecked(true);
				} else if (proximity == 10000) {
					popup.getMenu().getItem(5).setChecked(true);
				} else if (proximity == 100000) {
					popup.getMenu().getItem(6).setChecked(true);
				} else {
					popup.getMenu().getItem(1).setChecked(true);
				}

			}
			popup.show();
		} else if (position == 1) {
			popup = new PopupMenu(this, parentView);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.font_menu, popup.getMenu());
			popup.setOnMenuItemClickListener(this);
			if (fontSize != -1) {
				popup.getMenu().getItem(fontSize - 1).setChecked(true);
			}
			popup.show();
		} else if (position == 2) {
			popup = new PopupMenu(this, parentView);
			MenuInflater inflater = popup.getMenuInflater();
			inflater.inflate(R.menu.maps_menu, popup.getMenu());
			popup.setOnMenuItemClickListener(this);
			if (mapsMenu != -1) {
				popup.getMenu().getItem(mapsMenu - 1).setChecked(true);
			}
			popup.show();

		}
	}

	/**
	 * Menu for previous/next pages
	 * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
			case 2:
				createPopupWindow(position, view);
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
	 * GPS Sensor Functions
	 */
	@Override
	public void onLocationChanged(Location l) {
		MapManager.getInstance().setLocation(l);
		new LocationChangesRequestAsyncTask(this).execute(new String[] {
				this.getCurrentSessionID(), this.getCurrentBookID(),
				String.valueOf(l.getLatitude()),
				String.valueOf(l.getLongitude()) });
		Log.v("TAG",
				String.format("Location changed: Long:%f, Lat:%f",
						(float) l.getLongitude(), (float) l.getLatitude()));
	}

	/**
	 * Handler for new places received
	 */
	public void handleNewPlaces(ArrayList<Place> newPlaces) {
		if (newPlaces == null)
			return;
		if (newPlaces.size() >= 0) {
			setNearByPlaces(newPlaces);
		}
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

	public String getCurrentSessionID() {
		return currentSessionID;
	}

	public void setCurrentSessionID(String currentSessionID) {
		this.currentSessionID = currentSessionID;
	}

	public String getCurrentBookID() {
		return currentBookID;
	}

	public void setCurrentBookID(String currentBookID) {
		this.currentBookID = currentBookID;
	}

	public void setProximity(double proximity) {
		this.proximity = proximity;
	}

	public ArrayList<Place> getNearByPlaces() {
		return nearByPlaces;
	}

	public void setNearByPlaces(ArrayList<Place> nearByPlaces) {
		this.nearByPlaces = nearByPlaces;
		if (map != null)
			MapManager.getInstance().refreshWithData(nearByPlaces);
	}

	public void testSpannable() {
		String match = "cod";
		String text = "Dispozitiile cod  prezentului cod reglementeaza raporturile patrimoniale si nepatrimoniale cod dintre persoane, ca  cod subiecte de drept civil. In sensul prezentului cod, prin uzante se intelege obiceiul (cutuma) si uzurile profesionale. ";

		String[] sentence = text.split(" ");
		for (String word : sentence) {
			if (word.equals(match)) {
				ArrayList<String> links = new ArrayList<String>();
				links.add("Hello1");
				links.add("Hello2");
				appendHighlightedText(word + " ", links);

			} else
				appendNormalText(word + " ");
		}

		textview.setMovementMethod(LinkMovementMethod.getInstance());

		textview.setText(stringBuilder, BufferType.SPANNABLE);
	}

	private void appendHighlightedText(final String text,
			final ArrayList<String> links) {
		int startPosition = stringBuilder.length();
		stringBuilder.append(text);
		stringBuilder.setSpan(new ForegroundColorSpan(Color.BLUE),
				startPosition, startPosition + text.length(), 0);
		ClickableSpan clickfor = new ClickableSpan() {

			@Override
			public void onClick(View clickfor) {
				showDialogAlert(text, clickfor, links);
			}
		};

		stringBuilder.setSpan(clickfor, startPosition,
				startPosition + text.length(), 0);

	}

	private void appendNormalText(String text) {
		stringBuilder.append(text);
	}

	public void showDialogAlert(String text, View btn, ArrayList<String> links) {
		Builder builder = new AlertDialog.Builder(this);
		if(links == null) {
			links = new ArrayList<String>();
			links.add("Link1");
			links.add("Link2");
		}
		
		builder.setTitle(text);
		int i = 0;
		String[] dialogLinks = new String[links.size()];
		for (String link : links) {
			dialogLinks[i++] = link;
		}
		builder.setItems(dialogLinks, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.show();
		WindowManager.LayoutParams lp = alertDialog.getWindow().getAttributes();
		lp.dimAmount = 0.0F;
		alertDialog.getWindow().setAttributes(lp);
		alertDialog.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
	}

	public void showDownloadingOverlay() {
		overlay.setVisibility(View.VISIBLE);
		((TextView) findViewById(R.id.processTextView))
				.setText("Downloading...");
	}

	public void showProcessingOverlay() {
		((TextView) findViewById(R.id.processTextView))
				.setText("Processing...");
	}

	public void removeOverlay() {
		overlay.setVisibility(View.GONE);
	}

	public View getOverlay() {
		return overlay;
	}

	public void setOverlay(View overlay) {
		this.overlay = overlay;
	}

	public void showBook(List<Words> words) {
		for (Words word : words) {
			if (word.isEntity) {
				this.appendHighlightedText(word.word + " ", null);
			} else {
				this.appendNormalText(word.word + " ");
			}
		}
		textview.setMovementMethod(LinkMovementMethod.getInstance());
		textview.setText(stringBuilder, BufferType.SPANNABLE);
	}

	public void showSegments(List<Segment> words) {

	}
}
