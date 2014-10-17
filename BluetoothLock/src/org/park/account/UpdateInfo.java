package org.park.account;

import org.park.R;
import org.park.command.LockCommand;
import org.park.connection.ConnectCtrl;
import org.park.connection.ConnectedThread;
import org.park.connection.HandleConnMsg;
import org.park.entrance.splashScreen;
import org.park.util.Common;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

public class UpdateInfo implements HandleConnMsg {
	String new_username, new_psw, old_psw;
	int cabinet, box;
	AccountActivity ctx;
	Loading mload;
	ConnectCtrl mBtMgr = null;
	ConnectedThread mConnThr = null;
	LockCommand mLockcmd = null;

	public UpdateInfo(AccountActivity c, Loading mload) {
		this.mload = mload;
		ctx = c;
		mBtMgr = new ConnectCtrl(this);
		mLockcmd = new LockCommand();
	}

	public void set_username(String username) {
		this.new_username = username;
	}

	public void set_account(String new_username, String old_psw,
			String new_psw, int cabinet, int box) {
		this.new_username = new_username;
		this.old_psw = old_psw;
		this.new_psw = old_psw;
		this.cabinet = cabinet;
		this.box = box;
	}

	public void set_psw(String new_psw, String old_psw) {
		this.new_psw = new_psw;
		this.old_psw = old_psw;
	}

	public void update_info() {
		ctx.set_hint(R.string.loading);
		if (!mBtMgr.if_connected)
			mBtMgr.connect();
		else if (mConnThr == null || !mConnThr.if_connected) {
			connected(true);
		} else {
			mConnThr.send(mLockcmd.getChangePairPswCmd(old_psw, new_psw,
					cabinet, box));
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(15000);
					mHandler.obtainMessage(Common.MESSAGE_HINT,
							R.string.change_account_success, -1).sendToTarget();
					Thread.sleep(3000);
					mload.stop();
					mHandler.sendEmptyMessage(Common.MSG_UPDATE_SUCCESS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}).start();
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
				ctx.startActivity(new Intent(ctx, splashScreen.class));
				break;
			}
		}
	};

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		mConnThr = new ConnectedThread(mBtMgr.btSocket, this);
		mConnThr.send(mLockcmd.getChangePairPswCmd(old_psw, new_psw, cabinet,
				box));
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
	public void discovery_stated() {
		// TODO Auto-generated method stub

	}

	@Override
	public void discovery_finished() {
		// TODO Auto-generated method stub

	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		ctx.set_hint(received_data);

		if (mLockcmd == null)
			mLockcmd = new LockCommand();
		switch (mLockcmd.checkRecvType(received_data)) {
		case Common.RECEIVE_PAIR_PASSWORD_SUCCESS:
			ctx.set_hint(R.string.send_success);
			SharedPreferences mPrefs = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			mPrefs.edit().putString("username", new_username).commit();
			mPrefs.edit().putString("password", new_psw).commit();
			mPrefs.edit().putString("locknbr", String.valueOf(box)).commit();
			mPrefs.edit().putString("cabinet", String.valueOf(cabinet))
					.commit();
			break;
		case Common.RECEIVE_PAIR_PASSWORD_FAILED:
			ctx.set_hint(R.string.send_failed);
			mload.stop();
			break;
		}
	}
}