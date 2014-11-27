package org.park.util;

import org.park.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

public class About {

	public static void ShowAbout(final Context ctx) {
		new AlertDialog.Builder(ctx)
				.setIcon(R.drawable.ic_launcher)
				.setTitle(R.string.company_name)
				.setMessage(R.string.welcome)
				.setPositiveButton(R.string.btn_cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								// dialog.dismiss();
								Uri uri = Uri.parse("http://www.gzjnu.com/");
								Intent intent = new Intent(Intent.ACTION_VIEW,
										uri);
								ctx.startActivity(intent);
							}
						}).show();
	}
}
