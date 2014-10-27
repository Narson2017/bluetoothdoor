package com.bluetooth.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;

import org.park.util.ClsUtils;
import org.park.util.Common;
import org.park.util.HexConvert;

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
	private boolean IS_FOUND;
	static String strAddress;
	public BluetoothSocket btSocket;
	ConnHandle mHandleConn;
	public boolean if_connected;
	Context mCtx;
	boolean if_registed;
	public boolean if_connecting;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	LockCommand mCmdmgr;
	int nNeed;
	byte[] bRecv;
	int nRecved;
	public boolean if_receiving;

	public Connecter(ConnHandle c, Context ctx) {
		super();
		mHandleConn = c;
		mCtx = ctx;
		register(ctx);
		mCmdmgr = new LockCommand();
		if_connected = false;
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		IS_FOUND = false;
		if_registed = false;
		if_connecting = false;
		nNeed = -1;
		bRecv = new byte[1024];
		nRecved = 0;
		if_receiving = false;
	}

	public void register(Context c) {
		if (!if_registed) {
			IntentFilter intent = new IntentFilter();
			intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
			intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			intent.addAction(BluetoothDevice.ACTION_FOUND);
			intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
			intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			c.registerReceiver(this, intent);
			if_registed = true;
		}
	}

	public void receiving() {
		if (btSocket != null) {
			if_connected = true;
			// Get the input and output streams, using temp objects because
			// member streams are final
			InputStream tmpIn = null;
			OutputStream tmpOut = null;
			try {
				tmpIn = btSocket.getInputStream();
				tmpOut = btSocket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
				if_connected = false;
			}
			mmInStream = tmpIn;
			mmOutStream = tmpOut;
			new Thread(ConnThread).start();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
			clean();
			mHandleConn.disconnected();
		} else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
			receiving();
			mHandleConn.connected(true);

		} else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			try {
				ClsUtils.setPin(btDevice.getClass(), btDevice,
						Common.DEFAULT_PIN_CODE);
				ClsUtils.createBond(btDevice.getClass(), btDevice);
				ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (action.equals(BluetoothDevice.ACTION_FOUND)) { // found
																	// device
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			if (btDevice.getAddress().equalsIgnoreCase(
					strAddress == null ? Common.DEFAULT_DEVICE_ADDR
							: strAddress)) {
				IS_FOUND = true;
				mHandleConn.found(true);
				connect();
				return;
			}
		} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
			mHandleConn.searching();
		} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
			if (IS_FOUND)
				mHandleConn.searched();
			else
				mHandleConn.found(false);

		}
	}

	public void start() {
		// onClean();
		IS_FOUND = false;
		if_connecting = true;
		if_connected = false;
		register(mCtx);
		if (!btAdapt.isEnabled()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					btAdapt.enable();
				}

			}).start();
			// wait for opening bluetooth
			mHandler.sendEmptyMessageDelayed(Common.MESSAGE_START_SEARCHING,
					1024);
		} else {
			btAdapt.cancelDiscovery();
			if (!btAdapt.isDiscovering()) {
				if (!findPairedDevice())
					btAdapt.startDiscovery();
			}
		}
	}

	private void connect() {
		btAdapt.cancelDiscovery();
		try {
			new Thread(new Runnable() {
				public void run() {
					try {
						// UUID uuid = UUID.fromString(Common.SPP_UUID);
						BluetoothDevice btDev = btAdapt
								.getRemoteDevice(strAddress == null ? Common.DEFAULT_DEVICE_ADDR
										: strAddress);
						Method m = btDev.getClass()
								.getMethod("createRfcommSocket",
										new Class[] { int.class });
						btSocket = (BluetoothSocket) m.invoke(btDev, 1);
						// btSocket =
						// btDev.createRfcommSocketToServiceRecord(uuid);
						btSocket.connect();
						if_connected = true;
						if_connecting = false;
					} catch (Exception e) {
						unpair();
						e.printStackTrace();
						mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_FAILED);
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
					mHandleConn.found(true);
					connect();
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
			case Common.MESSAGE_START_SEARCHING:
				btAdapt.cancelDiscovery();
				if (!btAdapt.isDiscovering()) {
					if (!findPairedDevice())
						btAdapt.startDiscovery();
				}
				break;
			case Common.MESSAGE_RECV:
				if (if_receiving) {
					mHandleConn.received(HexConvert.Bytes2HexString(bRecv,
							nNeed));
					nRecved = 0;
				}
				break;
			case Common.MESSAGE_EXCEPTION_RECV:
				mHandleConn.received(null);
				break;
			case Common.MESSAGE_CONNECT_FAILED:
				mHandleConn.disconnected();
				break;
			}
		}
	};

	public void clean() {
		mHandleConn.disconnecting();
		if_connecting = false;
		if_connected = false;
		btAdapt.cancelDiscovery();
		if (if_registed) {
			mCtx.unregisterReceiver(Connecter.this);
			if_registed = false;
		}
		try {
			if (mmInStream != null)
				mmInStream.close();
			if (mmOutStream != null)
				mmOutStream.close();
			if (btSocket != null)
				btSocket.close();
		} catch (Exception e) {
			Log.e(Common.TAG, "Close Error");
			e.printStackTrace();
		} finally {
			mmInStream = null;
			mmOutStream = null;
			btSocket = null;
			if_connected = false;
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

	public void send(String mCommand) {
		// TODO Auto-generated method stub
		if (if_connected) {
			nNeed = Common.RESPONSE_LENGTH;
			nRecved = 0;
			try {
				mmOutStream.write(HexConvert.HexString2Bytes(mCommand));
				mmOutStream.flush();
				mHandleConn.sended(true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				mHandleConn.sended(false);
			}
		}
	}

	private Runnable ConnThread = new Runnable() {
		@Override
		public void run() {
			// Keep listening to the InputStream until an exception occurs
			if_receiving = true;
			byte[] bufRecv = new byte[32];
			int nRecv = 0;
			while (if_connected) {
				try {
					if (nRecved >= nNeed) {
						Log.e(Common.TAG, "System busy, please wait");
						Thread.sleep(Common.SYSTEM_WAITE);
						continue;
					}
					nRecv = mmInStream.read(bufRecv);
					if (nRecv < 1) {
						Log.e(Common.TAG, "Recving Short");
						Thread.sleep(Common.RECEIVE_INTERVAL);
						continue;
					}
					System.arraycopy(bufRecv, 0, bRecv, nRecved, nRecv);
					Log.e(Common.TAG, "Recv:" + String.valueOf(nRecv));
					nRecved += nRecv;
					if (nRecved < nNeed) {
						Thread.sleep(Common.RECEIVE_INTERVAL);
						continue;
					}
					mHandler.obtainMessage(Common.MESSAGE_RECV, nNeed, -1, null)
							.sendToTarget();

				} catch (Exception e) {
					if_receiving = false;
					Log.e(Common.TAG, "Recv thread:" + e.getMessage());
					mHandler.sendEmptyMessage(Common.MESSAGE_EXCEPTION_RECV);
					break;
				}
			}
			if_receiving = false;
			Log.e(Common.TAG, "Exit while");
		}
	};
}