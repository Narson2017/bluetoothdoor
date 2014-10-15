package org.park.boxlst;

import java.util.ArrayList;
import java.util.List;

import org.park.R;
import org.park.authorize.AuthenticationManager;
import org.park.entrance.splashScreen;
import org.park.util.About;
import org.park.util.Quit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

public class BoxlstActivity extends Activity implements View.OnClickListener {
	AuthenticationManager mAuthMgr;
	ListView box_lv;
	BoxAdapter mBoxApt;
	List<MBox> box_lst = new ArrayList<MBox>();
	View l_lsboxes, l_progressBar1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.boxlst);

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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_exit:
			Quit.act_exit(this);
			break;
		case R.id.btn_about:
			About.ShowAbout(this);
			break;
		case R.id.btn_action_back:
		case R.id.btn_back:
			startActivity(new Intent(this, splashScreen.class));
			finish();
			break;
		}
	}
}
