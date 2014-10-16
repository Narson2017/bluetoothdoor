package org.park.entrance;

import org.park.R;
import org.park.authorize.LoginActivity;
import org.park.boxlst.BoxAdapter;
import org.park.boxlst.BoxlstActivity;
import org.park.connection.showDetail;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class splashScreen extends Activity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (getIntent().getBooleanExtra(Quit.IS_EXIT, false))
			finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_login:
			SharedPreferences _sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			String box = _sharedPreferences.getString("locknbr", "");
			if (!box.equals("")) {
				String cabinet = _sharedPreferences.getString("cabinet", "");
				Intent tmp = new Intent(splashScreen.this, showDetail.class);
				tmp.putExtra(BoxAdapter.BOX_NUMBER, Integer.valueOf(box)
						.intValue());
				tmp.putExtra(BoxAdapter.CABINET_NUMBER, Integer
						.valueOf(cabinet).intValue());
				startActivity(tmp);
			} else {
				startActivity(new Intent(splashScreen.this, LoginActivity.class));
			}
			break;
		case R.id.btn_newuser:
			startActivity(new Intent(splashScreen.this, BoxlstActivity.class));
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Quit.act_exit(splashScreen.this);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}