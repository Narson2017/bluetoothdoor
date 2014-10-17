package org.park.connection;

public interface HandleConnMsg {

	public void connected(boolean state);

	public void sended(boolean state);

	public void disconnected();

	public void pairing();

	public void paired(boolean state);

	public void discovery_stated();

	public void discovery_finished();

	public void receive_data(int res_id);
}
