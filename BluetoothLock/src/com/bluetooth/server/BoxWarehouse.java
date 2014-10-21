package com.bluetooth.server;

import java.util.ArrayList;

import org.park.util.Common;

import android.util.Log;

public class BoxWarehouse implements ServerHandle {
	ServerConn mServer;

	public BoxWarehouse() {
		mServer = new ServerConn(this);
	}

	public void getAvaiableBoxes() {
		mServer.sendRequest(Common.OPERATE_ALL_BOXES);
	}

	public void registerBox(String phone, String password, int cabinet, int box) {
		mServer.setCabinet(cabinet);
		mServer.setLock(box);
		mServer.setPhone(phone);
		mServer.setPsw(password);
		mServer.sendRequest(Common.OPERATE_REGISTER);
	}

	public void obtainBox() {

	}

	public static ArrayList<Integer> str2intlst(String strlst) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (String tmp : strlst.split(Common.DELIMITER))
			result.add(Integer.valueOf(tmp));
		return result;
	}

	@Override
	public void sended(boolean done) {
		// TODO Auto-generated method stub
		if (done)
			Log.i(Common.TAG, "Send request success.");
		else
			Log.i(Common.TAG, "Send request failed.");
	}

	@Override
	public void received(String data) {
		// TODO Auto-generated method stub
		if (data != null)
			Log.i(Common.TAG, "Received from server: " + data);
		else
			Log.i(Common.TAG, "Received failed.");
	}
}
