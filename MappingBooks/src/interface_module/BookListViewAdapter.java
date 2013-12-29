package interface_module;

import java.util.List;

import com.project.mappingbooks.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BookListViewAdapter extends ArrayAdapter<RowItem> {
	private List<RowItem> items;
	private Context context;

	public BookListViewAdapter(Context context, int resourceId,
			List<RowItem> items) {
		super(context, resourceId, items);
		this.items = items;
		this.context = context;
	}

	public void setItems (List<RowItem> items) {
		this.items = items;
	}
	
	private class ViewHolder {
		ImageView imageView;
		TextView txtTitle;
		TextView txtDesc;
	}
	@Override
	public int getCount() {
		return (this.items == null) ? 0 : this.items.size();
	}
	
	@Override
	public RowItem getItem(int position) {
		return this.items.get(position);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RowItem rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();
			holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.txtDesc.setText(rowItem.getDesc());
		holder.txtTitle.setText(rowItem.getTitle());
		holder.imageView.setImageResource(rowItem.getImageId());

		return convertView;
	}
}