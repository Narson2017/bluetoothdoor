package com.bluetooth.authorize;

import org.park.R;
import org.park.util.Common;

import android.os.Handler;
import android.os.Message;

class Loading implements Runnable {
	int operation;
	boolean authorizing;
	LoginActivity mLoginActivity;

	int[] loginTexts = { R.string.login_load1, R.string.login_load2,
			R.string.login_load3 };
	int[] registerTexts = { R.string.register_load1, R.string.register_load2,
			R.string.register_load3 };

	public Loading(LoginActivity ctx) {
		operation = Common.MSG_LOGIN_LOADING;
		mLoginActivity = ctx;
	}

	public void stop() {
		authorizing = false;
	}

	public void start() {
		authorizing = true;
		new Thread(this).start();
	}

	public void setOperation(int opr) {
		operation = opr;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i = 0;
		while (authorizing) {
			switch (operation) {
			case Common.MSG_LOGIN_LOADING:
				mHandler.obtainMessage(Common.MSG_LOGIN_LOADING,
						loginTexts[(i++) % loginTexts.length], -1)
						.sendToTarget();
				break;
			case Common.MSG_REGISTER_LOADING:
				mHandler.obtainMessage(Common.MSG_REGISTER_LOADING,
						registerTexts[(i++) % registerTexts.length], -1)
						.sendToTarget();
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (i == 60) {
				mHandler.sendEmptyMessage(Common.MSG_SERVER_FAULT);
				break;
			}
		}
		mHandler.obtainMessage(Common.MSG_LOGIN_LOADING, R.string.login, -1)
				.sendToTarget();
		mHandler.obtainMessage(Common.MSG_REGISTER_LOADING, R.string.register,
				-1).sendToTarget();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MSG_LOGIN_LOADING:
				mLoginActivity.btn_login.setText(msg.arg1);
				break;
			case Common.MSG_REGISTER_LOADING:
				mLoginActivity.btn_register.setText(msg.arg1);
				break;
			case Common.MSG_SERVER_FAULT:
				mLoginActivity.hint(R.string.time_out);
				authorizing = false;
				break;
			}
		}
	};
}