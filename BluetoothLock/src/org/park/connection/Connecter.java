package org.park.connection;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import org.park.util.ClsUtils;
import org.park.util.Common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Connecter extends BroadcastReceiver {

	public BluetoothAdapter btAdapt;
	private boolean IS_FOUND = false;
	static String strAddress = null;
	public BluetoothSocket btSocket = null;
	HandleConnMsg mHandleConnMsg = null;
	public boolean if_connected = false;
	Context mCtx;
	boolean if_registed = false;
	public boolean if_connecting = false;

	public Connecter() {
		super();
	}

	public Connecter(HandleConnMsg c, Context ctx) {
		super();
		mHandleConnMsg = c;
		mCtx = ctx;
		register(ctx);
	}

	public void register(Context c) {
		if (!if_registed) {
			IntentFilter intent = new IntentFilter();
			intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			intent.addAction(BluetoothDevice.ACTION_FOUND);
			intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
			intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			c.registerReceiver(this, intent);
			if_registed = true;
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
			mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_LOST);
		} else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			mHandleConnMsg.pairing();
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			try {
				ClsUtils.setPin(btDevice.getClass(), btDevice,
						Common.DEFAULT_PIN_CODE); // 手机和蓝牙采集器配对
				ClsUtils.createBond(btDevice.getClass(), btDevice);
				ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				mHandleConnMsg.paired(false);
				e.printStackTrace();
			}
		}

		if (!IS_FOUND) {
			if (action.equals(BluetoothDevice.ACTION_FOUND)) { // found device
				BluetoothDevice btDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (btDevice.getAddress().equalsIgnoreCase(
						strAddress == null ? Common.DEFAULT_DEVICE_ADDR
								: strAddress)) {
					IS_FOUND = true;
					btAdapt.cancelDiscovery();
					connectTarget();
					return;
				}
			} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
				mHandleConnMsg.discovery_started();
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				mHandleConnMsg.discovery_finished();
			}
		}
	}

	public void connect() {
		IS_FOUND = false;
		if_connecting = true;
		if_connected = false;
		if (btAdapt == null)
			btAdapt = BluetoothAdapter.getDefaultAdapter();

		if (!btAdapt.isEnabled())
			btAdapt.enable();
		register(mCtx);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int count = 1;
				while (!if_connected && if_connecting) {
					if (count++ > 4)
						break;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (count > 6) {
					mHandler.sendEmptyMessage(Common.MSG_TIME_OUT);
				}
			}

		}).start();
		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_START_DISCOVER, 1024);
	}

	private void connectTarget() {
		btAdapt.cancelDiscovery();
		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						UUID uuid = UUID.fromString(Common.SPP_UUID);
						BluetoothDevice btDev = btAdapt
								.getRemoteDevice(strAddress == null ? Common.DEFAULT_DEVICE_ADDR
										: strAddress);
						btSocket = btDev
								.createRfcommSocketToServiceRecord(uuid);
						btSocket.connect();
						mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_SUCCEED);
					} catch (Exception e) {
						btSocket = null;
						e.printStackTrace();
						mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_LOST);
						return;
					}
				}

			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean findPairedDevice() //
	{
		Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices)
				if (device.getAddress().equalsIgnoreCase(
						strAddress == null ? Common.DEFAULT_DEVICE_ADDR
								: strAddress)) {
					IS_FOUND = true;
					connectTarget();
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
			case Common.MESSAGE_START_DISCOVER:
				if (!btAdapt.isDiscovering()) {
					if (!findPairedDevice())
						btAdapt.startDiscovery();
				}
				break;
			case Common.MESSAGE_CONNECT_SUCCEED:
				if_connected = true;
				if_connecting = false;
				mHandleConnMsg.connected(true);
				break;
			case Common.MESSAGE_CONNECT_LOST:
				if_connected = false;
				if_connecting = false;
				mHandleConnMsg.disconnected();
				break;
			case Common.MSG_TIME_OUT:
				if_connecting = false;
				if (!if_connected)
					mHandleConnMsg.timeout();
				break;
			}
		}
	};

	public void onClean() {
		if_connecting = false;
		btAdapt.cancelDiscovery();
		if (if_connected) {
			if_connected = false;
		}
		unpair();
		if (if_registed) {
			mCtx.unregisterReceiver(this);
			if_registed = false;
		}
		// if (btAdapt != null)
		// btAdapt.disable();
		try {
			if (btSocket != null)
				btSocket.close();
		} catch (IOException e) {
			Log.e(Common.TAG, "Close Error");
			e.printStackTrace();
		} finally {
			btSocket = null;
		}
	}

	public void unpair() {
		Set<BluetoothDevice> bondedDevices = btAdapt.getBondedDevices();
		try {
			Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class
					.getCanonicalName());
			Method removeBondMethod = btDeviceInstance.getMethod("removeBond");
			boolean cleared = false;
			for (BluetoothDevice bluetoothDevice : bondedDevices) {
				String mac = bluetoothDevice.getAddress();
				if (mac.equals(strAddress == null ? Common.DEFAULT_DEVICE_ADDR
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

	public void send(String old_password, String new_password) {
		Log.i(Common.TAG, "Sended");
		mHandleConnMsg.sended(true);
	}
}