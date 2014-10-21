package org.park.util;

public class Common {
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_HINT = 5;
	public static final int MESSAGE_CONNECT = 6;
	public static final int MESSAGE_CONNECT_SUCCEED = 7;
	public static final int MESSAGE_CONNECT_LOST = 8;
	public static final int MESSAGE_RECV = 9;
	public static final int MESSAGE_EXCEPTION_RECV = 10;
	public static final int MESSAGE_SHOW_DEVICES = 11;
	public static final int MESSAGE_AUTHORIZE_PASSED = 12;
	public static final int MESSAGE_START_DISCOVER = 13;
	public static final int MESSAGE_TARGET_FOUND = 14;
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

	public static final String HINT = "toast";
	public static final String TAG = "BlueToothTool";
	public static final String DEFAULT_PAIR_PASSWORD = "000000";
	public static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
	public static final String DEFAULT_PIN_CODE = "1234";
	public static final String DEFAULT_DEVICE_ADDR = "00:0E:0E:00:0F:53";
	public static final int RESPONSE_LENGTH = 16;
	public static final String IS_EXIT = "IS_EXIT";
}
