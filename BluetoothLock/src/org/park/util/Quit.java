package org.park.util;

import org.park.entrance.Navigation;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

public class Quit {

	public static void act_exit(final Context ctx) {
		BluetoothAdapter btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt.isDiscovering())
			btAdapt.cancelDiscovery();
		Intent tmp = new Intent(ctx, Navigation.class);
		tmp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		tmp.putExtra(Common.IS_EXIT, true);
		ctx.startActivity(tmp);
	}
}