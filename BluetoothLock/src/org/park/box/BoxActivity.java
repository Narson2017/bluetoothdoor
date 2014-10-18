package org.park.box;

import org.park.R;
import org.park.boxlst.BoxAdapter;
import org.park.command.LockCommand;
import org.park.connection.Connecter;
import org.park.connection.ContactThread;
import org.park.connection.HandleConnMsg;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BoxActivity extends Activity implements View.OnClickListener,
		HandleConnMsg {
	TextView tvTitle;
	public LinearLayout detail_view, progress_connect;
	public TextView tx_fault;

	private LockDisplay mLockManager;
	private Connecter mBtMgr;
	private LockCommand mCmdmgr;
	public ContactThread connThr;
	private String pair_psw = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail);

		mLockManager = new LockDisplay(this, R.id.btn_box, R.id.box_nbr);
		mLockManager.setNbr(getIntent().getIntExtra(BoxAdapter.BOX_NUMBER, -1));
		mLockManager.cabinet = getIntent().getIntExtra(
				BoxAdapter.CABINET_NUMBER, -1);

		detail_view = (LinearLayout) findViewById(R.id.detail_view);
		progress_connect = (LinearLayout) findViewById(R.id.progress_connect);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tx_fault = (TextView) findViewById(R.id.text_hint);
		mLockManager.setEnabled(false);

		SharedPreferences _sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		pair_psw = _sharedPreferences.getString("password", "");
		mBtMgr = new Connecter(this, this);
		Bundle bunde = this.getIntent().getExtras();
		if (bunde != null) {
			tvTitle.setText(bunde.getString("NAME"));
			mBtMgr.setMac(bunde.getString("MAC"));
		}
		mBtMgr.connect();
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		if (connThr != null)
			connThr.onClean();
		if (mBtMgr != null) {
			mBtMgr.onClean();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (connThr != null)
				connThr.onClean();
			if (mBtMgr != null)
				mBtMgr.onClean();
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
			if (connThr != null)
				connThr.onClean();
			if (mBtMgr != null)
				mBtMgr.onClean();
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			progress_connect.setVisibility(View.VISIBLE);
			tx_fault.setText(R.string.loading);
			mBtMgr.connect();
			break;
		case R.id.btn_box:
			mLockManager.set_state(false, true);
			connThr.send(mCmdmgr.getPswAlg(
					pair_psw.equals("") ? Common.DEFAULT_PAIR_PASSWORD
							: pair_psw, mLockManager.cabinet,
					mLockManager.lockNbr));
			break;
		case R.id.btn_back:
			if (connThr != null)
				connThr.onClean();
			if (mBtMgr != null)
				mBtMgr.onClean();
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

	public void startConnThr() {
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

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			detail_view.setVisibility(View.VISIBLE);
			progress_connect.setVisibility(View.GONE);
			setHint(R.string.connect_success);
			setBoxState(true, true);
			setBoxEnable(true);
			connThr = new ContactThread(mBtMgr.btSocket, this);
			startConnThr();
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		mBtMgr.unpair();
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

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		tx_fault.setText(received_data);

		if (mCmdmgr == null)
			mCmdmgr = new LockCommand();
		switch (mCmdmgr.checkRecvType(received_data)) {
		case Common.RECEIVE_DYNAMIC_PASSWORD_SUCCESS:
			connThr.send(mCmdmgr.getOpenLockCommand(
					Common.DEFAULT_PAIR_PASSWORD, received_data));
			break;
		case Common.RECEIVE_DYNAMIC_PASSWORD_FAILED:
			tx_fault.setText(R.string.device_return_wrong);
			break;
		}
	}
}