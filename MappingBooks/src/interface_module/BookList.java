package interface_module;

import java.util.ArrayList;
import java.util.List;

import com.project.mappingbooks.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ListView;

public class BookList extends Activity {

	 public static final String[] titles = new String[] { "East of Eden",
         "For Whom the Bell Tolls", "1984", "La Medeleni",
         "For Whom the Bell Tolls", "1984", "La Medeleni",
         "For Whom the Bell Tolls", "1984", "La Medeleni" };

 public static final String[] descriptions = new String[] {
         "writen by John Steinbeck",
         "writen by Ernest Hemingway",
         "writen by George Orwell",
         "writen by Ionel Teodoreanu",
         "writen by Ernest Hemingway",
         "writen by George Orwell",
         "writen by Ionel Teodoreanu",
         "writen by Ernest Hemingway",
         "writen by George Orwell",
         "writen by Ionel Teodoreanu"};

 public static final Integer image = R.drawable.book;

 	ListView listView;
 	List<RowItem> rowItems;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_list);
	
	
		rowItems = new ArrayList<RowItem>();
        for (int i = 0; i < titles.length; i++) {
            RowItem item = new RowItem(image, titles[i], descriptions[i]);
            rowItems.add(item);
        }
        
        listView = (ListView) findViewById(R.id.list);
        CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                R.layout.list_item, rowItems);
        listView.setAdapter(adapter);
       // listView.setOnItemClickListener(this);
 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.book_list, menu);
		return true;
	}

}
