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
import org.park.boxlst.BoxlstActivity;
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
	protected static final int MSG_REGISTER_CHECK = 2;
	protected static final int MSG_SAVE_RECEIVE = 3;
	protected static final int MSG_OPEN_RECEIVE = 4;
	protected static final int MSG_HINT = 5;

	private static final String DES_CODE = "38374008";
	public static final String PHONE_NUMBER = "telNo";
	public static final String PASSWORD = "Bpwd";
	public static final String LOCK_NUMBER = "UID";

	protected String OPERATION_FAILED = "-1";
	protected String NOT_AVAILABLE = "0";

	private BoxlstActivity boxlst_ctx;
	private LoginActivity login_ctx;

	private String phone_number = null, password = null;
	private int lock_number = -1;

	public AuthenticationManager(LoginActivity ctx) {
		super();
		login_ctx = ctx;
	}

	public AuthenticationManager(BoxlstActivity ctx) {
		super();
		this.boxlst_ctx = ctx;
	}

	private Handler mHandler = new Handler() {
		// 重写handleMessage()方法，此方法在UI线程运行
		@Override
		public void handleMessage(Message msg) {
			String result = null;
			switch (msg.what) {
			case MSG_HINT:
				login_ctx.hint(msg.arg1);
				break;
			case MSG_REGISTER_CHECK:
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(NOT_AVAILABLE))
					authSend(phone_number, password, login_ctx.cabinet,
							login_ctx.box, OPR_SAVE);
				else {
					login_ctx.hint(R.string.already_registered);
				}
				break;
			case MSG_OPEN_RECEIVE:
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(OPERATION_FAILED)) {
					login_ctx.hint(R.string.server_fault);
				} else if (result.equalsIgnoreCase(NOT_AVAILABLE)) {
					login_ctx.hint(R.string.not_register);
				} else {
					login_ctx.authPassed();
				}
				break;
			case MSG_SAVE_RECEIVE:
				login_ctx.setRegisterBtn(R.string.register);
				result = ((SoapObject) msg.obj).getProperty(0).toString();
				if (result.equalsIgnoreCase(OPERATION_FAILED)) {
					login_ctx.hint(R.string.save_failed);
				} else if (result.equalsIgnoreCase(NOT_AVAILABLE)) {
					login_ctx.hint(R.string.not_available);
				} else {
					login_ctx.authPassed();
				}
				break;
			// 否则提示失败
			case MSG_FAILURE:
				Log.e(Common.TAG,
						"AuthenticationManager: Receive message failed.");
				break;
			}
		}
	};

	public void authSend(final String phone, final String psw, int cabinet,
			int box, final int operation) {

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
						mHandler.obtainMessage(MSG_REGISTER_CHECK, result)
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

	public void getAvailableBoxes() {
		int[] tmp = {1, 2, 4, 6, 8, 10 };
		boxlst_ctx.initlst(tmp);
	}

	public void register(String username, String password, int cabinet, int box) {
		// authSend(username, password, cabinet, box, OPR_REGISTER);
		login_ctx.authPassed();
	}

	public void login(String username, String password) {
		// TODO Auto-generated method stub
//		authSend(username, password, -1, -1, OPR_OPEN);
		login_ctx.authPassed();
	}
}