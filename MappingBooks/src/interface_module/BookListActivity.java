package interface_module;

import interface_module.async_tasks.AddBookAsyncUpload;
import interface_module.async_tasks.BookListRequestAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.project.mappingbooks.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BookListActivity extends ListActivity {

	public static final Integer image = R.drawable.book;
	private LinearLayout progressLayout;
	private RelativeLayout noBooksLayout;
	List<RowItem> rowItems;
	private BookListViewAdapter adapter;
	private String sessionID;
	private final String TAG_STATUS = "status";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);
		rowItems = new ArrayList<RowItem>();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String sessionID = extras.getString("sessionID");
			this.setSessionID(sessionID);
			String username = extras.getString("username");
			if (username != null)
				setTitle(username);
			try {
				new BookListRequestAsync(this).execute(getSessionID()).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setProgressLayout((LinearLayout) findViewById(R.id.linlaHeaderProgress));
		setNoBooksLayout((RelativeLayout) findViewById(R.id.noBooksLayout));
		// this.progressLayout.setVisibility(View.VISIBLE);
		adapter = new BookListViewAdapter(this, R.layout.list_item, null);
		setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.add_book_menu_item:
			scanNow();
			return true;
		case R.id.logout_button: {
			Intent myIntent = new Intent(BookListActivity.this,
					LoginActivity.class);
			myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// clear back
																// stack
			startActivity(myIntent);
			finish();
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent bookViewerIntent = new Intent(getApplicationContext(),
				BookViewerActivity.class);
		startActivity(bookViewerIntent);
	}

	public void scanNow() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode != 0) {
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);
			if (scanningResult != null) {
				AddBookAsyncUpload uploadBook = new AddBookAsyncUpload(this);
				uploadBook.execute(this.getSessionID(),
						scanningResult.getContents());
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"No scan data received!", Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public void handleUploadResponse(String response) {
		try {
			JSONObject responseObject = new JSONObject(response);
			String status = responseObject.getString(TAG_STATUS);
			if (status.equalsIgnoreCase("ok")) {
				new BookListRequestAsync(this).execute(getSessionID()).get();
			} else if (status.equalsIgnoreCase("warning")) {
				Toast.makeText(getApplicationContext(),
						"Book already uploaded.", Toast.LENGTH_SHORT).show();
			} else {
				// String errorCode = responseObject.getString(TAG_ERROR);
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setTitle("Upload failed").setMessage("Try Again?");
				alertBuilder.setPositiveButton("YES",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).setNegativeButton("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog dialog = alertBuilder.create();
				dialog.show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	@Override
	public void onBackPressed() {

	}

	public LinearLayout getProgressLayout() {
		return progressLayout;
	}

	public void setProgressLayout(LinearLayout progressLayout) {
		this.progressLayout = progressLayout;
	}

	public BookListViewAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(BookListViewAdapter adapter) {
		this.adapter = adapter;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public RelativeLayout getNoBooksLayout() {
		return noBooksLayout;
	}

	public void setNoBooksLayout(RelativeLayout noBooksLayout) {
		this.noBooksLayout = noBooksLayout;
	}

}
