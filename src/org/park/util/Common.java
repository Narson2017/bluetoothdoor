package org.park.util;

public class Common {
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_HINT = 5;
	public static final int MESSAGE_CONNECT = 6;
	public static final int MESSAGE_CONNECT_SUCCEED = 7;
	public static final int MESSAGE_CONNECT_FAILED = 8;
	public static final int MESSAGE_RECV = 9;
	public static final int MESSAGE_EXCEPTION_RECV = 10;
	public static final int MESSAGE_SHOW_DEVICES = 11;
	public static final int MESSAGE_AUTHORIZE_PASSED = 12;
	public static final int MESSAGE_START_SEARCHING = 13;
	public static final int MSG_LOADING = 15;
	public static final int MSG_SERVER_FAULT = 16;
	public static final int MSG_UPDATE_SUCCESS = 17;
	public static final int MSG_RETURN_INDEX = 18;
	static public final int RECEIVE_PAIR_PASSWORD_SUCCESS = 19;
	static public final int RECEIVE_DYNAMIC_PASSWORD_SUCCESS = 20;
	static public final int RECEIVE_OPEN_DOOR_SUCCESS = 21;
	static public final int RECEIVE_CLOSE_DOOR_SUCCESS = 22;
	static public final int RECEIVE_PAIR_PASSWORD_FAILED = 23;
	static public final int RECEIVE_DYNAMIC_PASSWORD_FAILED = 24;
	static public final int RECEIVE_OPEN_DOOR_FAILED = 25;
	static public final int RECEIVE_CLOSE_DOOR_FAILED = 26;
	public static final int MSG_LOGIN_LOADING = 27;
	public static final int MSG_REGISTER_LOADING = 28;
	public static final int MSG_TIME_OUT = 29;
	public static final int MSG_FAILURE = 30;
	public static final int MSG_REGISTER_CHECK = 31;
	public static final int MSG_SAVE_RECEIVE = 32;
	public static final int MSG_OPEN_RECEIVE = 33;
	public static final int MSG_HINT = 34;
	public static final int OPERATE_SAVE = 35;
	public static final int OPERATE_OPEN = 36;
	public static final int OPERATE_REGISTER = 37;
	public static final int OPERATE_ALL_BOXES = 38;
	public static final int MSG_RECEIVE_FAILED = 39;
	public static final int MSG_RECEIVE_SUCCESS = 40;
	public static final int MSG_SEND_SUCCESS = 41;
	public static final int MSG_SEND_FAILED = 42;
	public static final int MSG_DELAY_CLEAN = 43;
	public static final int MSG_FOUND = 44;
	public static final int MSG_CONNECTED = 45;
	public static final int MSG_SENDED = 46;
	public static final int MSG_DISCONNECTED = 47;
	public static final int MSG_SEARCHING = 48;
	public static final int MSG_SEARCHED = 49;
	public static final int MSG_RECEIVED = 50;
	public static final int MSG_DISCONNECTING = 51;
	public static final int MSG_OPEN_ALL_SUCCESS = 52;
	public static final int MSG_OPEN_ALL_FAILED = 53;
	public static final int MSG_QUERY_SUCCESS = 54;
	public static final int MSG_QUERY_FAILED = 55;
	public static final int OPERATION_QUERY = 56;
	public static final int OPERATION_OPEN = 57;
	public static final int OPERATION_QUERY_ALL = 58;

	public static final String HINT = "toast";
	public static final String TAG = "BlueToothTool";
	public static final String DEFAULT_PAIR_PASSWORD = "000000";
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	public static final String DEFAULT_PIN_CODE = "1234";
	public static final String DEFAULT_DEVICE_ADDR = "00:0E:0E:00:0F:53";
	public static final int RESPONSE_LENGTH = 20;
	public static final String IS_EXIT = "IS_EXIT";
	public static final int TIME_OUT = 6;
	public static final String UTF_8 = "UTF-8";
	public static final String SOAP_ACTION = "";
	public static final String FUNC_SAVE = "save_Lock";
	public static final String FUNC_OPEN = "open_lock";
	public static final String FUNC_ALL_BOXES = "allowLockes";
	public static final String WSDL_TARGET_NAMESPACE = "http://web_test";
	public static final String SOAP_ADDRESS = "http://192.168.1.206:8092/web_test/services/web_test";
	public static final String DES_CODE = "38374008";
	public static final String PHONE_NUMBER = "telNo";
	public static final String PASSWORD = "Bpwd";
	public static final String BOX = "LockNo";
	public static final String CABINET = "UID";
	public static final int DEFAULT_CABINET = 1;
	public static final int DEFAULT_BOX = 1;
	public static final String DELIMITER = "#";
	public static final int RESULT_FAULT = -1;
	public static final int RESULT_NOT_FOUND = 0;
	public static final String PREFERENCE_PHONE = "username";
	public static final String PREFERENCE_PASSWORD = "password";
	public static final String PREFERENCE_BOX = "locknbr";
	public static final String PREFERENCE_CABINET = "cabinet";
	public static final int DELAY_TIME = 1024;
	public static final int ROTATE_STEP = 8;
	public static final int ROTATE_DELAY_TIME = 32;
	public static final int ROTATE_TIME_OUT = 32;
	public static final int LIMITE_SECOND = 1800;
	public static final int SYSTEM_WAITE = 512;
	public static final int RECEIVE_INTERVAL = 128;
	public static final int DECRYPTION_FAULT = -2;
	public static final int BOXES_AMOUNT = 18;
	public static final String ADMIN_PASS = "456987";
}
