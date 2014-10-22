package org.park.boxlst;

import java.util.ArrayList;
import java.util.List;

import org.park.R;
import org.park.entrance.Navigation;
import org.park.util.OnClickCtrl;
import org.park.util.Rotate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.bluetooth.server.BoxWarehouse;

public class BoxlstActivity extends Activity implements OnClickListener {
	ListView box_lv;
	BoxAdapter mBoxApt;
	List<MBox> box_lst = new ArrayList<MBox>();
	View l_lsboxes;
	OnClickCtrl mOnclickCtrl;
	AllBoxes mAllboxes;
	TextView text_hint;
	Rotate mRefresh;

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
		l_lsboxes = findViewById(R.id.l_lsboxes);
		text_hint = (TextView) findViewById(R.id.text_hint);
		box_lv = (ListView) this.findViewById(R.id.lvboxes);
		mBoxApt = new BoxAdapter(BoxlstActivity.this,
				R.layout.box_lst_item_row, box_lst);
		box_lv.setAdapter(mBoxApt);
		mRefresh = new Rotate(findViewById(R.id.btn_refresh),
				findViewById(R.id.refresh_view));

		mAllboxes = new AllBoxes();
		mAllboxes.getAvaiableBoxes();
		mRefresh.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			startActivity(new Intent(this, Navigation.class));
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	private class AllBoxes extends BoxWarehouse {
		@Override
		public void sended(boolean done) {
			// TODO Auto-generated method stub
			if (done)
				text_hint.setText(R.string.send_success);
			else
				text_hint.setText(R.string.send_failed);
		}

		@Override
		public void received(String data) {
			// TODO Auto-generated method stub
			if (data != null) {
				mRefresh.display(false);
				text_hint.setText(R.string.receive_success);
				for (int box : str2intlst(data))
					box_lst.add(new MBox(1, box));
				mBoxApt.notifyDataSetChanged();
				l_lsboxes.setVisibility(View.VISIBLE);
			} else {
				mRefresh.display(true);
				mRefresh.stop();
				text_hint.setText(R.string.server_fault);
			}
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_refresh:
		case R.id.btn_query:
			mAllboxes.getAvaiableBoxes();
			mRefresh.start();
			break;
		}
	}
}
