package org.park.boxlst;

import java.util.ArrayList;
import java.util.List;

import org.park.R;
import org.park.authorize.AuthenticationManager;
import org.park.entrance.splashScreen;
import org.park.util.OnClickCtrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class BoxlstActivity extends Activity {
	AuthenticationManager mAuthMgr;
	ListView box_lv;
	BoxAdapter mBoxApt;
	List<MBox> box_lst = new ArrayList<MBox>();
	View l_lsboxes, l_progressBar1;
	OnClickCtrl mOnclickCtrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.boxlst);

		mOnclickCtrl = new OnClickCtrl(this);
		findViewById(R.id.btn_exit).setOnClickListener(mOnclickCtrl);
		findViewById(R.id.btn_about).setOnClickListener(mOnclickCtrl);
		findViewById(R.id.btn_action_back).setOnClickListener(mOnclickCtrl);
		findViewById(R.id.btn_back).setOnClickListener(mOnclickCtrl);
		findViewById(R.id.btn_setting).setOnClickListener(mOnclickCtrl);

		l_progressBar1 = findViewById(R.id.progressBar1);
		l_lsboxes = findViewById(R.id.l_lsboxes);

		box_lv = (ListView) this.findViewById(R.id.lvboxes);
		mBoxApt = new BoxAdapter(BoxlstActivity.this,
				R.layout.box_lst_item_row, box_lst);
		box_lv.setAdapter(mBoxApt);

		mAuthMgr = new AuthenticationManager(this);
		mAuthMgr.getAvailableBoxes();
	}

	public void initlst(int[] boxes_lst) {
		for (int box : boxes_lst) {
			box_lst.add(new MBox(1, box));
		}
		mBoxApt.notifyDataSetChanged();
		l_progressBar1.setVisibility(View.GONE);
		l_lsboxes.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(this, splashScreen.class));
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
