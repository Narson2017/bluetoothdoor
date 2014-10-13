package org.park.connection;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import org.park.bluetooth.R;
import org.park.util.ClsUtils;
import org.park.util.Common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class ConnectCtrl extends BroadcastReceiver {

	public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	public BluetoothAdapter btAdapt;
	public static String DEVICE_MAC_ADDR = "00:0E:0E:00:0F:53";
	public showDetail mCtx;
	private boolean IS_FOUND = false;
	static String strAddress = null;
	public BluetoothSocket btSocket = null;

	public ConnectCtrl() {
		super();
	}

	public ConnectCtrl(showDetail ctx) {
		super();
		this.mCtx = ctx;
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt == null) {
			ctx.detail_view.setVisibility(View.GONE);
			ctx.progress_connect.setVisibility(View.GONE);
			ctx.tx_fault.setText(R.string.blue_unabailable);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
			mCtx.detail_view.setVisibility(View.VISIBLE);
			mCtx.progress_connect.setVisibility(View.GONE);
			mCtx.setHint(R.string.connect_success);
		} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
			mCtx.detail_view.setVisibility(View.GONE);
			mCtx.progress_connect.setVisibility(View.GONE);
			mCtx.tx_fault.setText(R.string.disconnect);
		} else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			mCtx.detail_view.setVisibility(View.GONE);
			mCtx.progress_connect.setVisibility(View.VISIBLE);
			mCtx.tx_fault.setText(R.string.pairing);
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
						strAddress == null ? ConnectCtrl.DEVICE_MAC_ADDR
								: strAddress)) {
					IS_FOUND = true;
					connectTarget(device);
					return;
				}
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				mCtx.setHint(R.string.searching);
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				mCtx.setHint(R.string.not_found);
			}
		}
	}

	public void findDev() {
		IS_FOUND = false;
		if (btAdapt == null)
			btAdapt = BluetoothAdapter.getDefaultAdapter();

		if (!btAdapt.isEnabled())
			btAdapt.enable();
		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_START_DISCOVER, 3072);
	}

	private void connectTarget(final BluetoothDevice btdev) {
		btAdapt.cancelDiscovery();
		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						UUID uuid = UUID.fromString(SPP_UUID);
						btSocket = btdev
								.createRfcommSocketToServiceRecord(uuid);
						btSocket.connect();
					} catch (Exception e) {
						btSocket = null;
						e.printStackTrace();
						mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_LOST);
						return;
					}
					mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_SUCCEED);
				}

			}).start();

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
						strAddress == null ? DEVICE_MAC_ADDR : strAddress)) {
					IS_FOUND = true;
					connectTarget(device);
					return true;
				}
		}
		return false;
	}

	public void setMac(String mac) {
		strAddress = mac;
	}

	// Hander
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_CONNECT_SUCCEED:
				mCtx.setBoxVisible(true);
				mCtx.setProgressVisible(false);
				mCtx.setHint(R.string.connect_success);
				mCtx.setBoxState(true, true);
				mCtx.setBoxEnable(true);
				mCtx.setConn(new ConnectedThread(btSocket, mCtx));
				mCtx.startConn();
				break;
			case Common.MESSAGE_START_DISCOVER:
				if (btAdapt.getState() != BluetoothAdapter.STATE_ON) {
					mCtx.tx_fault.setText(R.string.open_blue);
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

	public void unpair() {
		Set<BluetoothDevice> bondedDevices = btAdapt.getBondedDevices();
		try {
			Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class
					.getCanonicalName());
			Method removeBondMethod = btDeviceInstance.getMethod("removeBond");
			boolean cleared = false;
			for (BluetoothDevice bluetoothDevice : bondedDevices) {
				String mac = bluetoothDevice.getAddress();
				if (mac.equals(strAddress == null ? ConnectCtrl.DEVICE_MAC_ADDR
						: strAddress)) {
					removeBondMethod.invoke(bluetoothDevice);
					Log.i(Common.TAG, "Cleared Pairing");
					cleared = true;
					break;
				}
			}
			if (!cleared) {
				Log.i(Common.TAG, "Not Paired");
			}
		} catch (Throwable th) {
			Log.e(Common.TAG, "Error pairing", th);
		}
	}
}