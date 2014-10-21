package org.park.boxlst;

import java.util.List;

import org.park.R;
import org.park.box.BoxActivity;
import org.park.util.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class BoxAdapter extends ArrayAdapter<MBox> {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row = convertView;
		BoxHolder holder = null;
		final MBox mBox = objects.get(position);

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(resource, parent, false);

			holder = new BoxHolder();
			holder.tx_box_nbr = (TextView) row.findViewById(R.id.tx_box_nbr);
			holder.btn_box = (Button) row.findViewById(R.id.btn_box);
			holder.btn_box.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					switch (arg0.getId()) {
					case R.id.btn_box:
						if (position != ListView.INVALID_POSITION) {
							// DO THE STUFF YOU WANT TO DO WITH THE position
							Intent intent = new Intent(context,
									BoxActivity.class);
							// intent.putExtra(BOX_NUMBER, mBox.get_nbr());
							// intent.putExtra(CABINET_NUMBER, mBox.cabinet_id);
							SharedPreferences mPrefs;
							mPrefs = PreferenceManager
									.getDefaultSharedPreferences(getContext());
							mPrefs.edit()
									.putString("password",
											Common.DEFAULT_PAIR_PASSWORD)
									.commit();
							mPrefs.edit()
									.putString("locknbr",
											String.valueOf(mBox.get_nbr()))
									.commit();
							mPrefs.edit()
									.putString("cabinet",
											String.valueOf(mBox.cabinet_id))
									.commit();
							((BoxlstActivity) context).startActivity(intent);
						}
						break;
					}
				}
			});

			row.setTag(holder);
		} else {
			holder = (BoxHolder) row.getTag();
		}
		holder.tx_box_nbr.setText(String.valueOf(mBox.get_nbr()));

		return row;
	}

	static class BoxHolder {
		TextView tx_box_nbr;
		Button btn_box;
	}
}
