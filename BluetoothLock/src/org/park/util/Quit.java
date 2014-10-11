package org.park.util;

import org.park.bluetooth.R;
import org.park.bluetooth.splashScreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class Quit {
	public static final String IS_EXIT = "IS_EXIT";

	public static void act_exit(final Context ctx) {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ctx);

		// 2. Chain together various setter methods to set the dialog
		// characteristics
		builder.setMessage(R.string.exit).setTitle(R.string.hint);
		// Add the buttons
		builder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						Intent tmp = new Intent(ctx, splashScreen.class);
						tmp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						tmp.putExtra(IS_EXIT, true);
						ctx.startActivity(tmp);
					}
				});
		builder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});

		// 3. Get the AlertDialog from create()
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}