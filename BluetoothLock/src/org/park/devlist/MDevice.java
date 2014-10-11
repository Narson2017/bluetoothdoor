package org.park.devlist;

import org.park.bluetooth.R;

public class MDevice {
	public int index;
	public static final int ic_bar = R.drawable.ic_bar;
	public int ic_btn = R.drawable.btn_disconnect;
	public String dev_name;
	public String mac_addr;

	public MDevice() {
		super();
	}

	public MDevice(int index, String mac_addr, String dev) {
		super();
		this.index = index;
		this.dev_name = dev;
		this.mac_addr = mac_addr;
	}
}