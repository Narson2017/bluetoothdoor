package org.park.devlist;

import org.park.util.ClsUtils;
import org.park.util.Common;

import android.bluetooth.BluetoothDevice;

public class SearchDev implements SearchHandle {

	@Override
	public void started() {
		// TODO Auto-generated method stub

	}

	@Override
	public void finished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void found(BluetoothDevice device) {
		// TODO Auto-generated method stub
	}

	@Override
	public void pairing(BluetoothDevice btDevice) {
		// TODO Auto-generated method stub
		try {
			ClsUtils.setPin(btDevice.getClass(), btDevice,
					Common.DEFAULT_PIN_CODE); // 手机和蓝牙采集器配对
			ClsUtils.createBond(btDevice.getClass(), btDevice);
			ClsUtils.cancelPairingUserInput(btDevice.getClass(), btDevice);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
