package interface_module;

import com.project.mappingbooks.R;

import android.os.Bundle;
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
	}
	
	public void forgotPassword(View view) {
	//TODO: create a new intent for forgot password activity
	}
	
	public void login(View view) {
	//TODO:login flow	
	}
	
}
