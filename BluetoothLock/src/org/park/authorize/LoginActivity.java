package org.park.authorize;

import org.park.R;
import org.park.box.BoxActivity;
import org.park.boxlst.BoxAdapter;
import org.park.devlist.DevlstActivity;
import org.park.entrance.splashScreen;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends Activity implements
		View.OnFocusChangeListener, OnClickListener, OnLongClickListener {
	EditText edit_psw, edit_username;
	ImageView img_psw, img_username;
	Button btn_login, btn_register;
	TextView text_login_hint;

	AuthenticationManager mAuthMgr;
	RegisterAccount mRegister;
	public int box, cabinet;
	public String old_psw, new_psw, new_username;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra(Quit.IS_EXIT, false))
			finish();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		// display
		edit_psw = (EditText) findViewById(R.id.edit_psw);
		edit_psw.setOnFocusChangeListener(this);
		edit_username = (EditText) findViewById(R.id.edit_username);
		edit_username.setOnFocusChangeListener(this);
		img_psw = (ImageView) findViewById(R.id.img_psw);
		img_username = (ImageView) findViewById(R.id.img_username);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnClickListener(this);
		btn_login.setOnLongClickListener(this);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_register.setOnClickListener(this);
		btn_register.setOnLongClickListener(this);
		text_login_hint = (TextView) findViewById(R.id.text_login_hint);

		// initialize data
		old_psw = Common.DEFAULT_PAIR_PASSWORD;
		box = getIntent().getIntExtra(BoxAdapter.BOX_NUMBER, -1);
		cabinet = getIntent().getIntExtra(BoxAdapter.CABINET_NUMBER, -1);
		if (box != -1) {
			btn_login.setEnabled(false);
			btn_register.setEnabled(true);
			btn_login.setBackgroundColor(getResources().getColor(
					R.color.trolley_grey));
			text_login_hint.setText(R.string.not_register);
		} else {
			btn_register.setEnabled(false);
			btn_login.setEnabled(true);
			btn_register.setBackgroundColor(getResources().getColor(
					R.color.trolley_grey));
			text_login_hint.setText(R.string.please_login);
		}
		mAuthMgr = new AuthenticationManager(this);
		mRegister = new RegisterAccount(this);
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if (arg0 == edit_psw) {
			if (arg1)
				img_psw.setImageResource(R.drawable.ic_device_access_accounts_focused);
			else
				img_psw.setImageResource(R.drawable.ic_device_access_accounts);
		} else if (arg0 == edit_username) {
			if (arg1)
				img_username
						.setImageResource(R.drawable.ic_device_access_call_focused);
			else
				img_username.setImageResource(R.drawable.ic_device_access_call);
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_login:
			try {
				// btn_login.setText(R.string.logining);
				mAuthMgr.login(edit_username.getText().toString(), edit_psw
						.getText().toString());
				// new Thread(new OprLoad(MSG_LOGIN_LOADING)).start();
			} catch (Exception e) {
				System.err.print("Begin authorize failed: " + e.toString());
			}
			break;
		case R.id.btn_register:
			new_psw = edit_psw.getText().toString();
			new_username = edit_username.getText().toString();
			mRegister.register();
			break;
		case R.id.btn_back:
			startActivity(new Intent(this, splashScreen.class));
			finish();
			break;
		case R.id.btn_exit:
			Quit.act_exit(this);
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_setting:
			startActivity(new Intent(this, settingActivity.class));
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(this, splashScreen.class));
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void authPassed() {
		// TODO Auto-generated method stub
		text_login_hint.setText(R.string.auth_success);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefs.edit().putString("username", edit_username.getText().toString())
				.commit();
		prefs.edit().putString("password", edit_psw.getText().toString())
				.commit();
		prefs.edit().putString("locknbr", String.valueOf(box)).commit();
		prefs.edit().putString("cabinet", String.valueOf(cabinet)).commit();

		Intent intent = new Intent(this, BoxActivity.class);
		intent.putExtra(BoxAdapter.CABINET_NUMBER, cabinet);
		intent.putExtra(BoxAdapter.BOX_NUMBER, box);
		startActivity(intent);
	}

	public void hint(int res_id) {
		// TODO Auto-generated method stub
		hint(getString(res_id));
	}

	public void hint(String str) {
		// TODO Auto-generated method stub
		text_login_hint.setText(str);
	}

	public void setRegisterBtn(int res_id) {
		// TODO Auto-generated method stub
		btn_register.setText(res_id);
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_login:
			startActivity(new Intent(this, DevlstActivity.class));
			return true;
		case R.id.btn_register:
			startActivity(new Intent(this, BoxActivity.class));
			return true;
		}
		return false;
	}

}