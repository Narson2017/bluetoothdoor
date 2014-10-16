package org.park.account;

import org.park.R;
import org.park.connection.ConnectCtrl;
import org.park.connection.HandleConnMsg;
import org.park.entrance.splashScreen;
import org.park.util.Common;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public class UpdateInfo implements HandleConnMsg {
	String username, password, old_psw;
	AccountActivity ctx;
	Loading mload;
	ConnectCtrl mBtMgr = null;

	public UpdateInfo(AccountActivity c, Loading mload) {
		this.mload = mload;
		ctx = c;
		mBtMgr = new ConnectCtrl(this);
	}

	public void set_username(String username) {
		this.username = username;
	}

	public void set_psw(String new_psw, String old_psw) {
		this.password = new_psw;
		this.old_psw = old_psw;
	}

	public void update_info() {
		ctx.set_hint(R.string.loading);
		mBtMgr.connect();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(15000);
					mHandler.obtainMessage(Common.MESSAGE_HINT,
							R.string.change_account_success, -1).sendToTarget();
					Thread.sleep(10000);
					mHandler.obtainMessage(Common.MESSAGE_HINT,
							R.string.change_psw_success, -1).sendToTarget();
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
	public void connect_state(boolean state) {
		// TODO Auto-generated method stub
		mBtMgr.send(old_psw, password);
	}

	@Override
	public void send_state(boolean state) {
		// TODO Auto-generated method stub
		ctx.set_hint(R.string.change_psw_success);
	}
}
