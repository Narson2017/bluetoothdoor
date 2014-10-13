package org.park.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import org.park.lockmgr.LockManager;
import org.park.prefs.settingActivity;
import org.park.util.About;
import org.park.util.Common;
import org.park.util.HexConvert;
import org.park.util.Quit;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class showDetail extends Activity implements View.OnClickListener,
		Controler {
	public static final String OPERATION = "OPERATION";
	private static final int OPR_QUERY_LOCK = 1;
	private static final int OPR_QUERY_ALL = 0;
	private static final int OPR_OPEN_LOCK = 2;

	public static String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	public static String PIN_CODE = "1234";

	Button btnExit, btn_back;
	TextView tvTitle;

	BluetoothAdapter btAdapt = null;
	BluetoothSocket btSocket = null;

	Boolean bConnect = false;
	String strName = null;
	static String strAddress = null;
	int nNeed = -1;
	byte[] bRecv = new byte[1024];
	int nRecved = 0;
	ConnectedThread connThr;

	private LinearLayout detail_view, progress_connect, tx_connect_failed;
	private TextView tx_fault;

	private LockManager mLockManager;
	int[] lock_sequence = { 7, 6, 5, 4, 3, 2, 1, 0, 15, 14, 13, 12, 11, 10, 9,
			8, 23, 22 };
	int[] empty_sequence = { 31, 30, 29, 28, 27, 26, 25, 24, 39, 38, 37, 36,
			35, 34, 33, 32, 47, 46 };
	private BluetoothMgr mBtMgr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detail);

		mLockManager = new LockManager(this, R.id.btn_box, R.id.box_nbr);
		SharedPreferences _sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String tmp = _sharedPreferences.getString("locknbr", "");
		if (!tmp.equals(""))
			mLockManager.setNbr(Integer.valueOf(tmp));

		detail_view = (LinearLayout) findViewById(R.id.detail_view);
		progress_connect = (LinearLayout) findViewById(R.id.progress_connect);
		tx_connect_failed = (LinearLayout) findViewById(R.id.tx_connect_failed);
		btn_back = (Button) findViewById(R.id.btn_back);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tx_fault = (TextView) findViewById(R.id.tx_fault);

		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt == null) {
			detail_view.setVisibility(View.GONE);
			progress_connect.setVisibility(View.GONE);
			tx_fault.setText(R.string.blue_unabailable);
			tx_connect_failed.setVisibility(View.VISIBLE);
			return;
		}

		mBtMgr = new BluetoothMgr(this, btAdapt);
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		intent.addAction(BluetoothDevice.ACTION_FOUND);
		intent.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		intent.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mBtMgr, intent);
		mLockManager.setEnabled(false);

		Bundle bunde = this.getIntent().getExtras();
		if (bunde != null)
			connectDev(bunde.getString("MAC"), bunde.getString("NAME"));
		else
			mBtMgr.findDev(strAddress);
	}

	// private EditText etUsername, etPassword;

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBtMgr);
		// stop thread
		bConnect = false;
		if (btAdapt != null)
			btAdapt.disable();
		super.onDestroy();
	}

	private void unpair(String currentMac) {
		Set<BluetoothDevice> bondedDevices = btAdapt.getBondedDevices();
		try {
			Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class
					.getCanonicalName());
			Method removeBondMethod = btDeviceInstance.getMethod("removeBond");
			boolean cleared = false;
			for (BluetoothDevice bluetoothDevice : bondedDevices) {
				String mac = bluetoothDevice.getAddress();
				if (mac.equals(currentMac)) {
					removeBondMethod.invoke(bluetoothDevice);
					Log.i(Common.TAG, "Cleared Pairing");
					cleared = true;
					break;
				}
			}

			if (!cleared) {
				Log.i(Common.TAG, "Not Paired");
			}
		} catch (Throwable th) {
			Log.e(Common.TAG, "Error pairing", th);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			act_clean();
			startActivity(new Intent(this, splashScreen.class));
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private class ConnectedThread extends Thread {
		private final InputStream mmInStream;
		private final OutputStream mmOutStream;

		public ConnectedThread(BluetoothSocket socket) {
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the input and output streams, using temp objects because
			// member streams are final
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (IOException e) {
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		public void run() {
			// Keep listening to the InputStream until an exception occurs
			byte[] bufRecv = new byte[32];
			int nRecv = 0;
			while (bConnect) {
				try {
					if (nRecved >= nNeed) {
						Log.e(Common.TAG, "System busy, please wait");
						Thread.sleep(3000);
						continue;
					}
					nRecv = mmInStream.read(bufRecv);
					if (nRecv < 1) {
						Log.e(Common.TAG, "Recving Short");
						Thread.sleep(100);
						continue;
					}
					System.arraycopy(bufRecv, 0, bRecv, nRecved, nRecv);
					Log.e(Common.TAG, "Recv:" + String.valueOf(nRecv));
					nRecved += nRecv;
					if (nRecved < nNeed) {
						Thread.sleep(100);
						continue;
					}

					mHandler.obtainMessage(Common.MESSAGE_RECV, nNeed, -1, null)
							.sendToTarget();

				} catch (Exception e) {
					Log.e(Common.TAG, "Recv thread:" + e.getMessage());
					mHandler.sendEmptyMessage(Common.MESSAGE_EXCEPTION_RECV);
					break;
				}
			}
			Log.e(Common.TAG, "Exit while");
		}

		public void send(int lock_nbr, int act) {
			String send_tmp = "5cc50501d2000000d6";
			String begin_length_address = "5cc50501";
			String action;
			String xor_check;

			if (!bConnect)
				return;
			try {
				if (mmOutStream == null)
					return;
				nNeed = 13;
				nRecved = 0;

				switch (act) {
				case OPR_OPEN_LOCK:
					action = "D1";
					xor_check = Integer
							.toHexString(5 ^ 1
									^ Integer.valueOf(action, 16).intValue()
									^ lock_nbr);
					send_tmp = begin_length_address + action
							+ HexConvert.int2hexStr(lock_nbr) + "0000"
							+ xor_check;
					break;
				case OPR_QUERY_LOCK:
					action = "D2";
					xor_check = Integer
							.toHexString(5 ^ 1
									^ Integer.valueOf(action, 16).intValue()
									^ lock_nbr);
					send_tmp = begin_length_address + action
							+ HexConvert.int2hexStr(lock_nbr) + "0000"
							+ xor_check;
					break;
				case OPR_QUERY_ALL:
				default:
					break;
				}
				mmOutStream.write(HexConvert.HexString2Bytes(send_tmp));
				mmOutStream.flush();
			} catch (Exception e) {
				Toast.makeText(showDetail.this, R.string.send_failed,
						Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_setting:
			startActivity(new Intent(this, settingActivity.class));
			break;
		case R.id.btn_connect:
			detail_view.setVisibility(View.GONE);
			progress_connect.setVisibility(View.VISIBLE);
			tx_connect_failed.setVisibility(View.GONE);
			mBtMgr.findDev(strAddress);
			break;
		case R.id.btn_box:
			mLockManager.set_state(false, false);
			connThr.send(mLockManager.getNbr(), OPR_OPEN_LOCK);
			break;
		case R.id.btn_back:
			act_clean();
			startActivity(new Intent(this, splashScreen.class));
			finish();
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_exit:
			act_clean();
			Quit.act_exit(this);
			break;
		}
	}

	private void act_clean() {
		if (bConnect) {
			bConnect = false;
			try {
				Thread.sleep(100);
				if (mmInStream != null)
					mmInStream.close();
				if (mmOutStream != null)
					mmOutStream.close();
				if (btSocket != null)
					btSocket.close();
			} catch (Exception e) {
				Log.e(Common.TAG, "Clean error...");
				e.printStackTrace();
			}
		}
	}

	// Hander
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_CONNECT:
				new Thread(new Runnable() {
					public void run() {
						try {
							UUID uuid = UUID.fromString(SPP_UUID);
							BluetoothDevice btDev = btAdapt
									.getRemoteDevice(strAddress);
							btSocket = btDev
									.createRfcommSocketToServiceRecord(uuid);
							btSocket.connect();
						} catch (Exception e) {
							Log.d(Common.TAG, "Error connected to: "
									+ strAddress);
							bConnect = false;
							mmInStream = null;
							mmOutStream = null;
							btSocket = null;
							e.printStackTrace();
							mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_LOST);
							return;
						}
						mHandler.sendEmptyMessage(Common.MESSAGE_CONNECT_SUCCEED);
					}

				}).start();
				break;
			case Common.MESSAGE_CONNECT_SUCCEED:
				Toast.makeText(showDetail.this, R.string.connect_success,
						Toast.LENGTH_SHORT).show();
				detail_view.setVisibility(View.VISIBLE);
				progress_connect.setVisibility(View.GONE);
				tx_connect_failed.setVisibility(View.GONE);
				bConnect = true;
				connThr = new ConnectedThread(btSocket);
				connThr.start();
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (bConnect) {
							// TODO Auto-generated method stub
							connThr.send(-1, OPR_QUERY_ALL);
							try {
								Thread.sleep(10000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}

				}).start();
				break;
			case Common.MESSAGE_EXCEPTION_RECV:
			case Common.MESSAGE_CONNECT_LOST:
				unpair(strAddress);
				try {
					if (mmInStream != null)
						mmInStream.close();
					if (mmOutStream != null)
						mmOutStream.close();
					if (btSocket != null)
						btSocket.close();
				} catch (IOException e) {
					Log.e(Common.TAG, "Close Error");
					e.printStackTrace();
				} finally {
					mmInStream = null;
					mmOutStream = null;
					btSocket = null;
					bConnect = false;
					mLockManager.setEnabled(false);
					detail_view.setVisibility(View.GONE);
					progress_connect.setVisibility(View.GONE);
					tx_fault.setText(R.string.connect_failed);
					tx_connect_failed.setVisibility(View.VISIBLE);
				}
				break;
			case Common.MESSAGE_WRITE:
				break;
			case Common.MESSAGE_READ:
				break;
			case Common.MESSAGE_RECV:
				// String strRecv = bytesToString(bRecv, msg.arg1);
				String strRecv = HexConvert.Bytes2HexString(bRecv, nNeed);
				// System.out.print("Received: " + strRecv);
				String tmp1 = strRecv.substring(12, 24);
				String str_tmp = HexConvert.hexString2binaryString(tmp1);
				boolean is_lock = true,
				is_empty = true;

				int index = mLockManager.getNbr() - 1;
				if (index < 0)
					break;
				if (str_tmp.charAt(lock_sequence[index]) == '0')
					is_lock = false;
				else
					is_lock = true;
				if (str_tmp.charAt(empty_sequence[index]) == '0')
					is_empty = false;
				else
					is_empty = true;
				mLockManager.set_state(is_lock, is_empty);
				mLockManager.setEnabled(true);
				// reset received length
				nRecved = 0;
				break;
			case Common.MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(Common.TOAST),
						Toast.LENGTH_SHORT).show();
				break;
			case Common.MESSAGE_AUTHORIZE_PASSED:
				connThr.send(msg.arg1 + 1, OPR_OPEN_LOCK);
				mLockManager.set_state(false, true);
				break;
			}
		}
	};

	@Override
	public void connectDev(String addr, String name) {
		// TODO Auto-generated method stub
		strName = name;
		strAddress = addr;
		tvTitle.setText(strName);
		mHandler.sendEmptyMessageDelayed(Common.MESSAGE_CONNECT, 1000);
	}

	@Override
	public void changeView(int boxes, int progress, int fault_tx, int fault) {
		// TODO Auto-generated method stub
		detail_view.setVisibility(boxes);
		progress_connect.setVisibility(progress);
		tx_fault.setText(fault_tx);
		tx_connect_failed.setVisibility(fault);
	}
}