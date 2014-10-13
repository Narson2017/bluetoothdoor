package org.park.connection;

public class CommandMgr {
	String pairPsw = "000000000000";

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
	byte[] getPswAlg(String cabinet_id, String box_id) {
		return null;
	}

	// check out does the content that received contain dynamic password
	boolean ifReceivedDyPsw() {
		return false;
	}

	// get "open lock" command
	byte[] getOpenLockCommand(String received_str) {
		return null;
	}
}
