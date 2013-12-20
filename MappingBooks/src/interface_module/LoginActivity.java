package interface_module;

import java.util.concurrent.ExecutionException;

import interface_module.async_tasks.LoginAsyncTask;

import com.project.mappingbooks.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends Activity {
	protected EditText userNameEditText;
	protected EditText passwordEditText;
	protected ProgressBar progressBar;
	private int limit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		userNameEditText = (EditText) findViewById(R.id.input_username);
		passwordEditText = (EditText) findViewById(R.id.input_password);
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
		limit = 50;
		setLimit(userNameEditText);
		setLimit(passwordEditText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void createAccount(View view) {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
		if (view.getId() == R.id.register_button) {
			 Intent i = new Intent(this,RegisterActivity.class);
			 startActivity(i);
		}
	}

	public void forgotPassword(View view) {
		// TODO: create a new intent for forgot password activity
	}

	public void login(View view) {
		if (view.getId() == R.id.login_button) {
			String username = userNameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			Intent intent = new Intent(this,BookList.class);
			startActivity(intent);
			try {
				String response = new LoginAsyncTask(this).execute(new String[] { username, password }).get();
				Log.v("Response", response);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void setLimit(final EditText text) {
		text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (s.length() > limit) {
					new AlertDialog.Builder(LoginActivity.this)
							.setTitle("Character limit exceeded")
							.setMessage(
									"Input cannot exceed more than " + limit
											+ " characters.")
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() > limit) {
					text.setText(s.subSequence(0, limit));
				}
			}
		});
	}

	public View getProgressBar() {
		return this.progressBar;
	}

}
