package interface_module;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import android.os.Looper;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.project.mappingbooks.R;

public class NetworkManager {
	public static ConnectivityManager connectivityManager;

	public static void sendJson(final String[] params) {

		Thread t = new Thread() {

			public void run() {
				String URL;

				try {
					String[] type;
					if (params.length == 2) { // login
						type = new String[2];
						type[0] = "email";
						type[1] = "password";
						URL = "https://107.23.123.140/client/login";// https://ia_clientserver-c9-icaliman.c9.io/client/login
					} else {
						type = new String[3]; // register
						type[0] = "username";
						type[1] = "email";
						type[2] = "password";
						URL = "https://107.23.123.140/client/register";// https://ia_clientserver-c9-icaliman.c9.io/client/register
					}

					Looper.prepare();
					HttpPost post = new HttpPost(URL);
					HttpClient client = getNewHttpClient();// new
															// DefaultHttpClient();
					/*HttpConnectionParams.setConnectionTimeout(
							client.getParams(), 10000); // Timeout Limit*/
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
						Log.v("Server Response: ", builder.toString());
					}

				} catch (Exception e) {
					e.printStackTrace();

				}

				Looper.loop();
			}
		};

		t.start();
	}

	public static HttpClient getNewHttpClient() {
		/*
		 * TODO: Make sure this conditional statement is appropriate.
		 */
		if (!isConnected()) {
			Utils.toast(R.string.no_connection);
			return null;
		}

		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, 10000);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static class Receiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean connected = NetworkManager.isConnected();
			Utils.d(connected ? "Connected" : "DISCONNECTED");
			if (!connected) Utils.toast(R.string.no_connection);
		}
	}

	public static boolean isConnected() {
		NetworkInfo netInfo = getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting();
	}

	public static NetworkInfo getActiveNetworkInfo() {
		return connectivityManager.getActiveNetworkInfo();
	}

	public static void init(Context context) {
		String service = Context.CONNECTIVITY_SERVICE;
		connectivityManager = (ConnectivityManager) context.getSystemService(service);
	}
}
