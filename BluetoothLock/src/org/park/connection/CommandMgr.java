package org.park.connection;

import java.util.ArrayList;

import org.park.util.HexConvert;

public class CommandMgr {
	static final String DEFAULT_PAIR_PASSWORD = "000000000000";
	static public final int RECEIVE_PAIR_PASSWORD_SUCCESS = 0;
	static public final int RECEIVE_DYNAMIC_PASSWORD_SUCCESS = 1;
	static public final int RECEIVE_OPEN_DOOR_SUCCESS = 2;
	static public final int RECEIVE_CLOSE_DOOR_SUCCESS = 3;
	static public final int RECEIVE_PAIR_PASSWORD_FAILED = 4;
	static public final int RECEIVE_DYNAMIC_PASSWORD_FAILED = 5;
	static public final int RECEIVE_OPEN_DOOR_FAILED = 6;
	static public final int RECEIVE_CLOSE_DOOR_FAILED = 7;

	public CommandMgr() {
		super();
	}

	// get "password and algorithm type" command
	byte[] getPswAlg(String pairStr, int cabinet_id, int box_id) {
		String tmp1 = "feef";
		String tmp2 = "10" + pairStr + "a1" + "000000000000"
				+ HexConvert.int2hexStr(cabinet_id)
				+ HexConvert.int2hexStr(box_id) + "00";
		String tmp3 = xor(tmp2);

		return HexConvert.HexString2Bytes(tmp1 + tmp2 + tmp3);
	}

	String xor(String tmp) {
		ArrayList<String> strs = new ArrayList<String>();
		for (int i = 0; i < tmp.length() / 2; i++) {
			strs.add(tmp.substring(2 * i, 2 * i + 1));
		}
		int result = 0;
		for (String hexStr : strs)
			result = Integer.valueOf(hexStr, 16).intValue() ^ result;
		return Integer.toHexString(result);
	}

	// get "open lock" command
	byte[] getOpenLockCommand(String receivedStr) {
		return null;
	}

	public byte[] getChangePairPsw(String pairPsw) {
		// TODO Auto-generated method stub
		return null;
	}

	public int checkRecvType(String tmp) {
		return -1;
	}
}