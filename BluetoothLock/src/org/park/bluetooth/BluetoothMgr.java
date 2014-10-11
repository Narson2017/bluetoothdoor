package org.park.bluetooth;

import java.util.Set;

import org.park.util.ClsUtils;
import org.park.util.Common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BluetoothMgr extends BroadcastReceiver {

	public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	public static BluetoothAdapter btAdapt;
	public static String DEVICE_MAC_ADDR = "00:0E:0E:00:0F:54";
	public static Context mCtx;
	private boolean IS_FOUND = false;
	public String actualAddr = null;

	public BluetoothMgr() {
		super();
	}

	public BluetoothMgr(Context ctx, BluetoothAdapter btAdapt2) {
		super();
		BluetoothMgr.mCtx = ctx;
		BluetoothMgr.btAdapt = btAdapt2;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED))
			((Controler) mCtx).changeView(View.VISIBLE, View.GONE,
					R.string.connect_failed, View.GONE);
		else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED))
			((Controler) mCtx).changeView(View.GONE, View.GONE,
					R.string.connect_failed, View.VISIBLE);
		else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			try {
				ClsUtils.setPin(btDevice.getClass(), btDevice,
						showDetail.PIN_CODE); // 手机和蓝牙采集器配对
				ClsUtils.createBond(btDevice.getClass(), btDevice);
				ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!IS_FOUND) {
			if (action.equals(BluetoothDevice.ACTION_FOUND)) { // found device
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getAddress().equalsIgnoreCase(
						actualAddr == null ? BluetoothMgr.DEVICE_MAC_ADDR
								: actualAddr)) {
					IS_FOUND = true;
					connectTarget(device);
					return;
				}

			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				Toast.makeText(BluetoothMgr.mCtx, R.string.searching,
						Toast.LENGTH_SHORT).show();
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				((Controler) BluetoothMgr.mCtx).changeView(View.GONE,
						View.GONE, R.string.not_found, View.VISIBLE);
			}
		}
	}

	public void findDev(String mac) {
		IS_FOUND = false;
		actualAddr = mac;
		if (btAdapt == null)
			btAdapt = BluetoothAdapter.getDefaultAdapter();

		if (!btAdapt.isEnabled())
			btAdapt.enable();
		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_START_DISCOVER, 3072);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_START_DISCOVER:
				if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {
					Toast.makeText(BluetoothMgr.mCtx, R.string.open_blue, 1000)
							.show();
					break;
				}

				if (!btAdapt.isDiscovering()) {
					if (!findPairedDevice())
						btAdapt.startDiscovery();
				}
				break;
			}
		}
	};

	private void connectTarget(BluetoothDevice btdev) {
		btAdapt.cancelDiscovery();
		IS_FOUND = true;
		try {
			((Controler) BluetoothMgr.mCtx).connectDev(btdev.getAddress(),
					btdev.getName());
		} catch (Exception e) {
			Log.d(Common.TAG, "Error connected to: " + btdev.getAddress());
			e.printStackTrace();
		}
	}

	private boolean findPairedDevice() //
	{
		Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices)
				if (device.getAddress().equalsIgnoreCase(
						actualAddr == null ? DEVICE_MAC_ADDR : actualAddr)) {
					connectTarget(device);
					return true;
				}
		}
		return false;
	}
}