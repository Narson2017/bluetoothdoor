package org.park.box;

import org.park.R;
import org.park.command.LockCommand;
import org.park.connection.ConnHandle;
import org.park.connection.Connecter;
import org.park.prefs.PreferenceHelper;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.Quit;
import org.park.util.Rotate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class BoxActivity extends Activity implements View.OnClickListener,
		ConnHandle {
	TextView tvTitle;
	public View detail_view;
	public TextView tx_fault;

	public String pair_psw;
	public String mac_addr;
	public String dev_name;
	public int box;
	public int cabinet;
	PreferenceHelper mPrefs;
	Rotate mRefresh;
	private LockState mLockManager;
	private LockCommand mLockCmd;
	private Connecter mConnecter;

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
		mLockManager = new LockState(this, R.id.btn_box, R.id.box_nbr);
		mLockManager.setNbr(box);
		mLockManager.cabinet = cabinet;
		mLockCmd = new LockCommand();
		mConnecter = new Connecter(this, this);
		mConnecter.setMac(mac_addr);

		// start
		if (dev_name != null)
			tvTitle.setText(dev_name);
		mRefresh.start();
		mLockManager.setEnabled(false);
		mConnecter.register(this);
		mConnecter.connect();
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		mRefresh.stop();
		disconnected();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mRefresh.stop();
			disconnected();
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
			disconnected();
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_refresh:
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			mRefresh.start();
			tx_fault.setText(R.string.loading);
			mLockManager.setEnabled(false);
			mConnecter.register(this);
			mConnecter.connect();
			break;
		case R.id.btn_box:
			mConnecter.send(mLockCmd.getPswAlg(pair_psw, mLockManager.cabinet,
					mLockManager.lockNbr));
			break;
		case R.id.btn_back:
			mRefresh.stop();
			disconnected();
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

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			detail_view.setVisibility(View.VISIBLE);
			mRefresh.display(false);
			setHint(R.string.connect_success);
			mLockManager.set_state(true, false);
			mLockManager.setEnabled(true);
			mConnecter.startCommand();
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		mLockManager.setEnabled(false);
		setBoxVisible(false);
		mRefresh.display(true);
		mRefresh.stop();
		tx_fault.setText(R.string.connect_failed);
		if (mConnecter != null)
			mConnecter.onClean();
	}

	@Override
	public void pairing() {
		// TODO Auto-generated method stub
		tx_fault.setText(R.string.pairing);
	}

	@Override
	public void paired(boolean state) {
		// TODO Auto-generated method stub
		if (!state)
			setHint(R.string.pair_failed);
	}

	@Override
	public void discovery_started() {
		// TODO Auto-generated method stub
		setHint(R.string.searching);
		if (!mConnecter.if_connecting) {
			mLockManager.setEnabled(false);
			mConnecter.register(this);
			mConnecter.connect();
		}
	}

	@Override
	public void discovery_finished() {
		// TODO Auto-generated method stub
		setHint(R.string.not_found);
		mRefresh.stop();
	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		switch (mLockCmd.checkRecvType(received_data)) {
		case Common.RECEIVE_DYNAMIC_PASSWORD_SUCCESS:
			mConnecter.send(mLockCmd.getOpenLockCommand(
					Common.DEFAULT_PAIR_PASSWORD, received_data));
			break;
		case Common.RECEIVE_DYNAMIC_PASSWORD_FAILED:
			tx_fault.setText(R.string.device_return_wrong);
			break;
		case Common.RECEIVE_OPEN_DOOR_SUCCESS:
			tx_fault.setText(R.string.open_door_success);
			mLockManager.set_state(false, true);
			break;
		case Common.RECEIVE_OPEN_DOOR_FAILED:
			tx_fault.setText(R.string.open_door_failed);
			break;
		case Common.RECEIVE_CLOSE_DOOR_SUCCESS:
			tx_fault.setText(R.string.close_door_success);
			mLockManager.set_state(true, true);
			break;
		case Common.RECEIVE_CLOSE_DOOR_FAILED:
			tx_fault.setText(R.string.close_door_failed);
			break;
		}
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
	}

	public void openLock() {
		mLockManager.set_state(false, true);
		mConnecter.send(mLockCmd.getPswAlg(pair_psw, mLockManager.cabinet,
				mLockManager.lockNbr));
	}
}