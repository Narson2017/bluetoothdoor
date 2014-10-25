package org.park.entrance;

import org.park.R;
import org.park.boxlst.BoxlstActivity;
import org.park.devlist.DevlstActivity;
import org.park.pairpsw.AccountActivity;
import org.park.prefs.settingActivity;
import org.park.util.About;
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
import android.widget.Toast;

import com.bluetooth.authorize.LoginActivity;
import com.bluetooth.box.BoxActivity;

public class NavigateActivity extends Activity implements OnClickListener,
		OnLongClickListener {
	Button btn_login, btn_new_user, btn_change_account;
	int back_key_press_count;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.navigate);

		btn_login = (Button) findViewById(R.id.btn_login);
		btn_login.setOnLongClickListener(this);
		btn_new_user = (Button) findViewById(R.id.btn_new_user);
		btn_new_user.setOnLongClickListener(this);
		btn_change_account = (Button) findViewById(R.id.btn_change_account);
		btn_change_account.setOnLongClickListener(this);
		back_key_press_count = 0;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_login:
			startActivity(new Intent(NavigateActivity.this, LoginActivity.class));
			break;
		case R.id.btn_new_user:
			startActivity(new Intent(NavigateActivity.this,
					BoxlstActivity.class));
			break;
		case R.id.btn_change_account:
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			back_key_press_count++;
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(3072);
						back_key_press_count = 0;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}).start();
			if (back_key_press_count > 1)
				Quit.quit(this);
			else
				Toast.makeText(this, R.string.press_again, Toast.LENGTH_SHORT)
						.show();
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