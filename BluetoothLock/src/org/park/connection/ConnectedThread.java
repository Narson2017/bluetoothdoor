package org.park.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.park.bluetooth.R;
import org.park.util.Common;
import org.park.util.HexConvert;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ConnectedThread extends Thread {
	final int RESPONSE_LENGTH = 14;
	static final int OPR_OPEN_LOCK = 2;
	static final int OPR_QUERY_LOCK = 1;
	static final int OPR_QUERY_ALL = 0;
	Boolean bConnect = false;
	int nNeed = -1;
	byte[] bRecv = new byte[1024];
	int nRecved = 0;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	BluetoothSocket btSocket = null;
	showDetail mCtx;
	int[] lock_sequence = { 7, 6, 5, 4, 3, 2, 1, 0, 15, 14, 13, 12, 11, 10, 9,
			8, 23, 22 };
	int[] empty_sequence = { 31, 30, 29, 28, 27, 26, 25, 24, 39, 38, 37, 36,
			35, 34, 33, 32, 47, 46 };
	CommandMgr mCmdmgr;

	public ConnectedThread(BluetoothSocket socket, showDetail cx) {
		mCtx = cx;
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
		mCmdmgr = new CommandMgr();
	}

	public void setConnState(boolean bl) {
		bConnect = bl;
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

	public void act_clean() {
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

	public void startQuery() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (bConnect) {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}).start();
	}

	// Hander
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Common.MESSAGE_RECV:
				// String strRecv = bytesToString(bRecv, msg.arg1);
				String strRecv = HexConvert.Bytes2HexString(bRecv, nNeed);

				switch (mCmdmgr.checkRecvType(strRecv)) {
				case CommandMgr.RECEIVE_DYNAMIC_PASSWORD_SUCCESS:
					nNeed = RESPONSE_LENGTH;
					nRecved = 0;
					try {
						mmOutStream.write(mCmdmgr.getOpenLockCommand(strRecv));
						mmOutStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case CommandMgr.RECEIVE_DYNAMIC_PASSWORD_FAILED:
					mCtx.setHint(R.string.obtain_dynamic_psw_failed);
					break;
				case CommandMgr.RECEIVE_PAIR_PASSWORD_SUCCESS:
					mCtx.setHint(R.string.change_psw_success);
					break;
				case CommandMgr.RECEIVE_PAIR_PASSWORD_FAILED:
					mCtx.setHint(R.string.change_psw_failed);
					break;
				case CommandMgr.RECEIVE_OPEN_DOOR_SUCCESS:
					mCtx.setHint(R.string.open_door_success);
					break;
				case CommandMgr.RECEIVE_OPEN_DOOR_FAILED:
					mCtx.setHint(R.string.open_door_failed);
					break;
				case CommandMgr.RECEIVE_CLOSE_DOOR_SUCCESS:
					mCtx.setHint(R.string.close_door_success);
					break;
				case CommandMgr.RECEIVE_CLOSE_DOOR_FAILED:
					mCtx.setHint(R.string.close_door_failed);
					break;
				}
				// reset received length
				nRecved = 0;
				break;
			case Common.MESSAGE_EXCEPTION_RECV:
			case Common.MESSAGE_CONNECT_LOST:
				mCtx.unpair();
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
					mCtx.setBoxEnable(false);
					mCtx.setBoxVisible(false);
					mCtx.setProgressVisible(false);
					mCtx.setHint(R.string.connect_failed);
				}
				break;
			case Common.MESSAGE_WRITE:
				break;
			case Common.MESSAGE_READ:
				break;
			}
		}
	};

	public void changePairPsw(String tmp) {
		nNeed = RESPONSE_LENGTH;
		nRecved = 0;
		try {
			mmOutStream.write(mCmdmgr.getChangePairPsw(tmp));
			mmOutStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void openlock(int cabinet_id, int box_id) {
		nNeed = RESPONSE_LENGTH;
		nRecved = 0;
		try {
			mmOutStream.write(mCmdmgr.getPswAlg(showDetail.PAIR_PASSWORD,
					cabinet_id, box_id));
			mmOutStream.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}