package interface_module;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import interface_module.async_tasks.RegisterAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.project.mappingbooks.R;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class RegisterActivity extends Activity {
	protected EditText userNameEditText;
	protected EditText emailEditText;
	protected EditText passwordEditText;
	protected EditText confirmPasswordEditText;
	protected Button registerButton;
	protected ProgressBar progressBar;
	private int limit;
	private String sessionID;
	private final String TAG_STATUS = "status";
	// private final String TAG_ERROR = "errorCode";
	private final String TAG_SESSIONID = "sessionId";
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private Pattern pattern;
	private Matcher matcher;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.init(this);
		pattern = Pattern.compile(EMAIL_PATTERN);
		setContentView(R.layout.activity_register);
		limit = 50;
		userNameEditText = (EditText) findViewById(R.id.input_username);
		setLimit(userNameEditText);
		emailEditText = (EditText) findViewById(R.id.input_email);
		setLimit(emailEditText);
		passwordEditText = (EditText) findViewById(R.id.input_password);
		setLimit(passwordEditText);
		confirmPasswordEditText = (EditText) findViewById(R.id.confirm_input_password);
		setLimit(confirmPasswordEditText);
		registerButton = (Button) findViewById(R.id.register_button);
		registerButton.setEnabled(false);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void createAccount(View view) {
		if (view.getId() == R.id.register_button) {
			String username = userNameEditText.getText().toString();
			String email = emailEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String confirmedPassword = confirmPasswordEditText.getText()
					.toString();
			if (isValidInput(username, email, password, confirmedPassword)) {
				try {
					new RegisterAsyncTask(this).execute(
							new String[] { username, email, password }).get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
					new AlertDialog.Builder(RegisterActivity.this)
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
				if (s.length() > limit) {
					text.setText(s.subSequence(0, limit));
				}
				registerButton.setEnabled(shouldEnableRegister());
			}
		});
	}

	private boolean shouldEnableRegister() {
		return userNameEditText.getText().length() > 0
				&& emailEditText.getText().length() > 0
				&& passwordEditText.getText().length() > 0
				&& confirmPasswordEditText.getText().length() > 0;
	}

	private boolean isValidInput(String user, String email, String password,
			String confirmedPassword) {
		if (user.length() < 4) {
			Utils.alertDialog("Username length",
					"Username must have at least 4 characters.", this);
			return false;
		}
		if (!isValidEmail(email)) {
			Utils.alertDialog("Email Format", "This email is not valid.", this);
			return false;
		}
		if (password.length() < 6) {
			Utils.alertDialog("Password length",
					"Password must have at least 6 characters", this);
			return false;
		}
		if (!password.equals(confirmedPassword)) {
			Utils.alertDialog("Password mismatch", "", this);
			return false;
		}

		return true;
	}

	private boolean isValidEmail(final String email) {

		matcher = pattern.matcher(email);
		return matcher.matches();

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
				bookListIntent.putExtra("sessionID", getSessionID());
				startActivity(bookListIntent);
			} else {
				// String errorCode = responseObject.getString(TAG_ERROR);
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
				alertBuilder.setTitle("Register error").setMessage(
						"There was a problem on the server");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public View getProgressBar() {
		return this.progressBar;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
}
