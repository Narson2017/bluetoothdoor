package org.park.authorize;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;
import org.park.R;
import org.park.util.Common;
import org.park.util.HexConvert;
import org.park.util.MDes;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AuthenticationManager {
	private static final String UTF_8 = "UTF-8";
	private static final String SOAP_ACTION = "";
	public static final int OPR_SAVE = 0;
	public static final int OPR_OPEN = 1;
	public static final int OPR_REGISTER = 2;

	private static final String FUNC_SAVE = "save_Lock";
	private static final String FUNC_OPEN = "open_lock";
	private static final String WSDL_TARGET_NAMESPACE = "http://web_test";
	private static final String SOAP_ADDRESS = "http://192.168.1.206:8092/web_test/services/web_test";

	private static final int MSG_FAILURE = 1;
	protected static final int MSG_OPEN_CHECK = 2;
	protected static final int MSG_SAVE_RECEIVE = 3;
	protected static final int MSG_OPEN_RECEIVE = 4;
	protected static final int MSG_HINT = 5;

	private static final String DES_CODE = "38374008";
	public static final String PHONE_NUMBER = "telNo";
	public static final String PASSWORD = "Bpwd";
	public static final String LOCK_NUMBER = "UID";

	protected String OPERATION_FAILED = "-1";
	protected String NOT_AVAILABLE = "0";
	private MsgManager mMsgMgr;

	private String phone_number = null, password = null;
	private int lock_number = -1;

	public AuthenticationManager(MsgManager ctx) {
		super();
		this.mMsgMgr = ctx;
	}

	private Handler mHandler = new Handler() {
		// ��дhandleMessage()�������˷�����UI�߳�����
		@Override
		public void handleMessage(Message msg) {
			String result = null;
			switch (msg.what) {
			case MSG_HINT:
				mMsgMgr.hint(msg.arg1);
				break;
			case MSG_OPEN_CHECK:
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(NOT_AVAILABLE))
					authSend(phone_number, password, OPR_SAVE);
				else {
					mMsgMgr.hint(R.string.already_registered);
					mMsgMgr.stopLoading();
				}
				break;
			case MSG_OPEN_RECEIVE:
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(OPERATION_FAILED)) {
					mMsgMgr.hint(R.string.server_fault);
					mMsgMgr.stopLoading();
				} else if (result.equalsIgnoreCase(NOT_AVAILABLE)) {
					mMsgMgr.hint(R.string.not_register);
					mMsgMgr.stopLoading();
				} else {
					mMsgMgr.unHint();
					mMsgMgr.authPassed(Integer.valueOf(result).intValue());
				}
				break;
			case MSG_SAVE_RECEIVE:
				mMsgMgr.setRegisterBtn(R.string.register);
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(OPERATION_FAILED)) {
					mMsgMgr.hint(R.string.save_failed);
					mMsgMgr.stopLoading();
				} else if (result.equalsIgnoreCase(NOT_AVAILABLE)) {
					mMsgMgr.hint(R.string.not_available);
					mMsgMgr.stopLoading();
				} else {
					mMsgMgr.unHint();
					mMsgMgr.authPassed(Integer.valueOf(result).intValue());
				}
				break;
			// ������ʾʧ��
			case MSG_FAILURE:
				Log.e(Common.TAG,
						"AuthenticationManager: Receive message failed.");
				break;
			}
		}
	};

	public void authSend(final String phone, final String psw,
			final int operation) {

		phone_number = phone;
		password = psw;

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
						SoapEnvelope.VER11);

				SoapObject request = (operation == OPR_SAVE) ? new SoapObject(
						WSDL_TARGET_NAMESPACE, FUNC_SAVE) : new SoapObject(
						WSDL_TARGET_NAMESPACE, FUNC_OPEN);
				// body
				request.addProperty(PHONE_NUMBER, phone);
				request.addProperty(PASSWORD, psw);
				request.addProperty(LOCK_NUMBER, String.valueOf(lock_number));
				envelope.setOutputSoapObject(request);

				// soap header
				Element[] header = new Element[1];
				header[0] = new Element().createElement(WSDL_TARGET_NAMESPACE,
						"RequestSOAPHeader");

				Element version = new Element().createElement(
						WSDL_TARGET_NAMESPACE, "version");
				version.addChild(Node.TEXT, String.valueOf(SoapEnvelope.VER11));
				header[0].addChild(Node.ELEMENT, version);

				Element messageId = new Element().createElement(
						WSDL_TARGET_NAMESPACE, "messageId");
				String mSequence = "11112121";
				messageId.addChild(Node.TEXT, mSequence);
				header[0].addChild(Node.ELEMENT, messageId);

				Element timestamp = new Element().createElement(
						WSDL_TARGET_NAMESPACE, "timestamp");
				String mTimestamp = new java.sql.Timestamp(Calendar
						.getInstance().getTimeInMillis()).toString();
				timestamp.addChild(Node.TEXT, mTimestamp);
				header[0].addChild(Node.ELEMENT, timestamp);

				Element userId = new Element().createElement(
						WSDL_TARGET_NAMESPACE, "userId");
				userId.addChild(Node.TEXT, String.valueOf(lock_number));
				header[0].addChild(Node.ELEMENT, userId);

				Element sign = new Element().createElement(
						WSDL_TARGET_NAMESPACE, "sign");
				byte[] tmp = null;
				try {
					tmp = MDes.desEncrypt((phone_number + password + String
							.valueOf(lock_number)).getBytes(UTF_8), DES_CODE);
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String ciphertext = HexConvert.Bytes2HexString(tmp, tmp.length);
				sign.addChild(Node.TEXT, ciphertext);
				header[0].addChild(Node.ELEMENT, sign);
				envelope.headerOut = header;

				HttpTransportSE httpTransport = new HttpTransportSE(
						SOAP_ADDRESS);
				SoapObject result = null;
				try {
					httpTransport.call(SOAP_ACTION, envelope);
					Log.i(Common.TAG, envelope.bodyIn.toString());
					result = (SoapObject) envelope.bodyIn;
				} catch (Exception e) {
					e.printStackTrace();
					// return;
				}
				if (result != null)
					switch (operation) {
					case OPR_REGISTER:
						mHandler.obtainMessage(MSG_OPEN_CHECK, result)
								.sendToTarget();
						break;
					case OPR_SAVE:
						mHandler.obtainMessage(MSG_SAVE_RECEIVE, result)
								.sendToTarget();
						break;
					case OPR_OPEN:
						mHandler.obtainMessage(MSG_OPEN_RECEIVE, result)
								.sendToTarget();
						break;
					}
				else
					mHandler.obtainMessage(MSG_HINT, R.string.server_fault, -1)
							.sendToTarget();
			}
		}).start();
	}
}