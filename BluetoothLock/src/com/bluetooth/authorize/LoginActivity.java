package com.bluetooth.authorize;

import org.park.R;
import org.park.box.BoxActivity;
import org.park.boxlst.BoxAdapter;
import org.park.prefs.PreferenceHelper;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluetooth.server.BoxWarehouse;

public class LoginActivity extends Activity implements
		View.OnFocusChangeListener, OnClickListener {
	EditText edit_psw, edit_phone;
	ImageView img_psw, img_username;
	Button btn_login, btn_register;
	TextView text_hint;
	public int box, cabinet;
	public String old_psw, new_psw, new_phone;
	private View layout_login, layout_register;
	private Authorize mAuth;
	private PreferenceHelper mPrefs;
	private Loading mload;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);

		// initialize data
		edit_psw = (EditText) findViewById(R.id.edit_psw);
		edit_psw.setOnFocusChangeListener(this);
		edit_phone = (EditText) findViewById(R.id.edit_username);
		edit_phone.setOnFocusChangeListener(this);
		img_psw = (ImageView) findViewById(R.id.img_psw);
		img_username = (ImageView) findViewById(R.id.img_username);
		text_hint = (TextView) findViewById(R.id.text_login_hint);
		layout_login = findViewById(R.id.layout_login);
		layout_register = findViewById(R.id.layout_register);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_register = (Button) findViewById(R.id.btn_register);

		// start
		mPrefs = new PreferenceHelper(this);
		edit_phone
				.setText(((TelephonyManager) getSystemService(TELEPHONY_SERVICE))
						.getLine1Number());
		box = getIntent().getIntExtra(BoxAdapter.BOX_NUMBER, -1);
		cabinet = getIntent().getIntExtra(BoxAdapter.CABINET_NUMBER, -1);
		if (box != -1) {
			layout_register.setVisibility(View.VISIBLE);
			layout_login.setVisibility(View.GONE);
			text_hint.setText(R.string.register);
		} else {
			layout_register.setVisibility(View.GONE);
			layout_login.setVisibility(View.VISIBLE);
			text_hint.setText(R.string.authorize);
			new_phone = mPrefs.getPhone();
			new_psw = mPrefs.getPsw();
			box = mPrefs.getBox();
			cabinet = mPrefs.getCabinet();
		}
		mAuth = new Authorize();
		mload = new Loading(this);
		old_psw = Common.DEFAULT_PAIR_PASSWORD;
	}

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if (arg0 == edit_psw) {
			if (arg1)
				img_psw.setImageResource(R.drawable.ic_device_access_accounts_focused);
			else
				img_psw.setImageResource(R.drawable.ic_device_access_accounts);
		} else if (arg0 == edit_phone) {
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
		case R.id.btn_register:
			text_hint.setText(R.string.loading);
			new_phone = edit_phone.getText().toString();
			mload.setOperation(Common.MSG_REGISTER_LOADING);
			mload.start();
			mAuth.registerBox(new_phone, Common.DEFAULT_PAIR_PASSWORD, cabinet, box);
			break;
		case R.id.btn_login:
			mload.setOperation(Common.MSG_LOGIN_LOADING);
			mload.start();
			text_hint.setText(R.string.loading);
			new_phone = edit_phone.getText().toString().equals("") ? new_phone
					: edit_phone.getText().toString();
			mAuth.obtainBox(new_phone, new_psw, box);
			break;
		case R.id.btn_back:
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
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void hint(int res_id) {
		// TODO Auto-generated method stub
		hint(getString(res_id));
	}

	public void hint(String str) {
		// TODO Auto-generated method stub
		text_hint.setText(str);
	}

	private class Authorize extends BoxWarehouse {

		@Override
		public void received(String data) {
			// TODO Auto-generated method stub
			mload.stop();
			if (data == null) {
				text_hint.setText(R.string.server_fault);
			} else {
				switch (Integer.valueOf(data).intValue()) {
				case Common.RESULT_FAULT:
					text_hint.setText(R.string.operate_failed);
					break;
				case Common.RESULT_NOT_FOUND:
					text_hint.setText(R.string.not_available);
					break;
				default:
					text_hint.setText(R.string.operate_success);
					mPrefs.save(new_phone, new_psw, box, cabinet);
					startActivity(new Intent(LoginActivity.this,
							BoxActivity.class));
					break;
				}
			}
		}
	}
}