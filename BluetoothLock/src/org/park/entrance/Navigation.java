package org.park.entrance;

import org.park.R;
import org.park.authorize.LoginActivity;
import org.park.box.BoxActivity;
import org.park.boxlst.BoxlstActivity;
import org.park.devlist.DevlstActivity;
import org.park.pairpsw.AccountActivity;
import org.park.prefs.settingActivity;
import org.park.util.Common;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;

public class Navigation extends Activity implements OnClickListener,
		OnLongClickListener {
	Button btn_login, btn_new_user, btn_change_account;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnLongClickListener(this);
		btn_new_user = (Button) findViewById(R.id.btn_new_user);
		btn_new_user.setOnLongClickListener(this);
		btn_change_account = (Button) findViewById(R.id.btn_change_account);
		btn_change_account.setOnLongClickListener(this);
	}

	@Override
	protected void onStart() {
		if (getIntent().getBooleanExtra(Common.IS_EXIT, false))
			finish();
		super.onStart();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (getIntent().getBooleanExtra(Common.IS_EXIT, false))
			finish();
		super.onNewIntent(intent);
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
			startActivity(new Intent(Navigation.this, LoginActivity.class));
			break;
		case R.id.btn_new_user:
			startActivity(new Intent(Navigation.this, BoxlstActivity.class));
			break;
		case R.id.btn_change_account:
			startActivity(new Intent(this, settingActivity.class));
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Quit.act_exit(this);
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_login:
			startActivity(new Intent(this, DevlstActivity.class));
			return true;
		case R.id.btn_new_user:
			startActivity(new Intent(this, BoxActivity.class));
			return true;
		case R.id.btn_change_account:
			startActivity(new Intent(this, AccountActivity.class));
			break;
		}
		return false;
	}
}