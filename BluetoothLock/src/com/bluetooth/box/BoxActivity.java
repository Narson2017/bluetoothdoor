package com.bluetooth.box;

import org.park.R;
import org.park.entrance.NavigateActivity;
import org.park.prefs.PreferenceHelper;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.Quit;
import org.park.util.Rotate;

import com.bluetooth.connection.ConnHandle;
import com.bluetooth.connection.Connecter;
import com.bluetooth.connection.LockCommand;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
	private boolean if_exit;
	private Button btn_connect, btn_refresh;

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
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_refresh = (Button) findViewById(R.id.btn_refresh);
		mRefresh = new Rotate(btn_refresh, findViewById(R.id.refresh_view));
		mLockManager = new LockState(this, R.id.btn_box, R.id.box_nbr);
		mLockManager.setNbr(box);
		mLockManager.cabinet = cabinet;
		mLockCmd = new LockCommand();
		mLockCmd.setBoxNbr(cabinet, box);
		mConnecter = new Connecter(this, this);
		mConnecter.setMac(mac_addr);
		if_exit = false;

		// start
		btn_refresh.setEnabled(false);
		btn_connect.setEnabled(false);
		if (dev_name != null)
			tvTitle.setText(dev_name);
		mRefresh.start();
		mLockManager.setEnabled(false);
		mConnecter.register(this);
		mConnecter.start();
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		if_exit = true;
		mRefresh.stop();
		disconnected();
		mConnecter.clean();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mConnecter.clean();
			if_exit = true;
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
			// disconnected();
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_refresh:
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			mRefresh.start();
			tx_fault.setText(R.string.loading);
			mLockManager.setEnabled(false);
			mConnecter.register(this);
			mConnecter.start();
			btn_refresh.setEnabled(false);
			btn_connect.setEnabled(false);
			break;
		case R.id.btn_box:
			mConnecter.send(mLockCmd.getPswAlg(pair_psw, mLockManager.cabinet,
					mLockManager.lockNbr));
			break;
		case R.id.btn_back:
			mConnecter.clean();
			if_exit = true;
			mRefresh.stop();
			if (!mConnecter.if_receiving)
				finish();
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_exit:
			mConnecter.clean();
			if_exit = true;
			mRefresh.stop();
			if (!mConnecter.if_receiving)
				Quit.quit(this);
			break;
		}
	}

	public void setBoxVisible(boolean bl) {
		if (bl)
			detail_view.setVisibility(View.VISIBLE);
		else
			detail_view.setVisibility(View.GONE);
	}

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			new Thread(timeLimit).start();
			detail_view.setVisibility(View.VISIBLE);
			mRefresh.display(false);
			tx_fault.setText(R.string.connect_success);
			mLockManager.set_state(true, false);
			mLockManager.setEnabled(true);
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
		tx_fault.setText(R.string.connect_failed);
		mRefresh.stop();
		btn_refresh.setEnabled(true);
		btn_connect.setEnabled(true);
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
			tx_fault.setText(R.string.pair_failed);
		else
			tx_fault.setText(R.string.pair_success);
	}

	@Override
	public void searching() {
		// TODO Auto-generated method stub
		tx_fault.setText(R.string.searching);
		if (!mConnecter.if_connecting) {
			mLockManager.setEnabled(false);
			mRefresh.start();
		}
	}

	@Override
	public void searched() {
		// TODO Auto-generated method stub
		tx_fault.setText(R.string.search_done);
	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		if (received_data != null) {
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
		} else
			tx_fault.setText(R.string.receive_failed);
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
	}

	private Runnable timeLimit = new Runnable() {
		@Override
		public void run() {
			int count = 0;
			while (!if_exit) {
				try {
					Thread.sleep(1024);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (count++ > Common.LIMITE_SECOND) {
					mHandle.sendEmptyMessage(Common.TIME_OUT);
					break;
				}
			}
		}
	};
	private Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.TIME_OUT:
				disconnected();
				startActivity(new Intent(BoxActivity.this,
						NavigateActivity.class));
				finish();
				break;
			}
		}
	};

	@Override
	public void found(boolean state) {
		// TODO Auto-generated method stub
		if (state)
			tx_fault.setText(R.string.found);
		else {
			mRefresh.stop();
			btn_refresh.setEnabled(true);
			btn_connect.setEnabled(true);
			tx_fault.setText(R.string.not_found);
		}
	}

	@Override
	public void disconnecting() {
		// TODO Auto-generated method stub
		tx_fault.setText(R.string.disconnecting);
	}
}