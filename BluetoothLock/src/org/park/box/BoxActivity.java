package org.park.box;

import org.park.R;
import org.park.prefs.PreferenceHelper;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Quit;
import org.park.util.Rotate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class BoxActivity extends Activity implements View.OnClickListener {
	TextView tvTitle;
	public View detail_view;
	public TextView tx_fault;

	public String pair_psw;
	public String mac_addr;
	public String dev_name;
	LockOperation mLockOpr;
	public int box;
	public int cabinet;
	PreferenceHelper mPrefs;
	Rotate mRefresh;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail);

		// initial data
		mPrefs = new PreferenceHelper(this);
		box = mPrefs.getBox();
		cabinet = mPrefs.getCabinet();
		pair_psw = mPrefs.getPsw();
		Bundle bunde = this.getIntent().getExtras();
		if (bunde != null) {
			dev_name = bunde.getString("NAME");
			mac_addr = bunde.getString("MAC");
		}
		detail_view = findViewById(R.id.detail_view);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tx_fault = (TextView) findViewById(R.id.text_hint);
		mRefresh = new Rotate(findViewById(R.id.btn_refresh),
				findViewById(R.id.refresh_view));
		mLockOpr = new LockOperation(this);

		// start
		if (dev_name != null)
			tvTitle.setText(dev_name);
		mRefresh.start();
		mLockOpr.startOpr();
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		mRefresh.stop();
		mLockOpr.disconnected();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mRefresh.stop();
			mLockOpr.disconnected();
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
		case R.id.btn_refresh:
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			mRefresh.start();
			tx_fault.setText(R.string.loading);
			mLockOpr.startOpr();
			break;
		case R.id.btn_box:
			mLockOpr.openLock();
			break;
		case R.id.btn_back:
			mRefresh.stop();
			mLockOpr.disconnected();
			finish();
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_exit:
			mRefresh.stop();
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

	public void setHint(int strRes) {
		tx_fault.setText(strRes);
	}
}