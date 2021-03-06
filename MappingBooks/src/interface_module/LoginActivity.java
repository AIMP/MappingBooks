package interface_module;

import java.util.concurrent.ExecutionException;

import interface_module.async_tasks.LoginAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.project.mappingbooks.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class LoginActivity extends Activity {
	protected EditText userNameEditText;
	protected EditText passwordEditText;
	protected ProgressBar progressBar;
	protected Button loginButton;
	private int limit;
	private String sessionID;
	private final String TAG_STATUS = "status";
	private final String TAG_SESSIONID = "sessionId";
	protected boolean usernameFieldIsEmpty = true;
	protected boolean passwordFieldIsEmpty = true;
	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.init(this);

		setContentView(R.layout.activity_login);
		userNameEditText = (EditText) findViewById(R.id.input_username);
		passwordEditText = (EditText) findViewById(R.id.input_password);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setEnabled(!usernameFieldIsEmpty & !passwordFieldIsEmpty);
		limit = 50;
		setLimit(userNameEditText);
		setLimit(passwordEditText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void createAccount(View view) {
		if (view.getId() == R.id.register_button) {
			Intent i = new Intent(this, RegisterActivity.class);
			startActivity(i);
		}
	}

	public void forgotPassword(View view) {
		// TODO: create a new intent for forgot password activity
	}

	public void login(View view) {

		if (view.getId() == R.id.login_button) {
			username = userNameEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			try {
				new LoginAsyncTask(this).execute(
						new String[] { username, password }).get();
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
				if (s.length() > 0) {
					if (text == passwordEditText) {
						passwordFieldIsEmpty = false;
					} else {
						usernameFieldIsEmpty = false;
					}
				} else {
					if (text == passwordEditText) {
						passwordFieldIsEmpty = true;
					} else {
						usernameFieldIsEmpty = true;
					}
				}
				loginButton.setEnabled(!passwordFieldIsEmpty
						& !usernameFieldIsEmpty);
				if (s.length() > limit) {
					text.setText(s.subSequence(0, limit));
				}
			}
		});
	}

	public View getProgressBar() {
		return this.progressBar;
	}

	/**
	 * Handle the response received(JSON) for login action
	 * 
	 * @param response
	 */
	public void handleResponse(String response) {

		try {
			JSONObject responseObject = new JSONObject(response);
			String status = responseObject.getString(TAG_STATUS);
			if (status.equalsIgnoreCase("ok")) {
				setSessionID(responseObject.getString(TAG_SESSIONID));
				Intent bookListIntent = new Intent(getApplicationContext(),
						BookListActivity.class);
				bookListIntent.putExtra("username", username);
				bookListIntent.putExtra("sessionID", getSessionID());
				startActivity(bookListIntent);
			} else {
				// String errorCode = responseObject.getString(TAG_ERROR);
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setTitle("Login error").setMessage(
						"Your username and password are incorrect.");
				alertBuilder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog dialog = alertBuilder.create();
				dialog.show();
			}
		} catch (JSONException e) {
			Utils.alertDialog("Server Response",
					"Unrecognized response received.", this);
		}

	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
}
