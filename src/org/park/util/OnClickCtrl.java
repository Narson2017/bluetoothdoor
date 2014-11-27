package org.park.util;

import org.park.R;
import org.park.prefs.SettingActivity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class OnClickCtrl implements OnClickListener {
	private Activity ctx;

	public OnClickCtrl(Activity c) {
		super();
		ctx = c;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_exit:
			Quit.quit(ctx);
			break;
		case R.id.btn_about:
			About.ShowAbout(ctx);
			break;
		case R.id.btn_action_back:
		case R.id.btn_back:
			// ctx.startActivity(new Intent(ctx, splashScreen.class));
			ctx.finish();
			break;
		case R.id.btn_setting:
			ctx.startActivity(new Intent(ctx, SettingActivity.class));
			break;
		}
	}

}
