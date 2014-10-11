package org.park.bluetooth;

public interface ShowDetailer {
	public void connectDev(String addr, String name);
	public void hintNotFound();
	public void changeView(int boxes, int progress, int fault_tx, int fault);
}
