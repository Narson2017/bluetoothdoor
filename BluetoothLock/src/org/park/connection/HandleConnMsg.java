package org.park.connection;

public interface HandleConnMsg {
	public void connect_state(boolean state);

	public void send_state(boolean state);
}
