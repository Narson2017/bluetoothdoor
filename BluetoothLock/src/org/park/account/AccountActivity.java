package org.park.account;

import org.park.R;
import org.park.entrance.splashScreen;
import org.park.util.About;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AccountActivity extends Activity implements OnClickListener {
	EditText edit_username, edit_psw;
	String old_username, old_psw, new_psw, new_username;
	int cabinet, box;
	Button mbtn;
	TextView text_hint;
	UpdateInfo mUpdateinfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_change);

		edit_psw = (EditText) findViewById(R.id.edit_psw);
		edit_username = (EditText) findViewById(R.id.edit_username);
		SharedPreferences _sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		new_username = _sharedPreferences.getString("username", "");
		new_psw = _sharedPreferences.getString("password", "");
		cabinet = Integer.valueOf(_sharedPreferences.getString("cabinet", ""))
				.intValue();
		box = Integer.valueOf(_sharedPreferences.getString("locknbr", ""))
				.intValue();

		mbtn = (Button) findViewById(R.id.btn_authorize);
		text_hint = (TextView) findViewById(R.id.text_hint);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_authorize:
			if (mUpdateinfo == null)
				mUpdateinfo = new UpdateInfo(this);
			old_psw = edit_psw.getText().toString();
			old_username = edit_username.getText().toString();
			mUpdateinfo.startUpdate();
			break;
		case R.id.btn_exit:
			Quit.act_exit(this);
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_action_back:
		case R.id.btn_back:
			startActivity(new Intent(this, splashScreen.class));
			if (mUpdateinfo != null)
				mUpdateinfo.disconnected();
			finish();
			break;
		}
	}

	public void set_btn_text(int resid) {
		mbtn.setText(resid);
	}

	public void set_hint(int serverFault) {
		// TODO Auto-generated method stub
		text_hint.setText(serverFault);
	}

	public void set_hint(String received_data) {
		// TODO Auto-generated method stub
		text_hint.setText(received_data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(this, splashScreen.class));
			if (mUpdateinfo != null)
				mUpdateinfo.disconnected();
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		if (mUpdateinfo != null)
			mUpdateinfo.disconnected();
		super.onDestroy();
	}
}
