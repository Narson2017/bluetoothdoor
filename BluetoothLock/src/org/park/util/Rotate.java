package org.park.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;

@SuppressLint("NewApi")
public class Rotate implements Runnable {
	View rotateView, container;
	boolean rotating;

	public Rotate(View v, View container) {
		rotateView = v;
		this.container = container;
	}

	public void start() {
		rotating = true;
		container.setVisibility(View.VISIBLE);
		new Thread(this).start();
	}

	public void display(boolean bl) {
		if (bl) {
			container.setVisibility(View.VISIBLE);
		} else {
			container.setVisibility(View.GONE);
			stop();
		}
	}

	public void stop() {
		rotating = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		float rotation = 0;
		float speed_up = (float) 0.01;
		while (rotating) {
			mHandle.obtainMessage(Common.MSG_LOADING, rotation).sendToTarget();
			rotation += (Common.ROTATE_STEP + speed_up++);
			try {
				Thread.sleep(Common.ROTATE_DELAY_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	Handler mHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MSG_LOADING:
				rotateView.setRotation((Float) msg.obj % 360);
				break;
			}
		}
	};
}
