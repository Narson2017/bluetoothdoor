package org.park.pairpsw;

import org.park.R;
import org.park.prefs.PreferenceHelper;
import org.park.util.About;
import org.park.util.Quit;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AccountActivity extends Activity implements OnClickListener {
	EditText edit_psw;
	String old_psw, new_psw;
	Button mbtn;
	TextView text_hint;
	ChangePassword mUpdateinfo;
	PreferenceHelper mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.account_change);

		// initail data
		edit_psw = (EditText) findViewById(R.id.edit_psw);
		mPrefs = new PreferenceHelper(this);
		old_psw = mPrefs.getPsw();
		mbtn = (Button) findViewById(R.id.btn_authorize);
		text_hint = (TextView) findViewById(R.id.text_hint);
		mUpdateinfo = new ChangePassword(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_authorize:
			new_psw = edit_psw.getText().toString();
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
			mUpdateinfo.disconnected();
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		mUpdateinfo.disconnected();
		super.onDestroy();
	}
}
