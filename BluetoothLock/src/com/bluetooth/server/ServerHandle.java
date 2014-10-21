package com.bluetooth.server;

public interface ServerHandle {
	public void sended(boolean done);

	public void received(String data);
}
