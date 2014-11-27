package org.park.boxlst;

import android.widget.Button;

public class MBox {
	public int box;
	public int cabinet;
	public Button box_btn;
	public boolean if_locked;
	public boolean if_empty;

	public MBox(int cabinet, int box) {
		super();
		// TODO Auto-generated constructor stub
		this.box = box;
		this.cabinet = cabinet;
		if_locked = true;
		if_empty = true;
	}

	public int get_nbr() {
		// TODO Auto-generated method stub
		return box;
	}

	public void set_nbr(int nbr) {
		box = nbr;
	}
}
