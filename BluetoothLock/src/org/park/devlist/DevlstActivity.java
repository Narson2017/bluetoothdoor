package org.park.devlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.park.R;
import org.park.authorize.LoginActivity;
import org.park.box.showDetail;
import org.park.connection.ConnectCtrl;
import org.park.util.About;
import org.park.util.ClsUtils;
import org.park.util.Common;
import org.park.util.Quit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class DevlstActivity extends Activity implements OnClickListener,
		Devlster {
	public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	ListView lvBTDevices;
	DeviceAdapter adtDevices;
	List<MDevice> lstDevices = new ArrayList<MDevice>();
	BluetoothAdapter btAdapt;
	public static BluetoothSocket btSocket;

	Button btnDis, btnExit, btnAbout, btn_back;
	public static boolean IS_STORE = true;

	private View mProgress, lst_devs;
	protected String DEVICE_MAC_ADDR = "00:0E:0E:00:0F:54";
	private SearchDevReceiver mSearchDev;
	TextView text_hint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.devlst);

		mProgress = findViewById(R.id.progressBar1);
		lst_devs = findViewById(R.id.lst_devs);
		text_hint = (TextView) findViewById(R.id.text_hint);

		// Button
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

		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt == null) {
			text_hint.setText(R.string.blue_unabailable);
			startActivity(new Intent(this, LoginActivity.class));
			if (btAdapt != null)
				btAdapt.disable();
			finish();
		}
		if (!btAdapt.isEnabled())
			btAdapt.enable();
		// 注册Receiver来获取蓝牙设备相关的结果
		mSearchDev = new SearchDevReceiver(DevlstActivity.this);
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		registerReceiver(mSearchDev, intent);
		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_START_DISCOVER, 3072);
	}

	private void addPairedDevice() //
	{
		Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				// if (device.getAddress().equalsIgnoreCase(DEVICE_MAC_ADDR))
				// mHandler.obtainMessage(Common.MESSAGE_TARGET_FOUND, device)
				// .sendToTarget();
				MDevice tmp = new MDevice(lstDevices.size() + 1,
						device.getAddress(), device.getName());
				lstDevices.add(tmp);
				adtDevices.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected void onDestroy() {
		if (btAdapt != null)
			btAdapt.disable();
		unregisterReceiver(mSearchDev);
		super.onDestroy();
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	class ItemClickEvent implements AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {
				text_hint.setText(R.string.open_blue);
				return;
			}

			if (btAdapt.isDiscovering())
				btAdapt.cancelDiscovery();
			lstDevices.get(arg2).ic_btn = R.drawable.btn_connect_pressed;
			String address = lstDevices.get(arg2).mac_addr;
			Log.i(Common.TAG, lstDevices.get(arg2).mac_addr);
			ClsUtils.pair(address, ConnectCtrl.DEFAULT_PIN_CODE);
			try {
				Intent intMain = new Intent(getApplicationContext(),
						showDetail.class);
				Bundle bd = new Bundle();
				bd.putString("NAME", lstDevices.get(arg2).dev_name);
				bd.putString("MAC", lstDevices.get(arg2).mac_addr);
				intMain.putExtras(bd);
				startActivity(intMain);
			} catch (Exception e) {
				Log.d(Common.TAG, "Error connected to: " + address);
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(this, LoginActivity.class));
			if (btAdapt != null)
				btAdapt.disable();
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
			mHandler.sendEmptyMessage(Common.MESSAGE_START_DISCOVER);
			break;
		case R.id.btn_exit:
			Quit.act_exit(this);
			break;
		case R.id.btn_about:
			About.ShowAbout(DevlstActivity.this);
			break;
		case R.id.btn_action_back:
		case R.id.btn_back:
			startActivity(new Intent(this, LoginActivity.class));
			if (btAdapt != null)
				btAdapt.disable();
			finish();
			break;
		}
	}

	@Override
	public void onResume() {
		adtDevices.notifyDataSetChanged();
		super.onResume();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_SHOW_DEVICES:
				mProgress.setVisibility(View.GONE);
				lst_devs.setVisibility(View.VISIBLE);
				break;
			case Common.MESSAGE_START_DISCOVER:
				if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {
					text_hint.setText(R.string.open_blue);
					break;
				}

				if (!btAdapt.isDiscovering()) {
					lstDevices.clear();
					addPairedDevice();
					btAdapt.startDiscovery();
				}
				break;
			case Common.MESSAGE_TARGET_FOUND:
				btAdapt.cancelDiscovery();
				try {
					Intent intMain = new Intent(getApplicationContext(),
							showDetail.class);
					Bundle bd = new Bundle();
					bd.putString("NAME", ((BluetoothDevice) msg.obj).getName());
					bd.putString("MAC",
							((BluetoothDevice) msg.obj).getAddress());
					intMain.putExtras(bd);
					startActivity(intMain);
				} catch (Exception e) {
					Log.d(Common.TAG, "Error connected to: "
							+ ((BluetoothDevice) msg.obj).getAddress());
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void whenFound(BluetoothDevice device) {
		// TODO Auto-generated method stub
		MDevice tmp = new MDevice(lstDevices.size() + 1, device.getAddress(),
				device.getName());
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

	@Override
	public void whenStarted() {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.VISIBLE);
		lst_devs.setVisibility(View.GONE);
		text_hint.setText(R.string.searching);
	}

	@Override
	public void whenFinished() {
		// TODO Auto-generated method stub
		mProgress.setVisibility(View.GONE);
		lst_devs.setVisibility(View.VISIBLE);
		if (lstDevices.isEmpty())
			text_hint.setText(R.string.not_found);
		else
			text_hint.setText(R.string.please_connect);
	}

	@Override
	public void whenPairing(BluetoothDevice btDevice) {
		// TODO Auto-generated method stub
		try {
			ClsUtils.setPin(btDevice.getClass(), btDevice,
					ConnectCtrl.DEFAULT_PIN_CODE); // 手机和蓝牙采集器配对
			ClsUtils.createBond(btDevice.getClass(), btDevice);
			ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}