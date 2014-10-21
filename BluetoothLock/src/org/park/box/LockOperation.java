package org.park.box;

import org.park.R;
import org.park.command.LockCommand;
import org.park.connection.Connecter;
import org.park.connection.ContactThread;
import org.park.connection.HandleConnMsg;
import org.park.util.Common;

import android.view.View;

public class LockOperation implements HandleConnMsg {
	BoxActivity mBoxActivity;
	private LockState mLockManager;
	private Connecter mConnecter;
	private LockCommand mLockCmd;
	public ContactThread mConnThr;

	public LockOperation(BoxActivity ctx) {
		mBoxActivity = ctx;
	}

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			mBoxActivity.detail_view.setVisibility(View.VISIBLE);
			mBoxActivity.progress_connect.setVisibility(View.GONE);
			mBoxActivity.setHint(R.string.connect_success);
			mLockManager.set_state(true, false);
			mLockManager.setEnabled(true);
			mConnThr = new ContactThread(mConnecter.btSocket, this);
			mConnThr.start();
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		mLockManager.setEnabled(false);
		mBoxActivity.setBoxVisible(false);
		mBoxActivity.setProgressVisible(false);
		mBoxActivity.tx_fault.setText(R.string.connect_failed);
		if (mConnecter != null)
			mConnecter.onClean();
		if (mConnThr != null)
			mConnThr.onClean();
	}

	@Override
	public void pairing() {
		// TODO Auto-generated method stub
		mBoxActivity.detail_view.setVisibility(View.GONE);
		mBoxActivity.progress_connect.setVisibility(View.VISIBLE);
		mBoxActivity.tx_fault.setText(R.string.pairing);
	}

	@Override
	public void paired(boolean state) {
		// TODO Auto-generated method stub
		if (!state)
			mBoxActivity.setHint(R.string.pair_failed);
	}

	@Override
	public void discovery_started() {
		// TODO Auto-generated method stub
		mBoxActivity.setHint(R.string.searching);
		if (!mConnecter.if_connecting)
			startOpr();
		// mBoxActivity.detail_view.setVisibility(View.GONE);
		// mBoxActivity.progress_connect.setVisibility(View.VISIBLE);
	}

	@Override
	public void discovery_finished() {
		// TODO Auto-generated method stub
		mBoxActivity.setHint(R.string.not_found);
		mBoxActivity.setProgressVisible(false);
	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		// mBoxActivity.tx_fault.setText(received_data);

		if (mLockCmd == null)
			mLockCmd = new LockCommand();
		switch (mLockCmd.checkRecvType(received_data)) {
		case Common.RECEIVE_DYNAMIC_PASSWORD_SUCCESS:
			mConnThr.send(mLockCmd.getOpenLockCommand(
					Common.DEFAULT_PAIR_PASSWORD, received_data));
			break;
		case Common.RECEIVE_DYNAMIC_PASSWORD_FAILED:
			mBoxActivity.tx_fault.setText(R.string.device_return_wrong);
			break;
		case Common.RECEIVE_OPEN_DOOR_SUCCESS:
			mBoxActivity.tx_fault.setText(R.string.open_door_success);
			break;
		case Common.RECEIVE_OPEN_DOOR_FAILED:
			mBoxActivity.tx_fault.setText(R.string.open_door_failed);
			break;
		}
	}

	public void startOpr() {
		// TODO Auto-generated method stub
		if (mLockManager == null) {
			mLockManager = new LockState(mBoxActivity, R.id.btn_box,
					R.id.box_nbr);
			mLockManager.setNbr(mBoxActivity.box);
			mLockManager.cabinet = mBoxActivity.cabinet;
		}
		if (mLockCmd == null)
			mLockCmd = new LockCommand();
		mLockManager.setEnabled(false);

		if (mConnecter == null) {
			mConnecter = new Connecter(this, mBoxActivity);
			mConnecter.setMac(mBoxActivity.mac_addr);
		}
		mConnecter.register(mBoxActivity);
		mConnecter.connect();
	}

	public void openLock() {
		mLockManager.set_state(false, true);
		mConnThr.send(mLockCmd.getPswAlg(
				mBoxActivity.pair_psw.equals("") ? Common.DEFAULT_PAIR_PASSWORD
						: mBoxActivity.pair_psw, mLockManager.cabinet,
				mLockManager.lockNbr));
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
		mLockManager.setEnabled(false);
		mBoxActivity.setBoxVisible(false);
		mBoxActivity.setProgressVisible(false);
		mBoxActivity.tx_fault.setText(R.string.time_out);
		if (mConnecter != null)
			mConnecter.onClean();
		if (mConnThr != null)
			mConnThr.onClean();
	}
}
