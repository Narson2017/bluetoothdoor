package org.park.pairpsw;

import org.park.R;
import org.park.command.LockCommand;
import org.park.connection.ConnHandle;
import org.park.connection.Connecter;
import org.park.util.Common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ChangePassword implements ConnHandle {
	AccountActivity ctx;
	Loading mload;
	Connecter mConnecter = null;
	LockCommand mLockcmd = null;

	public ChangePassword(AccountActivity c) {
		mload = new Loading(c);
		ctx = c;
		mConnecter = new Connecter(this, c);
		mLockcmd = new LockCommand();
	}

	public void startUpdate() {
		ctx.set_hint(R.string.loading);
		mload.start();
		if (!mConnecter.if_connected){
			mConnecter.connect();
		} else {
			mConnecter.send(mLockcmd
					.getChangePairPswCmd(ctx.old_psw, ctx.new_psw));
		}
	}

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			ctx.set_hint(R.string.connect_success);
			mConnecter.startCommand();
			mConnecter.send(mLockcmd
					.getChangePairPswCmd(ctx.old_psw, ctx.new_psw));
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub
		if (state)
			ctx.set_hint(R.string.send_success);
		else
			ctx.set_hint(R.string.send_failed);
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		ctx.set_hint(R.string.connect_failed);
		mload.stop();
		if (mConnecter != null)
			mConnecter.onClean();
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
		ctx.set_hint(R.string.searching);
	}

	@Override
	public void discovery_finished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		SharedPreferences mPrefs;
		ctx.set_hint(received_data);

		if (mLockcmd == null)
			mLockcmd = new LockCommand();
		switch (mLockcmd.checkRecvType(received_data)) {
		case Common.RECEIVE_PAIR_PASSWORD_SUCCESS:
			ctx.set_hint(R.string.operate_success);
			mload.stop();
			mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			mPrefs.edit().putString("password", ctx.new_psw).commit();
			break;
		case Common.RECEIVE_PAIR_PASSWORD_FAILED:
			ctx.set_hint(R.string.operate_failed);
			mload.stop();
			break;
		}
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
		mload.stop();
		ctx.set_hint(R.string.time_out);
	}
}