package org.park.boxlst;

import java.util.List;

import org.park.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bluetooth.authorize.LoginActivity;

public class BoxAdapter extends ArrayAdapter<MBox> implements
AdapterView.OnItemClickListener{
	Context context;
	int resource;
	List<MBox> objects;
	public static String BOX_NUMBER = "box_number";
	public static String CABINET_NUMBER = "cabinet_number";

	public BoxAdapter(Context context, int resource, List<MBox> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.resource = resource;
		this.objects = objects;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg2 != ListView.INVALID_POSITION) {
			// DO THE STUFF YOU WANT TO DO WITH THE position
			Intent intent = new Intent(context,
					LoginActivity.class);
			MBox tmp = objects.get(arg2);
			intent.putExtra(BOX_NUMBER, tmp.box);
			intent.putExtra(CABINET_NUMBER, tmp.cabinet);
			((Activity) context).startActivity(intent);
		}
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		BoxHolder holder = null;
		final MBox mBox = objects.get(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			holder = new BoxHolder();
			holder.tx_box_nbr = (TextView) row.findViewById(R.id.tx_box_nbr);
			row.setTag(holder);
		} else {
			holder = (BoxHolder) row.getTag();
		}
		holder.tx_box_nbr.setText(String.valueOf(mBox.get_nbr()));

		return row;
	}

	static class BoxHolder {
		TextView tx_box_nbr;
	}
}
