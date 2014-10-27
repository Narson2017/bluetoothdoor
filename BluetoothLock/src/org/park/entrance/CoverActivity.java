package org.park.entrance;

import org.park.R;
import org.park.util.Common;
import org.park.util.Quit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;

public class CoverActivity extends Activity {
	BluetoothAdapter btAdapt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cover);
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (!btAdapt.isEnabled())
						btAdapt.enable();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// wait for opening bluetooth
				try {
					Thread.sleep(2048);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				startActivity(new Intent(CoverActivity.this,
						NavigateActivity.class));
			}

		}).start();
	}

	@Override
	protected void onStart() {
		if (getIntent().getBooleanExtra(Common.IS_EXIT, false)) {
			// stop connection
			finish();
		}
		super.onStart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (getIntent().getBooleanExtra(Common.IS_EXIT, false)) {
			finish();
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Quit.quit(this);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
