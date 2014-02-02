package interface_module.async_tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import interface_module.BookViewerActivity;
import interface_module.NetworkManager;
import interface_module.Utils;
import android.os.AsyncTask;

public class ProximityRequestAsyncTask extends AsyncTask<String, Void, String> {
	BookViewerActivity linkedActivity;
	boolean getRequest;

	public ProximityRequestAsyncTask(BookViewerActivity activity,
			boolean getRequest) {
		this.linkedActivity = activity;
		this.getRequest = getRequest;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			String[] type = null;
			String URL = null;

			if (!getRequest) {
				URL = "http://clientserver.aws.af.cm/client/setPreferences";
				type = new String[2];
				type[0] = "sessionID";
				type[1] = "proximity";
			} else {
				URL = "http://clientserver.aws.af.cm/client/getPreferences";
				type = new String[1];
				type[0] = "sessionID";
			}

			HttpPost post = new HttpPost(URL);
			HttpClient client = NetworkManager.getNewHttpClient();
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
		if (result != null) {
			JSONObject rootObject;
			try {
				rootObject = new JSONObject(result);
				String status = rootObject.getString("status");
				if (status.equalsIgnoreCase("ok")) {
					if (getRequest) {
						String proximity = rootObject.getString("proximity");
						linkedActivity
								.setProximity(Double.parseDouble(proximity));
					} else {
						new ProximityRequestAsyncTask(this.linkedActivity,
								true)
								.execute(new String[] { this.linkedActivity
										.getCurrentSessionID() });
					}
				}
			} catch (JSONException e) {
				Utils.toast("Proximity error");
			}

		}
	}
}
