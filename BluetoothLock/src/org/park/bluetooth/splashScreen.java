package org.park.bluetooth;

/*
 * ²Î¿¼×ÊÁÏ£ºhttp://www.jnhuamao.cn/bluetooth.pdf
 */

import org.park.authorize.LoginActivity;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;

public class splashScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		if (getIntent().getBooleanExtra(Quit.IS_EXIT, false))
			finish();
		else {
			Thread splashTread = new Thread() {
				@Override
				public void run() {
					try {
						int waited = 0;
						while (waited < 1000) {
							sleep(100);
							waited += 100;
						}
					} catch (InterruptedException e) {
						// do nothing
					} finally {
						SharedPreferences _sharedPreferences = PreferenceManager
								.getDefaultSharedPreferences(getBaseContext());
						String username = _sharedPreferences.getString(
								"locknbr", "");
						if (username.equals(""))
							startActivity(new Intent(splashScreen.this,
									LoginActivity.class));
						else
							startActivity(new Intent(splashScreen.this,
									showDetail.class));
					}
				}
			};
			splashTread.start();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}