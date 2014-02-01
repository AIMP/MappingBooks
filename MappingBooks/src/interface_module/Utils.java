package interface_module;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ApplicationInfo;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * This class provides various utility methods.
 */
public class Utils {
	/**
	 * This variable denotes the maximum number of characters of a text inside a
	 * toast which should be shown for a short period of time.
	 * 
	 * TODO: Make sure this value is appropriate.
	 */
	static final int toastThreshold = 30;
	static Context context;
	public static String TAG;
	static ApplicationInfo appInfo;

	/*
	 * This method must be called whenever a certain activity is started or
	 * resumed in order to be used properly.
	 */
	public static void init(Context context) {
		appInfo = context.getApplicationInfo();
		TAG = appInfo.packageName;
		Utils.context = context;
		NetworkManager.init(context);
	}

	public static void d(String format, Object... args) {
		String message = String.format(format, args);
		Log.d(TAG, message);
	}

	public static void v(String format, Object... args) {
		String message = String.format(format, args);
		Log.v(TAG, message);
	}

	public static void toast(int stringId) {
		String text = context.getResources().getString(stringId);
		toast(text);
	}

	public static void toast(String text) {
		int duration = text.length() < toastThreshold ? Toast.LENGTH_SHORT
				: Toast.LENGTH_LONG;
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
		toast.show();
	}

	public static void alertDialog(String title, String message, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title).setMessage(message)
				.setPositiveButton("OK", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
