package org.park.connection;

public class CommandMgr {
	final String DEFAULT_PAIR_PASSWORD = "000000000000";
	String pairPsw = null;
	String receivedData = null;

	public CommandMgr() {
		super();
	}

	public CommandMgr(String pair_psw) {
		super();
		this.pairPsw = pair_psw;
	}

	public void setPairPsw(String pair_psw) {
		this.pairPsw = pair_psw;
	}

	// get "password and algorithm type" command
	byte[] getPswAlg(int cabinet_id, int box_id) {
		return null;
	}

	// check out does the content that received contain dynamic password
	boolean ifReceivedDyPsw() {
		return false;
	}

	// get "open lock" command
	byte[] getOpenLockCommand() {
		return null;
	}

	public void setRecivedData(String tmp) {
		receivedData = tmp;
	}

	public byte[] getChangePairPsw() {
		// TODO Auto-generated method stub
		return null;
	}
}
