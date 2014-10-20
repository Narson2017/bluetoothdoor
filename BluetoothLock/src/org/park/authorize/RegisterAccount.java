package org.park.authorize;

import org.park.R;
import org.park.box.BoxActivity;
import org.park.command.LockCommand;
import org.park.connection.Connecter;
import org.park.connection.ContactThread;
import org.park.connection.HandleConnMsg;
import org.park.util.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class RegisterAccount implements HandleConnMsg {

	LoginActivity mLoginActivity;
	Connecter mConnecter = null;
	ContactThread mConnThr = null;
	LockCommand mLockcmd = null;
	OprLoad mLoad;

	public RegisterAccount(LoginActivity mLoginActivity) {
		mConnecter = new Connecter(this, mLoginActivity);
		this.mLoginActivity = mLoginActivity;
		mLockcmd = new LockCommand();
		mLoad = new OprLoad(Common.MSG_REGISTER_LOADING, mLoginActivity);
	}

	public void register() {
		mLoad.start();
		mLoginActivity.text_login_hint.setText(R.string.loading);
		if (!mConnecter.if_connected)
			mConnecter.connect();
		else if (mConnThr == null || !mConnThr.if_connected) {
			connected(true);
		} else {
			mConnThr.send(mLockcmd.getChangePairPswCmd(mLoginActivity.old_psw,
					mLoginActivity.new_psw, mLoginActivity.cabinet,
					mLoginActivity.box));
		}
	}

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			mLoginActivity.text_login_hint.setText(R.string.loading);
			mConnThr = new ContactThread(mConnecter.btSocket, this);
			mConnThr.start();
			mConnThr.send(mLockcmd.getChangePairPswCmd(mLoginActivity.old_psw,
					mLoginActivity.new_psw, mLoginActivity.cabinet,
					mLoginActivity.box));
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub
		if (state)
			mLoginActivity.text_login_hint.setText(R.string.send_success);
		else
			mLoginActivity.text_login_hint.setText(R.string.send_failed);
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		mLoginActivity.text_login_hint.setText(R.string.connect_failed);
		mLoad.stop();
		if (mConnecter != null)
			mConnecter.onClean();
		if (mConnThr != null)
			mConnThr.onClean();
	}

	@Override
	public void pairing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void paired(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void discovery_started() {
		// TODO Auto-generated method stub
		mLoginActivity.text_login_hint.setText(R.string.searching);
	}

	@Override
	public void discovery_finished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		SharedPreferences mPrefs;
		mLoginActivity.text_login_hint.setText(received_data);

		if (mLockcmd == null)
			mLockcmd = new LockCommand();
		switch (mLockcmd.checkRecvType(received_data)) {
		case Common.RECEIVE_PAIR_PASSWORD_SUCCESS:
			mLoginActivity.text_login_hint.setText(R.string.operate_success);
			mLoad.stop();
			mPrefs = PreferenceManager
					.getDefaultSharedPreferences(mLoginActivity);
			mPrefs.edit().putString("username", mLoginActivity.new_username)
					.commit();
			mPrefs.edit().putString("password", mLoginActivity.new_psw)
					.commit();
			mPrefs.edit()
					.putString("locknbr", String.valueOf(mLoginActivity.box))
					.commit();
			mPrefs.edit()
					.putString("cabinet",
							String.valueOf(mLoginActivity.cabinet)).commit();
			mLoginActivity.startActivity(new Intent(mLoginActivity,
					BoxActivity.class));
			disconnected();
			mLoginActivity.finish();
			break;
		case Common.RECEIVE_PAIR_PASSWORD_FAILED:
			mLoginActivity.text_login_hint.setText(R.string.operate_failed);
			break;
		}
	}

}