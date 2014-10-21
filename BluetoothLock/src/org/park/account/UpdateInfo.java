package org.park.account;

import org.park.R;
import org.park.command.LockCommand;
import org.park.connection.Connecter;
import org.park.connection.ContactThread;
import org.park.connection.HandleConnMsg;
import org.park.entrance.Navigation;
import org.park.util.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class UpdateInfo implements HandleConnMsg {
	AccountActivity ctx;
	Loading mload;
	Connecter mConnecter = null;
	ContactThread mConnThr = null;
	LockCommand mLockcmd = null;

	public UpdateInfo(AccountActivity c) {
		mload = new Loading(c);
		ctx = c;
		mConnecter = new Connecter(this, c);
		mLockcmd = new LockCommand();
	}

	public void startUpdate() {
		ctx.set_hint(R.string.loading);
		mload.start();
		if (!mConnecter.if_connected)
			mConnecter.connect();
		else if (mConnThr == null || !mConnThr.if_connected) {
			connected(true);
		} else {
			mConnThr.send(mLockcmd.getChangePairPswCmd(ctx.old_psw,
					ctx.new_psw, ctx.cabinet, ctx.box));
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_HINT:
				ctx.set_hint(msg.arg1);
				break;
			case Common.MSG_UPDATE_SUCCESS:
				ctx.set_hint(R.string.update_success);
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(3000);
							mHandler.sendEmptyMessage(Common.MSG_RETURN_INDEX);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				break;
			case Common.MSG_RETURN_INDEX:
				ctx.set_hint(R.string.back);
				ctx.startActivity(new Intent(ctx, Navigation.class));
				break;
			}
		}
	};

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			ctx.set_hint(R.string.connect_success);
			mConnThr = new ContactThread(mConnecter.btSocket, this);
			mConnThr.start();
			mConnThr.send(mLockcmd.getChangePairPswCmd(ctx.old_psw,
					ctx.new_psw, ctx.cabinet, ctx.box));
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
			mPrefs.edit().putString("username", ctx.new_username).commit();
			mPrefs.edit().putString("password", ctx.new_psw).commit();
			mPrefs.edit().putString("locknbr", String.valueOf(ctx.box))
					.commit();
			mPrefs.edit().putString("cabinet", String.valueOf(ctx.cabinet))
					.commit();
			mHandler.sendEmptyMessage(Common.MSG_UPDATE_SUCCESS);
			break;
		case Common.RECEIVE_PAIR_PASSWORD_FAILED:
			ctx.set_hint(R.string.operate_failed);
			mload.stop();
			mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			mPrefs.edit().putString("username", ctx.old_username).commit();
			mPrefs.edit().putString("password", ctx.old_psw).commit();
			mPrefs.edit().putString("locknbr", String.valueOf(ctx.box))
					.commit();
			mPrefs.edit().putString("cabinet", String.valueOf(ctx.cabinet))
					.commit();
			break;
		}
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub

	}
}