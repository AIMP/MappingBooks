package interface_module.async_tasks;

import interface_module.BookViewerActivity;
import android.os.AsyncTask;

public class ParseXmlAsync extends AsyncTask {
	protected BookViewerActivity linkedActivity;
	
	public ParseXmlAsync(BookViewerActivity activity) {
		this.linkedActivity = activity;
	}
	
	@Override
	protected void onPreExecute() {
		this.linkedActivity.showProcessingOverlay();
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		// TODO call parsing method
		return null;
	}

}
