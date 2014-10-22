package org.park.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import org.park.command.LockCommand;
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
	private boolean IS_FOUND = false;
	static String strAddress = null;
	public BluetoothSocket btSocket = null;
	ConnHandle mHandleConn = null;
	public boolean if_connected = false;
	Context mCtx;
	boolean if_registed = false;
	public boolean if_connecting = false;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	LockCommand mCmdmgr;
	int nNeed = -1;
	byte[] bRecv = new byte[1024];
	int nRecved = 0;

	public Connecter(ConnHandle c, Context ctx) {
		super();
		mHandleConn = c;
		mCtx = ctx;
		register(ctx);
		mCmdmgr = new LockCommand();
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

	public void startCommand() {
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
			mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_LOST);
		} else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			mHandleConn.pairing();
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			try {
				ClsUtils.setPin(btDevice.getClass(), btDevice,
						Common.DEFAULT_PIN_CODE); // 手机和蓝牙采集器配对
				ClsUtils.createBond(btDevice.getClass(), btDevice);
				ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				mHandleConn.paired(false);
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
				mHandleConn.discovery_started();
			} else if (action
					.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
				mHandleConn.discovery_finished();
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
					if (count++ > Common.TIME_OUT)
						break;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (count > Common.TIME_OUT) {
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
				mHandleConn.connected(true);
				break;
			case Common.MESSAGE_CONNECT_LOST:
				if_connected = false;
				if_connecting = false;
				mHandleConn.disconnected();
				break;
			case Common.MSG_TIME_OUT:
				if_connecting = false;
				if (!if_connected)
					mHandleConn.timeout();
				break;
			case Common.MESSAGE_RECV:
				mHandleConn.received(HexConvert.Bytes2HexString(bRecv, nNeed));
				nRecved = 0;
				break;
			}
		}
	};

	public void onClean() {
		unpair();
		if_connecting = false;
		btAdapt.cancelDiscovery();
		if (if_connected) {
			if_connected = false;
			try {
				if (mmInStream != null)
					mmInStream.close();
				if (mmOutStream != null)
					mmOutStream.close();
				if (btSocket != null)
					btSocket.close();
			} catch (IOException e) {
				Log.e(Common.TAG, "Close Error");
				e.printStackTrace();
			} finally {
				mmInStream = null;
				mmOutStream = null;
				btSocket = null;
				if_connected = false;
			}
		}
		if (if_registed) {
			mCtx.unregisterReceiver(this);
			if_registed = false;
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

	private Runnable ConnThread = new Runnable() {
		@Override
		public void run() {
			// Keep listening to the InputStream until an exception occurs
			byte[] bufRecv = new byte[32];
			int nRecv = 0;
			while (if_connected) {
				try {
					if (nRecved >= nNeed) {
						Log.e(Common.TAG, "System busy, please wait");
						Thread.sleep(3000);
						continue;
					}
					nRecv = mmInStream.read(bufRecv);
					if (nRecv < 1) {
						Log.e(Common.TAG, "Recving Short");
						Thread.sleep(100);
						continue;
					}
					System.arraycopy(bufRecv, 0, bRecv, nRecved, nRecv);
					Log.e(Common.TAG, "Recv:" + String.valueOf(nRecv));
					nRecved += nRecv;
					if (nRecved < nNeed) {
						Thread.sleep(100);
						continue;
					}

					mHandler.obtainMessage(Common.MESSAGE_RECV, nNeed, -1, null)
							.sendToTarget();

				} catch (Exception e) {
					Log.e(Common.TAG, "Recv thread:" + e.getMessage());
					mHandler.sendEmptyMessage(Common.MESSAGE_EXCEPTION_RECV);
					break;
				}
			}
			Log.e(Common.TAG, "Exit while");
		}
	};
}