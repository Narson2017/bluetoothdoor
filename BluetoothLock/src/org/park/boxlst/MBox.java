package org.park.boxlst;

import android.widget.Button;

public class MBox {
	public int box_id;
	public int cabinet_id;
	public Button box_btn;

	public MBox(int cabinet, int box) {
		super();
		// TODO Auto-generated constructor stub
		box_id = box;
		cabinet_id= cabinet;
	}

	public int get_nbr() {
		// TODO Auto-generated method stub
		return box_id;
	}

	public void set_nbr(int nbr) {
		box_id = nbr;
	}
}
