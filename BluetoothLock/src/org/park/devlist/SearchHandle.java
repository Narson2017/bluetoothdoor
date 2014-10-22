package org.park.devlist;

import android.bluetooth.BluetoothDevice;

public interface SearchHandle {

	public void started();

	public void finished();

	void found(BluetoothDevice device);

	void pairing(BluetoothDevice btDevice);
}
