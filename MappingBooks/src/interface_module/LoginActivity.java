package interface_module;

import java.io.InputStream;
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
import com.project.mappingbooks.R;
import android.os.Bundle;
import android.os.Looper;
import android.app.Activity;
import android.view.Menu;
import android.view.View;


public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void createAccount(View view) {
	//TODO: create a new intent for register activity
		if(view.getId() == R.id.register_button){
			sendJson(new String[] {"user1","andreas.chelsau@facebook.com","pwd"});
		}
	}
	
	public void forgotPassword(View view) {
	//TODO: create a new intent for forgot password activity
	}
	
	public void login(View view) {
	//TODO:login flow	
		if(view.getId()==R.id.login_button)
		{
			sendJson(new String[] {"user1","pwd"});
			
		}
	}	

	public HttpClient getNewHttpClient() {
	   try {
	       KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	       trustStore.load(null, null);
	
	       SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	       sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	
	       HttpParams params = new BasicHttpParams();
	       HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	       HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
	
	       SchemeRegistry registry = new SchemeRegistry();
	       registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	       registry.register(new Scheme("https", sf, 443));
	
	       ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
	
	       return new DefaultHttpClient(ccm, params);
	   } catch (Exception e) {
	       return new DefaultHttpClient();
	   }
}

	
	protected void sendJson(final String[] params) {
       
        Thread t = new Thread() {

            public void run() {
            	String URL;
              

                try {
                	String[] type;
                    if(params.length == 2){
                    	type = new String[2];
                    	type[0] = "email"; type[1] = "password";
                    	URL = "https://107.23.123.140/client/login";//https://ia_clientserver-c9-icaliman.c9.io/client/login
                    }
                    else{
                    	type = new String[3];
                    	type[0] = "username"; type[1] = "email"; type[2] = "password";
                    	URL = "https://107.23.123.140/client/register";//https://ia_clientserver-c9-icaliman.c9.io/client/register
                    }
                    
                    
                    Looper.prepare();
                    HttpPost post = new HttpPost(URL);
                    HttpClient client = getNewHttpClient();//new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                    HttpResponse response;
                    JSONObject json = new JSONObject();
                    
                    
                    
                    for(int i = 0; i < params.length; i++)
                    	json.put(type[i], params[i]);
                    
                  //  json.put("password", pwd);
                    StringEntity se = new StringEntity( json.toString());  
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    if(response!=null){
                        InputStream in = response.getEntity().getContent();
                        String str = (String)in.toString();
                        System.out.println(str);
                    }

                } catch(Exception e) {
                    e.printStackTrace();
  
                }

                Looper.loop();
            }
        };

        t.start();      
    }
	
}
