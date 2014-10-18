package org.park.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.park.command.LockCommand;
import org.park.util.Common;
import org.park.util.HexConvert;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ContactThread extends Thread {
	final int RESPONSE_LENGTH = 16;
	static final int OPR_OPEN_LOCK = 2;
	static final int OPR_QUERY_LOCK = 1;
	static final int OPR_QUERY_ALL = 0;
	public Boolean if_connected = false;
	int nNeed = -1;
	byte[] bRecv = new byte[1024];
	int nRecved = 0;
	private InputStream mmInStream;
	private OutputStream mmOutStream;
	BluetoothSocket btSocket = null;
	HandleConnMsg mCtx;
	LockCommand mCmdmgr;

	public ContactThread(BluetoothSocket socket, HandleConnMsg cx) {
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
		mCmdmgr = new LockCommand();
	}

	public void run() {
		// Keep listening to the InputStream until an exception occurs
		byte[] bufRecv = new byte[32];
		int nRecv = 0;
		if_connected = true;
		while (if_connected) {
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

	public void onClean() {
		if (if_connected) {
			if_connected = false;
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
				if_connected = false;
				mCtx.disconnected();
			}
		}
	}

	public void startQuery() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (if_connected) {
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
				mCtx.received(HexConvert.Bytes2HexString(bRecv, nNeed));
				// reset received length
				nRecved = 0;
				break;
			case Common.MESSAGE_EXCEPTION_RECV:
			case Common.MESSAGE_CONNECT_LOST:
				onClean();
				break;
			case Common.MESSAGE_WRITE:
				break;
			case Common.MESSAGE_READ:
				break;
			}
		}
	};

	public void send(String mCommand) {
		// TODO Auto-generated method stub
		nNeed = RESPONSE_LENGTH;
		nRecved = 0;
		try {
			mmOutStream.write(HexConvert.HexString2Bytes(mCommand));
			mmOutStream.flush();
			mCtx.sended(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mCtx.sended(false);
		}
	}
}