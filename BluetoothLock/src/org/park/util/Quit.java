package org.park.util;

import org.park.entrance.CoverActivity;

import android.content.Context;
import android.content.Intent;

public class Quit {

	public static void quit(final Context ctx) {
		// finish
		Intent tmp = new Intent(ctx, CoverActivity.class);
		tmp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		tmp.putExtra(Common.IS_EXIT, true);
		ctx.startActivity(tmp);
	}
}