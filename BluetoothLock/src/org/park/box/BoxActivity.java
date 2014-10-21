package org.park.box;

import org.park.R;
import org.park.entrance.splashScreen;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BoxActivity extends Activity implements View.OnClickListener {
	TextView tvTitle;
	public LinearLayout detail_view, progress_connect;
	public TextView tx_fault;

	public String pair_psw;
	public String mac_addr;
	public String dev_name;
	LockOperation mLockOpr;
	public int box;
	public int cabinet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail);

		// obtain data
		SharedPreferences _sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		box = Integer.valueOf(_sharedPreferences.getString("locknbr", ""))
				.intValue();
		cabinet = Integer.valueOf(_sharedPreferences.getString("cabinet", ""))
				.intValue();
		pair_psw = _sharedPreferences.getString("password", "");
		Log.i(Common.TAG, "Now password: " + pair_psw);
		Bundle bunde = this.getIntent().getExtras();
		if (bunde != null) {
			dev_name = bunde.getString("NAME");
			mac_addr = bunde.getString("MAC");
		}

		// display
		detail_view = (LinearLayout) findViewById(R.id.detail_view);
		progress_connect = (LinearLayout) findViewById(R.id.progress_connect);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tx_fault = (TextView) findViewById(R.id.text_hint);
		if (dev_name != null)
			tvTitle.setText(dev_name);

		// initail
		mLockOpr = new LockOperation(this);
		mLockOpr.startOpr();
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		mLockOpr.disconnected();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mLockOpr.disconnected();
			startActivity(new Intent(this, splashScreen.class));
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_setting:
			mLockOpr.disconnected();
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			progress_connect.setVisibility(View.VISIBLE);
			tx_fault.setText(R.string.loading);
			mLockOpr.startOpr();
			break;
		case R.id.btn_box:
			mLockOpr.openLock();
			break;
		case R.id.btn_back:
			mLockOpr.disconnected();
			// startActivity(new Intent(this, splashScreen.class));
			finish();
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_exit:
			Quit.act_exit(this);
			break;
		}
	}

	public void setBoxVisible(boolean bl) {
		if (bl)
			detail_view.setVisibility(View.VISIBLE);
		else
			detail_view.setVisibility(View.GONE);
	}

	public void setProgressVisible(boolean bl) {
		if (bl)
			progress_connect.setVisibility(View.VISIBLE);
		else
			progress_connect.setVisibility(View.GONE);
	}

	public void setHint(int strRes) {
		tx_fault.setText(strRes);
	}
}