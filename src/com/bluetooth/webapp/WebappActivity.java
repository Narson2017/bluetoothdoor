package com.bluetooth.webapp;

import org.park.R;
import org.park.prefs.PreferenceHelper;
import org.park.prefs.SettingActivity;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.Quit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.bluetooth.connection.ConnHandle;
import com.bluetooth.connection.Connecter;
import com.bluetooth.connection.LockCommand;

@SuppressLint("SetJavaScriptEnabled")
public class WebappActivity extends Activity implements ConnHandle {
	String url = "http://192.168.137.1:8080/webapp/web_app.html";
	WebView myWebView;
	Connecter mConnecter;
	int cabinet, box;
	String password;
	boolean if_exit;
	WebAppInterface myWebapp;
	TextView tx_fault;
	PreferenceHelper mPrefs;
	int operation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.web_app);

		// initial data
		myWebapp = new WebAppInterface();
		mConnecter = new Connecter(this, this);
		if_exit = false;
		mConnecter.register(this);
		tx_fault = (TextView) findViewById(R.id.text_hint);
		mPrefs = new PreferenceHelper(this);
		password = mPrefs.getPsw();
		cabinet = 1;
		box = 1;

		// Log.i("WebApp", Uri.parse(url).getHost());
		myWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = myWebView.getSettings();
		// enable javascript
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);

		// bind javascript interface
		myWebView.addJavascriptInterface(myWebapp, "Android");
		// load page in my own web view
		myWebView.setWebViewClient(new MyWebViewClient());
		// load web page
		myWebView.loadUrl(url);
		mConnecter.start();
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (Uri.parse(url).getHost().equals("192.168.137.1")) {
				// This is my web site, so do not override; let my WebView load
				// the page
				return false;
			}
			// Otherwise, the link is not for a page on my site, so launch
			// another Activity that handles URLs
			// which resolves to the user's default web browser
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			startActivity(intent);
			return true;
		}
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_back:
			mConnecter.clean();
			if_exit = true;
			if (!mConnecter.if_receiving)
				finish();
			break;
		case R.id.btn_exit:
			mConnecter.clean();
			if_exit = true;
			if (!mConnecter.if_receiving)
				Quit.quit(this);
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_setting:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.btn_query:
			myWebView.reload();
			break;
		}
	}

	public class WebAppInterface {
		@JavascriptInterface
		public void open(String m_pair_psw, String m_cabinet, String m_box) {
			password = m_pair_psw;
			cabinet = Integer.valueOf(m_cabinet).intValue();
			box = Integer.valueOf(m_box).intValue();
			if (mConnecter.if_connected) {
				operation = Common.OPERATE_OPEN;
				mConnecter.send(LockCommand.getPasswordCmd(password, cabinet,
						box));
			} else
				mConnecter.start();
		}

		@JavascriptInterface
		public void change(String old_password, String new_password) {
			if (!mConnecter.if_connected) {
				mConnecter.start();
			} else {
				mConnecter.send(LockCommand.changePasswordCmd(old_password,
						new_password));
				password = new_password;
			}
		}

		@JavascriptInterface
		public void openAll(String psw) {
			if (mConnecter.if_connected) {
				operation = Common.OPERATE_ALL_BOXES;
				mConnecter.send(LockCommand.getPasswordCmd(password, cabinet,
						box));
			} else
				mConnecter.start();
		}

		@JavascriptInterface
		public void query(String psw) {
			if (mConnecter.if_connected) {
				operation = Common.OPERATION_QUERY;
				mConnecter.send(LockCommand.getPasswordCmd(password, cabinet,
						box));
			} else
				mConnecter.start();
		}
	}

	@Override
	public void connected(boolean state) {
		// TODO Auto-generated method stub
		if (state) {
			tx_fault.setText(R.string.connect_success);
			new Thread(timeLimit).start();
		}
	}

	@Override
	public void sended(boolean state) {
		// TODO Auto-generated method stub
		if (state)
			tx_fault.setText(R.string.send_success);
		else
			tx_fault.setText(R.string.send_failed);
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		tx_fault.setText(R.string.connect_failed);
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

		tx_fault.setText(R.string.searching);

	}

	@Override
	public void searched() {
		// TODO Auto-generated method stub

		tx_fault.setText(R.string.search_done);

	}

	private void feedback(int state) {
		feedback(String.valueOf(state));
	}

	private void feedback(String data) {
		myWebView.loadUrl("javascript:feedback('" + data + "')");
	}

	@Override
	public void received(String received_data) {
		// TODO Auto-generated method stub
		if (received_data != null) {
			switch (LockCommand.checkRecvType(received_data)) {
			case Common.RECEIVE_DYNAMIC_PASSWORD_SUCCESS:
				switch (operation) {
				case Common.OPERATE_OPEN:
					mConnecter.send(LockCommand.openCmd(
							Common.DEFAULT_PAIR_PASSWORD, cabinet, box,
							received_data));
					break;
				case Common.OPERATE_ALL_BOXES:
					mConnecter.send(LockCommand.openAllCmd(password, cabinet,
							box, received_data));
					break;
				case Common.OPERATION_QUERY:
					mConnecter.send(LockCommand.queryCmd(password, cabinet,
							box, received_data));
					break;
				}
				feedback(Common.RECEIVE_DYNAMIC_PASSWORD_SUCCESS);
				break;
			case Common.RECEIVE_DYNAMIC_PASSWORD_FAILED:
				tx_fault.setText(R.string.device_return_wrong);
				myWebView.loadUrl("javascript:feedback('"
						+ Common.RECEIVE_DYNAMIC_PASSWORD_FAILED + "')");
				break;
			case Common.RECEIVE_OPEN_DOOR_SUCCESS:
				tx_fault.setText(R.string.open_door_success);
				myWebView.loadUrl("javascript:feedback('"
						+ Common.RECEIVE_OPEN_DOOR_SUCCESS + "')");
				break;
			case Common.RECEIVE_OPEN_DOOR_FAILED:
				tx_fault.setText(R.string.open_door_failed);
				myWebView.loadUrl("javascript:feedback('"
						+ Common.RECEIVE_OPEN_DOOR_FAILED + "')");
				break;
			case Common.RECEIVE_CLOSE_DOOR_SUCCESS:
				tx_fault.setText(R.string.close_door_success);
				myWebView.loadUrl("javascript:feedback('"
						+ Common.RECEIVE_CLOSE_DOOR_SUCCESS + "')");
				break;
			case Common.RECEIVE_CLOSE_DOOR_FAILED:
				tx_fault.setText(R.string.close_door_failed);
				myWebView.loadUrl("javascript:feedback('"
						+ Common.RECEIVE_CLOSE_DOOR_FAILED + "')");
				break;
			case Common.RECEIVE_PAIR_PASSWORD_SUCCESS:
				tx_fault.setText(R.string.operate_success);
				myWebView.loadUrl("javascript:feedback('"
						+ Common.RECEIVE_PAIR_PASSWORD_SUCCESS + "')");
				mPrefs.savePassword(password);
				break;
			case Common.RECEIVE_PAIR_PASSWORD_FAILED:
				tx_fault.setText(R.string.operate_failed);
				myWebView.loadUrl("javascript:feedback('"
						+ Common.RECEIVE_PAIR_PASSWORD_FAILED + "')");
				password = mPrefs.getPsw();
				break;
			case Common.MSG_OPEN_ALL_SUCCESS:
				tx_fault.setText(R.string.open_door_success);
				feedback(Common.MSG_OPEN_ALL_SUCCESS);
				break;
			case Common.MSG_OPEN_ALL_FAILED:
				tx_fault.setText(R.string.open_door_failed);
				feedback(Common.MSG_OPEN_ALL_FAILED);
				break;
			case Common.MSG_QUERY_SUCCESS:
				feedback("#" + received_data.substring(28, 52));
				break;
			case Common.MSG_QUERY_FAILED:
				feedback(Common.MSG_QUERY_FAILED);
				break;
			}
		} else
			tx_fault.setText(R.string.receive_failed);

	}

	@Override
	public void timeout() {
		// TODO Auto-generated method stub
		mConnecter.clean();
		finish();
	}

	@Override
	public void found(boolean state) {
		// TODO Auto-generated method stub
		if (state)
			tx_fault.setText(R.string.found);
		else {
			tx_fault.setText(R.string.not_found);
		}
	}

	@Override
	public void disconnecting() {
		// TODO Auto-generated method stub
		tx_fault.setText(R.string.disconnecting);
	}

	private Runnable timeLimit = new Runnable() {
		@Override
		public void run() {
			int count = 0;
			while (!if_exit) {
				try {
					Thread.sleep(1024);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (count++ > Common.LIMITE_SECOND) {
					mConnecter.clean();
					finish();
					break;
				}
			}
		}
	};
}