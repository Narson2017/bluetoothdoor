package com.bluetooth.connection;

import java.util.ArrayList;

import org.park.util.Common;
import org.park.util.HexConvert;

public class LockCommand {
	// get "password and algorithm type" command
	public static String getPasswordCmd(String pairStr, int cabinet_id,
			int box_id) {
		String tmp1 = "feef";
		String tmp2 = "10" + intStr6hexStr(pairStr) + "a2" + "000000000000"
				+ HexConvert.int2hexStr(cabinet_id)
				+ HexConvert.int2hexStr(box_id) + "00";
		String tmp3 = xor(tmp2);
		return tmp1 + tmp2 + tmp3;
	}

	public static String xor(String tmp) {
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
	public static String openCmd(String pairStr, int cabinet, int box,
			String receivedStr) {
		String tmp1 = "feef";
		String tmp2 = "10" + intStr6hexStr(pairStr) + "a3"
				+ calculatePassword(receivedStr)
				+ HexConvert.int2hexStr(cabinet) + HexConvert.int2hexStr(box)
				+ "00";
		String tmp3 = xor(tmp2);
		
		return tmp1 + tmp2 + tmp3;
	}

	public static String changePasswordCmd(String old_psw, String new_psw) {
		// TODO Auto-generated method stub
		String tmp1 = "feef";
		String tmp2 = "10" + intStr6hexStr(old_psw) + "a1"
				+ intStr6hexStr(new_psw) + HexConvert.int2hexStr(1)
				+ HexConvert.int2hexStr(1) + "00";
		String tmp3 = xor(tmp2);
		
		return tmp1 + tmp2 + tmp3;
	}

	// convert old_psw to 6 byte hexadecimal string
	public static String intStr6hexStr(String intStr) {
		String result = "";
		int i = 0;
		for (; i < intStr.length(); i++) {
			result += HexConvert.int2hexStr(Integer.valueOf(
					"0" + intStr.charAt(i)).intValue());
		}
		if (i < 5) {
			for (; i < 6; i++)
				result += "00";
		}
		return result;
	}

	public static int checkRecvType(String tmp) {
		if (tmp.substring(6, 8).equalsIgnoreCase("b2"))
			if (tmp.substring(8, 10).equalsIgnoreCase("da")) {
				return Common.RECEIVE_DYNAMIC_PASSWORD_SUCCESS;
			} else {
				return Common.RECEIVE_DYNAMIC_PASSWORD_FAILED;
			}
		else if (tmp.substring(6, 8).equalsIgnoreCase("b1"))
			if (tmp.substring(8, 10).equalsIgnoreCase("da")) {
				return Common.RECEIVE_PAIR_PASSWORD_SUCCESS;
			} else {
				return Common.RECEIVE_PAIR_PASSWORD_FAILED;
			}
		else if (tmp.substring(6, 8).equalsIgnoreCase("b3"))
			if (tmp.substring(8, 10).equalsIgnoreCase("da")) {
				return Common.RECEIVE_OPEN_DOOR_SUCCESS;
			} else {
				return Common.RECEIVE_OPEN_DOOR_FAILED;
			}
		else if (tmp.substring(6, 8).equalsIgnoreCase("b5"))
			if (tmp.substring(8, 10).equalsIgnoreCase("da")) {
				return Common.MSG_QUERY_SUCCESS;
			} else {
				return Common.MSG_QUERY_FAILED;
			}
		else if (tmp.substring(6, 8).equalsIgnoreCase("b4"))
			if (tmp.substring(8, 10).equalsIgnoreCase("da")) {
				return Common.MSG_OPEN_ALL_SUCCESS;
			} else {
				return Common.MSG_OPEN_ALL_FAILED;
			}
		return -1;
	}

	public static String calculatePassword(String receivedStr) {
		// dynamic source code
		String source = receivedStr.substring(10, 22);
		String result = "";
		
		// divide into byte and store in an array
		ArrayList<String> strs = new ArrayList<String>();
		for (int i = 0; i < source.length() / 2; i++) {
			strs.add(source.substring(2 * i, 2 * i + 2));
		}

		int key = 0;
		if (receivedStr.substring(22, 24).equalsIgnoreCase("f1")) {
			for (String hexStr : strs)
				key = Integer.valueOf(hexStr, 16).intValue() ^ key;

			for (String hexStr : strs) {
				key = Integer.valueOf(hexStr, 16).intValue() ^ key;
				result += HexConvert.int2hexStr(key);
			}
		} else if (receivedStr.substring(22, 24).equalsIgnoreCase("f2")) {
			for (int i = 0; i < strs.size() - 1; i += 2) {
				key = Integer.valueOf(strs.get(i), 16).intValue()
						^ Integer.valueOf(strs.get(i + 1), 16).intValue();
				result += HexConvert.int2hexStr(Integer
						.valueOf(strs.get(i), 16).intValue() ^ key);
				result += HexConvert.int2hexStr(Integer.valueOf(
						strs.get(i + 1), 16).intValue()
						^ key);
			}
		}
		return result;
	}

	public static String openAllCmd(String pair_password, int cabinet, int box,
			String receivedStr) {
		String tmp1 = "feef";
		String tmp2 = "10" + intStr6hexStr(pair_password) + "a4"
				+ calculatePassword(receivedStr)
				+ HexConvert.int2hexStr(cabinet) + HexConvert.int2hexStr(box)
				+ "00";
		String tmp3 = xor(tmp2);
		
		return tmp1 + tmp2 + tmp3;
	}

	public static String queryCmd(String pair_password, int cabinet, int box,
			String receivedStr) {
		String tmp1 = "feef";
		String tmp2 = "10" + intStr6hexStr(pair_password) + "a5"
				+ calculatePassword(receivedStr)
				+ HexConvert.int2hexStr(cabinet) + HexConvert.int2hexStr(box)
				+ "00";
		String tmp3 = xor(tmp2);
		
		return tmp1 + tmp2 + tmp3;
	}
}