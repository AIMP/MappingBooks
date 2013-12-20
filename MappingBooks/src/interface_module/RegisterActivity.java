package interface_module;

import java.util.concurrent.ExecutionException;

import interface_module.async_tasks.RegisterAsyncTask;

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

public class RegisterActivity extends Activity {
	protected EditText userNameEditText;
	protected EditText emailEditText;
	protected EditText passwordEditText;
	protected EditText confirmPasswordEditText;
	protected ProgressBar progressBar;
	private int limit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		progressBar = (ProgressBar)findViewById(R.id.progressBar);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void createAccount(View view) {
		if (view.getId() == R.id.register_button) {
			Intent intent = new Intent(this,BookViewerActivity.class);
			startActivity(intent);
			String username = userNameEditText.getText().toString();
			String email = emailEditText.getText().toString();
			String password = passwordEditText.getText().toString();
			String confirmedPassword = confirmPasswordEditText.getText()
					.toString();
//			if (isValidInput(username, email, password, confirmedPassword)) {
//				try {
//					String response =  new RegisterAsyncTask(this).execute(new String[] {username,email,password,confirmedPassword}).get();
//					Log.v("Response:", response);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (ExecutionException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
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
			}
		});
	}

	private boolean isValidInput(String user, String email, String password,
			String confirmedPassword) {
		// TODO:validate input
		return true;

	}

	public View getProgressBar() {
		return this.progressBar;
	}
}
