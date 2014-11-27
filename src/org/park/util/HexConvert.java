package org.park.util;

import java.io.ByteArrayOutputStream;

public class HexConvert {
	private static String hexString = "0123456789ABCDEF";

	public static byte[] HexString2Bytes(String src) {
		if (null == src || 0 == src.length()) {
			return null;
		}
		byte[] ret = new byte[src.length() / 2];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < (tmp.length / 2); i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}

	/*
	 * ���ַ��������16��������,�����������ַ����������ģ�
	 */
	public static String encode(String str) {
		// ����Ĭ�ϱ����ȡ�ֽ�����
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// ���ֽ�������ÿ���ֽڲ���2λ16��������
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * ��16�������ֽ�����ַ���,�����������ַ����������ģ�
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// ��ÿ2λ16����������װ��һ���ֽ�
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}

	private static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
				.byteValue();
		byte ret = (byte) (_b0 | _b1);
		return ret;
	}

	/**
	 * 
	 * @param b
	 *            byte[]
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b, int length) {
		String ret = "";
		for (int i = 0; i < length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	/**
	 * ��ָ��byte������16���Ƶ���ʽ��ӡ������̨
	 * 
	 * @param hint
	 *            String
	 * @param b
	 *            byte[]
	 * @return void
	 */
	public static void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	public static String hexStr2binaryStr(String hexString) {
		if (hexString == null)
			return null;
		String bString = "", tmp;
		int mLen;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = Integer.toBinaryString(Integer.parseInt(
					hexString.substring(i, i + 1), 16));
			mLen = tmp.length();
			if (mLen < 4) {
				for (; mLen < 4; mLen++)
					tmp = "0" + tmp;
			}
			bString += tmp;
		}
		return bString;
	}

	// integer to hexadecimal(two digit, replenish 0)
	public static String int2hexStr(int tmp) {
		if (tmp < 0)
			return "";
		String hexStr = Integer.toHexString(tmp);
		if (hexStr.length() < 2)
			hexStr = "0" + hexStr;
		return hexStr;
	}

	public static String bytesToString(byte[] b, int length) {
		StringBuffer result = new StringBuffer("");
		for (int i = 0; i < length; i++) {
			result.append((char) (b[i]));
		}

		return result.toString();
	}
}