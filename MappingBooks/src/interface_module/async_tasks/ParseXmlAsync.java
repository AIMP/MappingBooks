package interface_module.async_tasks;

import java.util.List;

import interface_module.BookViewerActivity;
import interface_module.Components;
import interface_module.Segment;
import interface_module.Words;
import interface_module.XMLParser;
import android.os.AsyncTask;

public class ParseXmlAsync extends AsyncTask<String, Void, Components> {
	protected BookViewerActivity linkedActivity;

	public ParseXmlAsync(BookViewerActivity activity) {
		this.linkedActivity = activity;
	}

	@Override
	protected void onPreExecute() {
		this.linkedActivity.showProcessingOverlay();
	}

	@Override
	protected Components doInBackground(String... params) {

		XMLParser x = new XMLParser();
		List<Segment> l1 = x.XMLTagParser((String) params[0]);
		List<Words> l2 = x.XMLTag((String) params[0]);
		Components components = new Components();
		components.setSegments(l1);
		components.setWords(l2);
		return components;
	}

	@Override
	protected void onPostExecute(Components result) {
		linkedActivity.removeOverlay();
		linkedActivity.showBook(result.getWords());
		//linkedActivity.showSegments(result.getSegments());
	}

}
