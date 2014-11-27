package org.park.pairpsw;

import org.park.R;
import org.park.prefs.PreferenceHelper;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.Quit;

import com.bluetooth.connection.ConnHandle;
import com.bluetooth.connection.Connecter;
import com.bluetooth.connection.LockCommand;

import android.app.Activity;
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

public class AccountActivity extends Activity implements OnClickListener,
		ConnHandle {
	EditText edit_psw;
	String old_psw, new_psw;
	Button mbtn;
	TextView text_hint;
	PreferenceHelper mPrefs;
	Loading mload;
	Connecter mConnecter;

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
		mload = new Loading(this);
		mConnecter = new Connecter(this, this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_authorize:
			new_psw = edit_psw.getText().toString();
			update();
			break;
		case R.id.btn_exit:
			Quit.quit(this);
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_action_back:
		case R.id.btn_back:
			disconnected();
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
			disconnected();
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onDestroy() {
		disconnected();
		super.onDestroy();
	}

	public void update() {
		set_hint(R.string.loading);
		mload.start();
		if (!mConnecter.if_connected) {
			mConnecter.start();
		} else {
			mConnecter.send(LockCommand.changePasswordCmd(old_psw, new_psw));
		}
	}

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			set_hint(R.string.connect_success);
			mConnecter.send(LockCommand.changePasswordCmd(old_psw, new_psw));
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub
		if (state)
			set_hint(R.string.send_success);
		else
			set_hint(R.string.send_failed);
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		set_hint(R.string.connect_failed);
		mload.stop();
	}

	@Override
	public void pairing() {
		// TODO Auto-generated method stub

	}

	@Override
	public void paired(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void searching() {
		// TODO Auto-generated method stub
		set_hint(R.string.searching);
	}

	@Override
	public void searched() {
		// TODO Auto-generated method stub

	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		SharedPreferences mPrefs;
		set_hint(received_data);

		switch (LockCommand.checkRecvType(received_data)) {
		case Common.RECEIVE_PAIR_PASSWORD_SUCCESS:
			set_hint(R.string.operate_success);
			mload.stop();
			mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			mPrefs.edit().putString("password", new_psw).commit();
			break;
		case Common.RECEIVE_PAIR_PASSWORD_FAILED:
			set_hint(R.string.operate_failed);
			mload.stop();
			break;
		}
	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
		mload.stop();
		set_hint(R.string.time_out);
	}

	@Override
	public void found(boolean state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnecting() {
		// TODO Auto-generated method stub

	}
}
