package org.park.connection;

import org.park.R;
import org.park.authorize.LoginActivity;
import org.park.boxlst.BoxAdapter;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Quit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class showDetail extends Activity implements View.OnClickListener {
	public static final String OPERATION = "OPERATION";
	public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	public static String PAIR_PASSWORD = "000000000000";
	TextView tvTitle;

	public ConnectedThread connThr;

	public LinearLayout detail_view, progress_connect;
	public TextView tx_fault;

	private LockManager mLockManager;
	private ConnectCtrl mBtMgr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail);

		mLockManager = new LockManager(this, R.id.btn_box, R.id.box_nbr);
		mLockManager.setNbr(getIntent().getIntExtra(BoxAdapter.BOX_NUMBER, -1));

		detail_view = (LinearLayout) findViewById(R.id.detail_view);
		progress_connect = (LinearLayout) findViewById(R.id.progress_connect);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tx_fault = (TextView) findViewById(R.id.text_hint);

		mBtMgr = new ConnectCtrl(this);
		if (mBtMgr.btAdapt == null)
			return;

		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intent.addAction(BluetoothDevice.ACTION_FOUND);
		intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mBtMgr, intent);
		mLockManager.setEnabled(false);

		Bundle bunde = this.getIntent().getExtras();
		if (bunde != null) {
			tvTitle.setText(bunde.getString("NAME"));
			mBtMgr.setMac(bunde.getString("MAC"));
		}
		mBtMgr.findDev();
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBtMgr);
		connThr.act_clean();
		mBtMgr.disable_bluetooth();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			connThr.act_clean();
			mBtMgr.disable_bluetooth();
			startActivity(new Intent(this, LoginActivity.class));
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
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			progress_connect.setVisibility(View.VISIBLE);
			tx_fault.setText(R.string.loading);
			mBtMgr.findDev();
			break;
		case R.id.btn_box:
			mLockManager.set_state(false, false);
			connThr.openlock(1, mLockManager.getNbr());
			break;
		case R.id.btn_back:
			connThr.act_clean();
			mBtMgr.disable_bluetooth();
			startActivity(new Intent(this, LoginActivity.class));
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

	public void setConn(ConnectedThread ct) {
		connThr = ct;
	}

	public void startConn() {
		if (connThr != null) {
			connThr.start();
			// connThr.startQuery();
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

	public int getBoxnbr() {
		return mLockManager.getNbr() - 1;
	}

	public void setBoxState(boolean is_lock, boolean is_empty) {
		mLockManager.set_state(is_lock, is_empty);
	}

	public void setBoxEnable(boolean bl) {
		mLockManager.setEnabled(bl);
	}

	public void unpair() {
		mBtMgr.unpair();
	}
}