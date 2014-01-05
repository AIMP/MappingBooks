package interface_module;

import interface_module.async_tasks.BookListRequestAsync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.project.mappingbooks.R;

import android.os.Bundle;
import android.app.ListActivity;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BookListActivity extends ListActivity {

	public static final Integer image = R.drawable.book;
	private LinearLayout progressLayout;
	private RelativeLayout noBooksLayout;
	List<RowItem> rowItems;
	private BookListViewAdapter adapter;
	private String sessionID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);
		rowItems = new ArrayList<RowItem>();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String sessionID = extras.getString("sessionID");
			this.setSessionID(sessionID);
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