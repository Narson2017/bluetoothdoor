package org.park.util;

import org.park.entrance.splashScreen;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

public class Quit {
	public static final String IS_EXIT = "IS_EXIT";

	public static void act_exit(final Context ctx) {
		BluetoothAdapter btAdapt = BluetoothAdapter
				.getDefaultAdapter();
		if (btAdapt.isDiscovering())
			btAdapt.cancelDiscovery();
		if (btAdapt.isEnabled())
			btAdapt.disable();
		Intent tmp = new Intent(ctx, splashScreen.class);
		tmp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		tmp.putExtra(IS_EXIT, true);
		ctx.startActivity(tmp);
	}
}