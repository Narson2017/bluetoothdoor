package org.park.connection;

import java.util.ArrayList;

import org.park.util.Common;
import org.park.util.HexConvert;

import android.util.Log;

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

	int cabinet_id, box_id;

	public CommandMgr() {
		super();
	}

	public void setBoxnbr(int cabinet_id, int box_id) {
		this.cabinet_id = cabinet_id;
		this.box_id = box_id;
	}

	// get "password and algorithm type" command
	byte[] getPswAlg(String pairStr) {
		String tmp1 = "feef";
		String tmp2 = "10" + pairStr + "a2" + "000000000000"
				+ HexConvert.int2hexStr(cabinet_id)
				+ HexConvert.int2hexStr(box_id) + "00";
		String tmp3 = xor(tmp2);
		Log.i(Common.TAG, tmp1 + tmp2 + tmp3);
		return HexConvert.HexString2Bytes(tmp1 + tmp2 + tmp3);
	}

	public String xor(String tmp) {
		ArrayList<String> strs = new ArrayList<String>();
		for (int i = 0; i < tmp.length() / 2; i++) {
			strs.add(tmp.substring(2 * i, 2 * i + 2));
		}
		int result = 0;
		for (String hexStr : strs)
			result = Integer.valueOf(hexStr, 16).intValue() ^ result;
		return HexConvert.int2hexStr(result);
	}

	// get "open lock" command
	byte[] getOpenLockCommand(String pairStr, String receivedStr) {
		String tmp1 = "feef";
		String tmp2 = "0c" + pairStr + "a3" + calculateDynamicPsw(receivedStr)
				+ HexConvert.int2hexStr(cabinet_id)
				+ HexConvert.int2hexStr(box_id) + "00";
		String tmp3 = xor(tmp2);
		return HexConvert.HexString2Bytes(tmp1 + tmp2 + tmp3);
	}

	public byte[] getChangePairPsw(String pairPsw) {
		// TODO Auto-generated method stub
		return null;
	}

	public int checkRecvType(String tmp) {
		if (tmp.substring(6, 8).equalsIgnoreCase("b2"))
			if (tmp.substring(8, 10).equalsIgnoreCase("da")) {
				return CommandMgr.RECEIVE_DYNAMIC_PASSWORD_SUCCESS;
			} else {
				return CommandMgr.RECEIVE_DYNAMIC_PASSWORD_FAILED;
			}
		else if (tmp.substring(6, 8).equalsIgnoreCase("b1"))
			if (tmp.substring(8, 10).equalsIgnoreCase("da")) {
				return CommandMgr.RECEIVE_PAIR_PASSWORD_SUCCESS;
			} else {
				return CommandMgr.RECEIVE_PAIR_PASSWORD_FAILED;
			}

		return -1;
	}

	public String calculateDynamicPsw(String receivedStr) {
		String source = receivedStr.substring(10, 16);
		String result = "";

		if (receivedStr.substring(16, 18).equalsIgnoreCase("f1")) {
			ArrayList<String> strs = new ArrayList<String>();
			for (int i = 0; i < source.length() / 2; i++) {
				strs.add(source.substring(2 * i, 2 * i + 2));
			}
			int key = 0;
			for (String hexStr : strs)
				key = Integer.valueOf(hexStr, 16).intValue() ^ key;

			for (String hexStr : strs) {
				key = Integer.valueOf(hexStr, 16).intValue() ^ key;
				result += HexConvert.int2hexStr(key);
			}
		}
		return result;
	}
}