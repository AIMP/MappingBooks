package interface_module.async_tasks;

import interface_module.BookViewerActivity;
import interface_module.NetworkManager;
import interface_module.Place;
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

import android.os.AsyncTask;

public class LocationChangesRequestAsyncTask extends
		AsyncTask<String, Void, ArrayList<Place>> {
	private BookViewerActivity linkedActivity;

	public LocationChangesRequestAsyncTask(BookViewerActivity activity) {
		this.linkedActivity = activity;
	}

	@Override
	protected ArrayList<Place> doInBackground(String... params) {
		try {
			String[] type = null;
			String URL = null;
			if (params.length == 4) { // proximity
				type = new String[4];
				type[0] = "sessionID";
				type[1] = "bookID";
				type[2] = "lat";
				type[3] = "long";
				URL = "http://clientserver.aws.af.cm/client/getProximityLocations";
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
				return this.parsePlacesJson(builder.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	@Override
	protected void onPostExecute(ArrayList<Place> newPlaces) {
		if(this.linkedActivity != null) {
			this.linkedActivity.handleNewPlaces(newPlaces);
		}
	}

	private ArrayList<Place> parsePlacesJson(String json) {
		ArrayList<Place> places = new ArrayList<Place>();
		try {
			JSONObject rootObject = new JSONObject(json);
			String status = rootObject.getString("status");
			if (status.equalsIgnoreCase("ok")) {
				JSONArray bookListArray = rootObject.getJSONArray("locations");
				for (int i = 0; i < bookListArray.length(); i++) {
					JSONObject placeObject = bookListArray.getJSONObject(i);
					String name = placeObject.getString("name");
					String ref = placeObject.getString("ref");
					String subtype = placeObject.getString("subtype");
					JSONArray geoObject = placeObject.getJSONArray("geo");
					String latString = geoObject.get(0).toString();
					String longString = geoObject.get(1).toString();
					Place newPlace = new Place(name, subtype, ref,
							Float.parseFloat(latString),
							Float.parseFloat(longString));
					places.add(newPlace);
				}
			} else {
				places = null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return places;
	}
}
