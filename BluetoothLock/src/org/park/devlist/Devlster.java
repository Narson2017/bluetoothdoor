package org.park.devlist;

import android.bluetooth.BluetoothDevice;

public interface Devlster {

	public void whenStarted();

	public void whenFinished();

	void whenFound(BluetoothDevice device);

	void whenPairing(BluetoothDevice btDevice);
}
