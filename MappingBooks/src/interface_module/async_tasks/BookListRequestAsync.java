package interface_module.async_tasks;

import interface_module.BookListActivity;
import interface_module.NetworkManager;
import interface_module.RowItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.project.mappingbooks.R;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;

public class BookListRequestAsync extends
		AsyncTask<String, Void, ArrayList<RowItem>> {
	protected BookListActivity activity;

	public BookListRequestAsync(BookListActivity activity) {
		this.activity = activity;
	}

	@Override
	protected ArrayList<RowItem> doInBackground(String... params) {
		try {
			String postSessionID = "sessionID";
			String URL = "http://clientserver.aws.af.cm/client/getUserBooks";// https://ia_clientserver-c9-icaliman.c9.io/client/login
			HttpPost post = new HttpPost(URL);
			HttpClient client = NetworkManager.getNewHttpClient();// new
			// DefaultHttpClient();
			/*HttpConnectionParams
					.setConnectionTimeout(client.getParams(), 10000); // Timeout
																		// Limit*/
			HttpResponse response;
			JSONObject json = new JSONObject();
			json.put(postSessionID, params[0]);

			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(se);
			response = client.execute(post);

			if (response != null) {
				InputStream in = response.getEntity().getContent();
				String line;
				StringBuilder builder = new StringBuilder();
				// (1)
				BufferedReader bReader = new BufferedReader(
						new InputStreamReader(in));
				while ((line = bReader.readLine()) != null) {
					builder.append(line);
				}
				return parseBooksJson(builder.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	@Override
	protected void onPostExecute(ArrayList<RowItem> bookList) {
		if (bookList == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Error")
					.setMessage(
							"There was a problem on server with your list of books")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
			AlertDialog dialog = builder.create();
			dialog.show();
		} else {
			this.activity.getAdapter().setItems(bookList);
			this.activity.getProgressLayout().setVisibility(View.GONE);
			if (bookList.size() > 0)
				this.activity.getAdapter().notifyDataSetChanged();
			else 
				this.activity.getNoBooksLayout().setVisibility(View.VISIBLE);

		}
	}

	private ArrayList<RowItem> parseBooksJson(String json) {
		ArrayList<RowItem> books = new ArrayList<RowItem>();
		try {
			JSONObject rootObject = new JSONObject(json);
			String status = rootObject.getString("status");
			if (status.equalsIgnoreCase("ok")) {
				JSONArray bookListArray = rootObject.getJSONArray("books");
				for (int i = 0; i < bookListArray.length(); i++) {
					JSONObject bookObject = bookListArray.getJSONObject(i);
					String bookID = bookObject.getString("_id");
					String bookYear = bookObject.getString("bookYear");
					String bookTitle = bookObject.getString("bookTitle");
					String bookAuthor = bookObject.getString("bookAuthor");
					
					books.add(new RowItem(R.drawable.book,bookTitle,bookAuthor,bookYear,bookID));
				}
			} else {
				books = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return books;
	}
}
