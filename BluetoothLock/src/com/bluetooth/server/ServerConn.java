package com.bluetooth.server;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.park.util.Common;
import org.park.util.HexConvert;
import org.park.util.MDes;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ServerConn {
	protected String OPERATION_FAILED = "-1";
	protected String NOT_AVAILABLE = "0";

	private String phone, password, ciphertext;
	private int box, cabinet;
	private ServerHandle mServmsg;

	public ServerConn(ServerHandle handle) {
		super();
		mServmsg = handle;
	}

	public void sendRequest(int operation) {
		SoapObject request;
		byte[] tmp = null;
		switch (operation) {
		case Common.OPERATE_ALL_BOXES:
			request = new SoapObject(Common.WSDL_TARGET_NAMESPACE,
					Common.FUNC_ALL_BOXES);
			// body
			request.addProperty(Common.CABINET,
					String.valueOf(Common.DEFAULT_CABINET));
			// encrypt header
			try {
				tmp = MDes.desEncrypt(String.valueOf(Common.DEFAULT_CABINET)
						.getBytes(Common.UTF_8), Common.DES_CODE);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ciphertext = HexConvert.Bytes2HexString(tmp, tmp.length);
			sendRequest(request);
			break;
		case Common.OPERATE_OPEN:
			request = new SoapObject(Common.WSDL_TARGET_NAMESPACE,
					Common.FUNC_OPEN);
			// body
			request.addProperty(Common.PHONE_NUMBER, phone);
			request.addProperty(Common.PASSWORD, password);
			request.addProperty(Common.BOX, String.valueOf(box));
			// encrypt header
			try {
				tmp = MDes.desEncrypt((phone + password + String.valueOf(box))
						.getBytes(Common.UTF_8), Common.DES_CODE);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ciphertext = HexConvert.Bytes2HexString(tmp, tmp.length);
			sendRequest(request);
			break;
		case Common.OPERATE_REGISTER:
			request = new SoapObject(Common.WSDL_TARGET_NAMESPACE,
					Common.FUNC_SAVE);
			// body
			request.addProperty(Common.PHONE_NUMBER, phone);
			request.addProperty(Common.PASSWORD, password);
			request.addProperty(Common.BOX, String.valueOf(box));
			request.addProperty(Common.CABINET, String.valueOf(cabinet));
			// encrypt header
			try {
				tmp = MDes.desEncrypt(
						(phone + password + String.valueOf(box) + String
								.valueOf(cabinet)).getBytes(Common.UTF_8),
						Common.DES_CODE);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ciphertext = HexConvert.Bytes2HexString(tmp, tmp.length);
			sendRequest(request);
			break;
		}
	}

	public void sendRequest(final SoapObject request) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);
				envelope.setOutputSoapObject(request);

				// soap header
				Element[] header = new Element[1];
				header[0] = new Element().createElement(
						Common.WSDL_TARGET_NAMESPACE, "RequestSOAPHeader");

				Element version = new Element().createElement(
						Common.WSDL_TARGET_NAMESPACE, "version");
				version.addChild(Node.TEXT, String.valueOf(SoapEnvelope.VER11));
				header[0].addChild(Node.ELEMENT, version);

				Element messageId = new Element().createElement(
						Common.WSDL_TARGET_NAMESPACE, "messageId");
				String mSequence = "11112121";
				messageId.addChild(Node.TEXT, mSequence);
				header[0].addChild(Node.ELEMENT, messageId);

				Element timestamp = new Element().createElement(
						Common.WSDL_TARGET_NAMESPACE, "timestamp");
				String mTimestamp = new java.sql.Timestamp(Calendar
						.getInstance().getTimeInMillis()).toString();
				timestamp.addChild(Node.TEXT, mTimestamp);
				header[0].addChild(Node.ELEMENT, timestamp);

				Element userId = new Element().createElement(
						Common.WSDL_TARGET_NAMESPACE, "userId");
				userId.addChild(Node.TEXT, String.valueOf(box));
				header[0].addChild(Node.ELEMENT, userId);

				Element sign = new Element().createElement(
						Common.WSDL_TARGET_NAMESPACE, "sign");
				sign.addChild(Node.TEXT, ciphertext);
				header[0].addChild(Node.ELEMENT, sign);
				envelope.headerOut = header;
				
				HttpTransportSE httpTransport = new HttpTransportSE(
						Common.SOAP_ADDRESS);
				SoapObject result = null;
				try {
					httpTransport.call(Common.SOAP_ACTION, envelope);
					// Log.i(Common.TAG, envelope.bodyIn.toString());
					mHandler.sendEmptyMessage(Common.MSG_SEND_SUCCESS);
					result = (SoapObject) envelope.bodyIn;
				} catch (Exception e) {
					e.printStackTrace();
					mHandler.sendEmptyMessage(Common.MSG_SEND_FAILED);
				}
				if (result != null) {
					mHandler.obtainMessage(Common.MSG_RECEIVE_SUCCESS,
							result.getProperty(0).toString()).sendToTarget();
				} else
					mHandler.sendEmptyMessage(Common.MSG_RECEIVE_FAILED);
			}
		}).start();
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPsw(String psw) {
		password = psw;
	}

	public void setLock(int lock) {
		box = lock;
	}

	public void setCabinet(int cabinet) {
		this.cabinet = cabinet;
	}

	private Handler mHandler = new Handler() {
		// 重写handleMessage()方法，此方法在UI线程运行
		@Override
		public void handleMessage(Message msg) {
			String result = null;
			switch (msg.what) {
			case Common.MSG_RECEIVE_FAILED:
				mServmsg.received(null);
				break;
			case Common.MSG_RECEIVE_SUCCESS:
				mServmsg.received((String) msg.obj);
				break;
			case Common.MSG_SEND_SUCCESS:
				mServmsg.sended(true);
				break;
			case Common.MSG_SEND_FAILED:
				mServmsg.sended(false);
				break;
			case Common.MSG_HINT:
				// login_ctx.hint(msg.arg1);
				break;
			case Common.MSG_REGISTER_CHECK:
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(NOT_AVAILABLE))
					sendRequest(Common.OPERATE_SAVE);
				else {
					// login_ctx.hint(R.string.already_registered);
				}
				break;
			case Common.MSG_OPEN_RECEIVE:
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(OPERATION_FAILED)) {
					// login_ctx.hint(R.string.server_fault);
				} else if (result.equalsIgnoreCase(NOT_AVAILABLE)) {
					// login_ctx.hint(R.string.not_register);
				} else {
					// login_ctx.authPassed();
				}
				break;
			case Common.MSG_SAVE_RECEIVE:
				// login_ctx.setRegisterBtn(R.string.register);
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(OPERATION_FAILED)) {
					// login_ctx.hint(R.string.save_failed);
				} else if (result.equalsIgnoreCase(NOT_AVAILABLE)) {
					// login_ctx.hint(R.string.not_available);
				} else {
					// login_ctx.authPassed();
				}
				break;
			// 否则提示失败
			case Common.MSG_FAILURE:
				Log.e(Common.TAG,
						"AuthenticationManager: Receive message failed.");
				break;
			}
		}
	};

}