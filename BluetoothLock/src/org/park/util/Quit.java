package org.park.util;

import org.park.entrance.CoverActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

public class Quit {

	public static void quit(final Context ctx) {
		// stop discovering
		BluetoothAdapter btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt.isDiscovering())
			btAdapt.cancelDiscovery();
		if (btAdapt.isEnabled())
			btAdapt.enable();
		// wait for shutting down bluetooth
		try {
			Thread.sleep(2048);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// finish
		Intent tmp = new Intent(ctx, CoverActivity.class);
		tmp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		tmp.putExtra(Common.IS_EXIT, true);
		ctx.startActivity(tmp);
	}
}