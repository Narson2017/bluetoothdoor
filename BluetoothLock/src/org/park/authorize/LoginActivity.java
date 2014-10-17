package org.park.authorize;

import org.park.R;
import org.park.boxlst.BoxAdapter;
import org.park.connection.showDetail;
import org.park.devlist.DevlstActivity;
import org.park.entrance.splashScreen;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
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
	protected static final int MSG_LOGIN_LOADING = 0;
	protected static final int MSG_REGISTER_LOADING = 2;
	public static final int MSG_SERVER_FAULT = 3;
	EditText edit_psw, edit_username;
	ImageView img_psw, img_username;
	Button btn_login, btn_register;
	TextView text_login_hint;
	AuthenticationManager mAuthMgr;

	int[] loginTexts = { R.string.login_load1, R.string.login_load2,
			R.string.login_load3 };
	int[] registerTexts = { R.string.register_load1, R.string.register_load2,
			R.string.register_load3 };
	public boolean authorizing = true;
	public int box_nbr, cabinet_nbr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra(Quit.IS_EXIT, false))
			finish();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);

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

		box_nbr = getIntent().getIntExtra(BoxAdapter.BOX_NUMBER, -1);
		cabinet_nbr = getIntent().getIntExtra(BoxAdapter.CABINET_NUMBER, -1);
		if (box_nbr != -1) {
			btn_login.setEnabled(false);
			btn_register.setEnabled(true);
			btn_login.setBackgroundColor(R.color.trolley_grey);
			text_login_hint.setText(R.string.not_register);
		} else {
			btn_register.setEnabled(false);
			btn_login.setEnabled(true);
			btn_register.setBackgroundColor(R.color.trolley_grey);
			text_login_hint.setText(R.string.please_login);
		}
		mAuthMgr = new AuthenticationManager(this);
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

	private class OprLoad implements Runnable {
		int operation;

		public OprLoad(int opr) {
			operation = opr;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int i = 0;
			while (authorizing) {
				switch (operation) {
				case MSG_LOGIN_LOADING:
					mHandler.obtainMessage(MSG_LOGIN_LOADING,
							loginTexts[(i++) % loginTexts.length], -1)
							.sendToTarget();
					break;
				case MSG_REGISTER_LOADING:
					mHandler.obtainMessage(MSG_REGISTER_LOADING,
							registerTexts[(i++) % registerTexts.length], -1)
							.sendToTarget();
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (i == 60) {
					mHandler.sendEmptyMessage(MSG_SERVER_FAULT);
					break;
				}
			}
			mHandler.obtainMessage(MSG_LOGIN_LOADING, R.string.login, -1)
					.sendToTarget();
			mHandler.obtainMessage(MSG_REGISTER_LOADING, R.string.register, -1)
					.sendToTarget();
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
				new Thread(new OprLoad(MSG_LOGIN_LOADING)).start();
			} catch (Exception e) {
				System.err.print("Begin authorize failed: " + e.toString());
			}
			break;
		case R.id.btn_register:
			try {
				mAuthMgr.register(edit_username.getText().toString(), edit_psw
						.getText().toString(), cabinet_nbr, box_nbr);
				new Thread(new OprLoad(MSG_REGISTER_LOADING)).start();
			} catch (Exception e) {
				System.err.print("Begin register failed: " + e.toString());
			}
			System.out.print("Registering");
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

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOGIN_LOADING:
				btn_login.setText(msg.arg1);
				break;
			case MSG_REGISTER_LOADING:
				btn_register.setText(msg.arg1);
				break;
			case MSG_SERVER_FAULT:
				hint(R.string.server_fault);
				authorizing = false;
				break;
			}
		}
	};

	public void authPassed() {
		// TODO Auto-generated method stub
		authorizing = false;
		text_login_hint.setText(R.string.auth_success);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		prefs.edit().putString("username", edit_username.getText().toString())
				.commit();
		prefs.edit().putString("password", edit_psw.getText().toString())
				.commit();
		prefs.edit().putString("locknbr", String.valueOf(box_nbr)).commit();
		prefs.edit().putString("cabinet", String.valueOf(cabinet_nbr)).commit();

		Intent intent = new Intent(this, showDetail.class);
		intent.putExtra(BoxAdapter.CABINET_NUMBER, cabinet_nbr);
		intent.putExtra(BoxAdapter.BOX_NUMBER, box_nbr);
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

	public void stopLoading() {
		// TODO Auto-generated method stub
		authorizing = false;
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_login:
			startActivity(new Intent(this, DevlstActivity.class));
			return true;
		case R.id.btn_register:
			startActivity(new Intent(this, showDetail.class));
			return true;
		}
		return false;
	}

}