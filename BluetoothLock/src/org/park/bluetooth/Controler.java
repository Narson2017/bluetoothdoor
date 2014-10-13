package org.park.bluetooth;

public interface Controler {
	public void connectDev(String addr, String name);
	public void changeView(int boxes, int progress, int fault_tx);
}
