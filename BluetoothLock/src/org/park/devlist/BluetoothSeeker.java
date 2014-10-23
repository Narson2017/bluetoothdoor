package org.park.devlist;

import java.util.Set;

import org.park.util.Common;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

public class BluetoothSeeker extends BroadcastReceiver {
	BluetoothAdapter btAdapt;
	SearchHandle mSearchHandle;
	Context mctx;
	boolean if_registered;

	public BluetoothSeeker(SearchHandle s, Context c) {
		mctx = c;
		mSearchHandle = s;
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if_registered = false;
	}

	public void start() {
		register();
		if (!btAdapt.isEnabled())
			btAdapt.enable();
		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_START_SEARCHING,
				Common.DELAY_TIME);
	}

	public void cancel() {
		if (if_registered) {
			mctx.unregisterReceiver(this);
			if_registered = false;
		}
		if (btAdapt.isDiscovering())
			btAdapt.cancelDiscovery();
	}

	private void addPairedDevice() //
	{
		Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				mSearchHandle.found(device);
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_SHOW_DEVICES:
				mSearchHandle.finished();
				break;
			case Common.MESSAGE_START_SEARCHING:
				if (!btAdapt.isDiscovering()) {
					addPairedDevice();
					btAdapt.startDiscovery();
				}
				mSearchHandle.started();
				break;
			}
		}
	};

	public void register() {
		if (!if_registered) {
			IntentFilter intent = new IntentFilter();
			intent.addAction(BluetoothDevice.ACTION_FOUND); // 用BroadcastReceiver来取得搜索结果
			intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
			mctx.registerReceiver(this, intent);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(BluetoothDevice.ACTION_FOUND)) { // found device
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mSearchHandle.found(device);

		} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
			mSearchHandle.started();
		} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
			mSearchHandle.finished();
		} else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mSearchHandle.pairing(btDevice);
		}
	}
}
