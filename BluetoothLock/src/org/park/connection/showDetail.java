package org.park.connection;

import org.park.R;
import org.park.boxlst.BoxAdapter;
import org.park.entrance.splashScreen;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class showDetail extends Activity implements View.OnClickListener,
		HandleConnMsg {
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
		mLockManager.cabinet = getIntent().getIntExtra(
				BoxAdapter.CABINET_NUMBER, -1);

		detail_view = (LinearLayout) findViewById(R.id.detail_view);
		progress_connect = (LinearLayout) findViewById(R.id.progress_connect);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tx_fault = (TextView) findViewById(R.id.text_hint);
		mLockManager.setEnabled(false);

		mBtMgr = new ConnectCtrl(this);
		Bundle bunde = this.getIntent().getExtras();
		if (bunde != null) {
			tvTitle.setText(bunde.getString("NAME"));
			mBtMgr.setMac(bunde.getString("MAC"));
		}
		if (mBtMgr.btAdapt == null) {
			tx_fault.setText(R.string.blue_unabailable);
			return;
		}
		mBtMgr.connect();
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		if (connThr != null)
			connThr.act_clean();
		if (mBtMgr != null) {
			mBtMgr.disable_bluetooth();
			mBtMgr.unregister();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (connThr != null)
				connThr.act_clean();
			if (mBtMgr != null)
				mBtMgr.disable_bluetooth();
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
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			progress_connect.setVisibility(View.VISIBLE);
			tx_fault.setText(R.string.loading);
			mBtMgr.connect();
			break;
		case R.id.btn_box:
			mLockManager.set_state(false, false);
			connThr.openlock(1, mLockManager.getNbr());
			break;
		case R.id.btn_back:
			if (connThr != null)
				connThr.act_clean();
			if (mBtMgr != null)
				mBtMgr.disable_bluetooth();
			startActivity(new Intent(this, splashScreen.class));
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

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			detail_view.setVisibility(View.VISIBLE);
			progress_connect.setVisibility(View.GONE);
			setHint(R.string.connect_success);
			setBoxState(true, true);
			setBoxEnable(true);
			setConn(new ConnectedThread(mBtMgr.btSocket, this));
			startConn();
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		setBoxEnable(false);
		setBoxVisible(false);
		setProgressVisible(false);
		setHint(R.string.connect_failed);
	}

	@Override
	public void pairing() {
		// TODO Auto-generated method stub
		detail_view.setVisibility(View.GONE);
		progress_connect.setVisibility(View.VISIBLE);
		tx_fault.setText(R.string.pairing);
	}

	@Override
	public void paired(boolean state) {
		// TODO Auto-generated method stub
		if (!state)
			setHint(R.string.pair_failed);
	}

	@Override
	public void discovery_stated() {
		// TODO Auto-generated method stub
		setHint(R.string.searching);		
	}

	@Override
	public void discovery_finished() {
		// TODO Auto-generated method stub
		setHint(R.string.not_found);
		setProgressVisible(false);		
	}
}