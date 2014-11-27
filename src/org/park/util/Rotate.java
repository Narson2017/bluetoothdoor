package org.park.util;

import org.park.R;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Rotate {
	View rotateView, container;
	Context ctx;
	boolean rotating;
	Animation rotate;

	public Rotate(View v, View container, Context c) {
		rotate = AnimationUtils.loadAnimation(c, R.anim.rotate);
		rotateView = v;
		this.container = container;
	}

	public void start() {
		if (!rotating) {
			rotating = true;
			container.setVisibility(View.VISIBLE);
			rotateView.startAnimation(rotate);
		}
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
		rotateView.clearAnimation();
	}
}
