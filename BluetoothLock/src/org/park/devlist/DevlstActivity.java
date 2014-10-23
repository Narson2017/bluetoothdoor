package org.park.devlist;

import java.util.ArrayList;
import java.util.List;

import org.park.R;
import org.park.util.About;
import org.park.util.Quit;
import org.park.util.Rotate;

import com.bluetooth.box.BoxActivity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DevlstActivity extends Activity implements OnClickListener {
	ListView lvBTDevices;
	DeviceAdapter adtDevices;
	List<MDevice> lstDevices = new ArrayList<MDevice>();
	public static BluetoothSocket btSocket;

	Button btnDis, btnExit, btnAbout, btn_back;
	public static boolean IS_STORE = true;

	private View lst_devs;
	private ObtainDevlst mObtainDevlst;
	TextView text_hint;
	Rotate mRefresh;
	BluetoothSeeker mSeeker;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.devlst);
		// initail data
		lst_devs = findViewById(R.id.lst_devs);
		text_hint = (TextView) findViewById(R.id.text_hint);
		btnDis = (Button) findViewById(R.id.btnDis);
		btnDis.setOnClickListener(this);
		btnAbout = (Button) findViewById(R.id.btn_about);
		btnAbout.setOnClickListener(this);
		btn_back = (Button) findViewById(R.id.btn_back);
		lvBTDevices = (ListView) this.findViewById(R.id.lvDevices);
		adtDevices = new DeviceAdapter(DevlstActivity.this,
				R.layout.dev_lst_item_row, lstDevices);
		lvBTDevices.setAdapter(adtDevices);
		lvBTDevices.setOnItemClickListener(new ItemClickEvent());

		// start
		mObtainDevlst = new ObtainDevlst();
		mSeeker = new BluetoothSeeker(mObtainDevlst, this);
		mSeeker.start();
		mRefresh = new Rotate(findViewById(R.id.btn_refresh),
				findViewById(R.id.refresh_view));
		mRefresh.start();
	}

	@Override
	protected void onDestroy() {
		mRefresh.stop();
		mSeeker.cancel();
		super.onDestroy();
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	class ItemClickEvent implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mSeeker.cancel();
			lstDevices.get(arg2).ic_btn = R.drawable.btn_connect_pressed;
			Intent intMain = new Intent(getApplicationContext(),
					BoxActivity.class);
			Bundle bd = new Bundle();
			bd.putString("NAME", lstDevices.get(arg2).dev_name);
			bd.putString("MAC", lstDevices.get(arg2).mac_addr);
			intMain.putExtras(bd);
			startActivity(intMain);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mSeeker.cancel();
			mRefresh.stop();
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnDis:
			mSeeker.start();
			break;
		case R.id.btn_exit:
			mSeeker.cancel();
			mRefresh.stop();
			Quit.act_exit(this);
			break;
		case R.id.btn_about:
			About.ShowAbout(DevlstActivity.this);
			break;
		case R.id.btn_action_back:
		case R.id.btn_back:
			mRefresh.stop();
			mSeeker.cancel();
			finish();
			break;
		}
	}

	@Override
	public void onResume() {
		adtDevices.notifyDataSetChanged();
		super.onResume();
	}

	private class ObtainDevlst extends SearchDev {

		@Override
		public void started() {
			// TODO Auto-generated method stub
			lstDevices.clear();
			mRefresh.start();
			lst_devs.setVisibility(View.GONE);
			text_hint.setText(R.string.searching);
		}

		@Override
		public void finished() {
			// TODO Auto-generated method stub
			mRefresh.stop();
			lst_devs.setVisibility(View.VISIBLE);
			if (lstDevices.isEmpty()) {
				text_hint.setText(R.string.not_found);
				mRefresh.display(true);
			} else {
				mRefresh.display(false);
				text_hint.setText(R.string.please_connect);
			}
		}

		@Override
		public void found(BluetoothDevice device) {
			// TODO Auto-generated method stub
			MDevice tmp = new MDevice(lstDevices.size() + 1,
					device.getAddress(), device.getName());
			boolean if_no_exists = true;
			for (int i = 0; i < lstDevices.size(); i++) {
				if (lstDevices.get(i).mac_addr.equalsIgnoreCase(tmp.mac_addr)) {
					if_no_exists = false;
					break;
				}
			}
			if (if_no_exists)
				lstDevices.add(tmp);
			adtDevices.notifyDataSetChanged();
		}
	}
}