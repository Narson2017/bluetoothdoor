package org.park.devlist;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SearchDevReceiver extends BroadcastReceiver {
	Devlster mDevlster;

	public SearchDevReceiver(Context mctx) {
		mDevlster = (Devlster) mctx;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(BluetoothDevice.ACTION_FOUND)) { // found device
			BluetoothDevice device = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mDevlster.whenFound(device);

		} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
			mDevlster.whenStarted();
		} else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
			mDevlster.whenFinished();
		} else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
			BluetoothDevice btDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			mDevlster.whenPairing(btDevice);
		}

	}
}
