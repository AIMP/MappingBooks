package interface_module;

import interface_module.slinding_menu.NavDrawerItem;
import interface_module.slinding_menu.NavDrawerListAdapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.project.mappingbooks.R;

@SuppressLint("NewApi")
public class BookViewerActivity extends Activity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;
	String popUpContents[];
	PopupWindow popupWindowDogs;   

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		TextView t = (TextView) findViewById(R.id.book_text);
		t.append("Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt! Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!Aici sunt!");
		t.setMovementMethod(new ScrollingMovementMethod());
		
		 mDrawerList.setAdapter(new MyAddapter(BookViewerActivity.this));
		 
		 List<String> dogsList = new ArrayList<String>();
	        dogsList.add("Samsung");
	        dogsList.add("Google");
	        dogsList.add("Yahoo");
	        dogsList.add("Microsoft");

	        // convert to simple array
	        popUpContents = new String[dogsList.size()];
	        dogsList.toArray(popUpContents);

	        /*
	         * initialize pop up window
	         */
	    popupWindowDogs = popupWindowDogs();
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We  will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));
		

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.app_name, // nav drawer open - description for accessibility
				R.string.app_name // nav drawer close - description for accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			
		}
	}
	
	
	 public PopupWindow popupWindowDogs() {

	        // initialize a pop up window type
	        PopupWindow popupWindow = new PopupWindow(this);

	        // the drop down list is a list view
	        ListView listViewDogs = new ListView(this);
	       
	        // set our adapter and pass our pop up window contents
	        listViewDogs.setAdapter(dogsAdapter(popUpContents));
	       
	        // set the item click listener
	        listViewDogs.setOnItemClickListener(new DogsDropdownOnItemClickListener());

	        // some other visual settings
	        popupWindow.setFocusable(true);
	        popupWindow.setWidth(250);
	        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
	       
	        // set the list view as pop up window content
	        popupWindow.setContentView(listViewDogs);

	        return popupWindow;
	    }
	 
	 
	 public class DogsDropdownOnItemClickListener implements OnItemClickListener {
		    
		    @Override
		    public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

		        // get the context and main activity to access variables
		        Context mContext = v.getContext();
		        BookViewerActivity mainActivity = ((BookViewerActivity) mContext);
		        
		        // add some animation when a list item was clicked
		        Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
		        fadeInAnimation.setDuration(10);
		        v.startAnimation(fadeInAnimation);
		        
		        // dismiss the pop up
		        mainActivity.popupWindowDogs.dismiss();
		        
		        // get the text and set it as the button text
		        
		        Toast.makeText(mContext, "Selected Positon is: " + arg2, 100).show();
		        
		        
		    }

		}
	 private ArrayAdapter<String> dogsAdapter(String dogsArray[]) {

	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dogsArray) {

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
			// display view for selected nav drawer item
			popupWindowDogs.showAsDropDown(view, -5, 0);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_previous:
		case R.id.action_next:
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	

		

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

}
