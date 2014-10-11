package org.park.bluetooth;

import org.park.util.Quit;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class splashScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		if (getIntent().getBooleanExtra(Quit.IS_EXIT, false)) {
			finish();
			return;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}