package org.park.boxlst;

import java.util.List;

import org.park.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BoxAdapter extends ArrayAdapter<MBox> {
	Context context;
	int resource;
	List<MBox> objects;

	public BoxAdapter(Context context, int resource, List<MBox> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resource = resource;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		BoxHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			holder = new BoxHolder();
			holder.tx_box_nbr = (TextView) row.findViewById(R.id.tx_box_nbr);

			row.setTag(holder);
		} else {
			holder = (BoxHolder) row.getTag();
		}

		MBox mBox = objects.get(position);
		holder.tx_box_nbr.setText(String.valueOf(mBox.get_nbr()));

		return row;
	}

	static class BoxHolder {
		TextView tx_box_nbr;
	}
}
