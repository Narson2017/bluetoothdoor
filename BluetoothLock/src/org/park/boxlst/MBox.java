package org.park.boxlst;

import android.widget.Button;

public class MBox {
	int box_nbr;
	public Button box_btn;

	public MBox(int box) {
		super();
		// TODO Auto-generated constructor stub
		box_nbr = box;
	}

	public int get_nbr() {
		// TODO Auto-generated method stub
		return box_nbr;
	}

	public void set_nbr(int nbr) {
		box_nbr = nbr;
	}
}
