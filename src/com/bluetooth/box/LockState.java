package com.bluetooth.box;

import org.park.R;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LockState {
	int lockState;
	Button boxBtn;
	TextView nbrText;
	int lockNbr;
	public int cabinet;

	public LockState(Context ctx, int btn_res, int tx_res) {
		Activity act = (Activity) ctx;
		View.OnClickListener mListener = (View.OnClickListener) ctx;
		boxBtn = (Button) act.findViewById(btn_res);
		boxBtn.setEnabled(false);
		boxBtn.setOnClickListener(mListener);
		nbrText = (TextView) act.findViewById(tx_res);
		lockState = R.drawable.btn_close_empty;
	}

	public void setEnabled(boolean bl) {
		boxBtn.setEnabled(bl);
		if (bl) {
			boxBtn.setBackgroundResource(lockState);
		} else {
			boxBtn.setBackgroundResource(R.drawable.ic_lock_grey);
		}
	}

	public void set_state(boolean is_lock, boolean is_empty) {
		if (is_lock)
			if (is_empty) {
				boxBtn.setBackgroundResource(R.drawable.btn_close_empty);
			} else {
				boxBtn.setBackgroundResource(R.drawable.btn_close_filled);
			}
		else {
			if (is_empty) {
				boxBtn.setBackgroundResource(R.drawable.btn_open_empty);
			} else {
				boxBtn.setBackgroundResource(R.drawable.btn_open_filled);
			}
		}
	}

	public void setNbr(int lockNbr) {
		nbrText.setText(String.valueOf(lockNbr));
		this.lockNbr = lockNbr;
	}

	public int getNbr() {
		return lockNbr;
	}
}