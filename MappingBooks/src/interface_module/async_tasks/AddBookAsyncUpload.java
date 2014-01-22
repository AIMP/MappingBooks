package interface_module.async_tasks;

import interface_module.BookListActivity;
import interface_module.NetworkManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.os.AsyncTask;

public class AddBookAsyncUpload extends AsyncTask<String, Void, String> {

	protected BookListActivity activity;

	public AddBookAsyncUpload(BookListActivity activity) {
		this.activity = activity;
	}
	
	@Override
	protected String doInBackground(String... params) {
		try {
			String[] type = null;
			String URL = null;
			if (params.length == 2) { // login
				type = new String[2];
				type[0] = "sessionID";
				type[1] = "qrcode";
				URL = "http://192.168.0.103:3000/client/addBook";// https://ia_clientserver-c9-icaliman.c9.io/client/addBook
			}
			HttpPost post = new HttpPost(URL);
			HttpClient client = NetworkManager.getNewHttpClient();// new
			// DefaultHttpClient();
			/*HttpConnectionParams
					.setConnectionTimeout(client.getParams(), 10000); // Timeout
																		// Limit*/
			HttpResponse response;
			JSONObject json = new JSONObject();

			for (int i = 0; i < params.length; i++)
				json.put(type[i], params[i]);

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
				return builder.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		if(this.activity!=null) {
			this.activity.handleUploadResponse(result);
		}
	}

}
