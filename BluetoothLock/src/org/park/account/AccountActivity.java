package org.park.account;

import org.park.R;
import org.park.prefs.settingActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AccountActivity extends Activity implements OnClickListener {
	EditText edit_username, edit_psw;
	String old_username, old_psw;
	int cabinet, box;
	Button mbtn;
	TextView text_hint;
	Loading mLoad = null;
	UpdateInfo mUpdateinfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_change);

		edit_psw = (EditText) findViewById(R.id.edit_psw);
		edit_username = (EditText) findViewById(R.id.edit_username);
		Intent tmp = getIntent();
		old_username = tmp.getStringExtra(settingActivity.USERNAME);
		old_psw = tmp.getStringExtra(settingActivity.PASSWORD);
		cabinet = tmp.getIntExtra(settingActivity.CABINET, -1);
		box = tmp.getIntExtra(settingActivity.BOX, -1);

		mbtn = (Button) findViewById(R.id.btn_authorize);
		text_hint = (TextView) findViewById(R.id.text_hint);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_authorize:
			mLoad = new Loading(this);
			new Thread(mLoad).start();
			if (mUpdateinfo == null)
				mUpdateinfo = new UpdateInfo(this, mLoad);
			mUpdateinfo.set_account(edit_username.getText().toString(),
					old_psw, edit_psw.getText().toString(), cabinet, box);
			mUpdateinfo.update_info();
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
}
