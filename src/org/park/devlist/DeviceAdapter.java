package org.park.devlist;

import java.util.List;

import org.park.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DeviceAdapter extends ArrayAdapter<MDevice> {

	Context context;
	int layoutResourceId;
	List<MDevice> data = null;

	public DeviceAdapter(Context context, int layoutResourceId,
			List<MDevice> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DeviceHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new DeviceHolder();
			holder.ic_bar = (ImageView) row.findViewById(R.id.ic_bar);
			holder.tx_index = (TextView) row.findViewById(R.id.tx_index);
			holder.tx_devname = (TextView) row.findViewById(R.id.tx_devname);
			holder.btn_connect = (Button) row.findViewById(R.id.btn_connect);

			row.setTag(holder);
		} else {
			holder = (DeviceHolder) row.getTag();
		}

		MDevice mDevice = data.get(position);
		holder.tx_index.setText(String.valueOf(mDevice.index));
		holder.ic_bar.setImageResource(R.drawable.ic_bar);
		holder.tx_devname.setText(mDevice.dev_name + ":" + mDevice.mac_addr);
		holder.btn_connect.setBackgroundResource(mDevice.ic_btn);

		return row;
	}

	static class DeviceHolder {
		ImageView ic_bar;
		TextView tx_index;
		TextView tx_devname;
		Button btn_connect;
	}
}