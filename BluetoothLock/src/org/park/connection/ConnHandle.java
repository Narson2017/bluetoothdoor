package org.park.connection;

public interface ConnHandle {

	public void connected(boolean state);

	public void sended(boolean state);

	public void disconnected();

	public void pairing();

	public void paired(boolean state);

	public void discovery_started();

	public void discovery_finished();

	public void received(String received_data);

	public void timeout();

	public void found(boolean state);
}