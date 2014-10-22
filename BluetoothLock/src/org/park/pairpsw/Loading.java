package org.park.pairpsw;

import org.park.R;
import org.park.util.Common;

import android.os.Handler;
import android.os.Message;

class Loading implements Runnable {
	public boolean loading;
	AccountActivity ctx;
	int[] loding_txs = { R.string.authorize_update1,
			R.string.authorize_update2, R.string.authorize_update3,
			R.string.authorize_update4 };

	public Loading(AccountActivity c) {
		super();
		loading = true;
		ctx = c;
	}

	public void stop() {
		loading = false;
	}

	public void start() {
		loading = true;
		new Thread(this).start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i = 0;
		while (loading) {
			mHandler.obtainMessage(Common.MSG_LOADING,
					loding_txs[(i++) % loding_txs.length], -1).sendToTarget();
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
		mHandler.obtainMessage(Common.MSG_LOADING, R.string.authorize_update,
				-1).sendToTarget();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MSG_LOADING:
				ctx.set_btn_text(msg.arg1);
				break;
			case Common.MSG_SERVER_FAULT:
				ctx.set_hint(R.string.time_out);
				break;
			}
		}
	};
}
