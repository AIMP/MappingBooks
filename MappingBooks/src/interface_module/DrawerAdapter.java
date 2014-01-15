package interface_module;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

class DrawerAdapter extends BaseAdapter {
	Context rContext;
	private LayoutInflater rInflater;
	private Activity activity;

	public DrawerAdapter(Context c) {
		setrInflater(LayoutInflater.from(c));
		rContext = c;

	}

	public DrawerAdapter(Activity imagebinding) {
		// TODO Auto-generated constructor stub
		activity = imagebinding;
		rContext = imagebinding;
		setrInflater(LayoutInflater.from(imagebinding));
		rContext = imagebinding;
		setrInflater((LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE));

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return 10;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	public LayoutInflater getrInflater() {
		return rInflater;
	}

	public void setrInflater(LayoutInflater rInflater) {
		this.rInflater = rInflater;
	}

}
