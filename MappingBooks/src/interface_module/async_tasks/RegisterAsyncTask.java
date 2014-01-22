package interface_module.async_tasks;

import interface_module.NetworkManager;
import interface_module.RegisterActivity;

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
import android.view.View;

public class RegisterAsyncTask extends AsyncTask<String, Void, String> {
	protected RegisterActivity activity;

	public RegisterAsyncTask(RegisterActivity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		this.activity.getProgressBar().setVisibility(View.VISIBLE);
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String[] type = null;
			String URL = null;
			if (params.length == 3) {
				type = new String[3]; // register
				type[0] = "username";
				type[1] = "email";
				type[2] = "password";
				URL = "http://192.168.0.103:3000/client/register";// https://ia_clientserver-c9-icaliman.c9.io/client/register
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
		this.activity.getProgressBar().setVisibility(View.INVISIBLE);
		if (result != null)
			this.activity.handleResponse(result);
	}
}
