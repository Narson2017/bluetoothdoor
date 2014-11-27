package com.bluetooth.admin;

import java.util.ArrayList;
import java.util.List;

import org.park.R;
import org.park.boxlst.MBox;
import org.park.prefs.PreferenceHelper;
import org.park.prefs.SettingActivity;
import org.park.util.Common;
import org.park.util.HexConvert;
import org.park.util.Quit;
import org.park.util.Rotate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bluetooth.connection.ConnHandle;
import com.bluetooth.connection.Connecter;
import com.bluetooth.connection.LockCommand;

public class AdminActivity extends Activity implements OnClickListener {
	Connecter mConnecter;
	PreferenceHelper mPrefs;
	static int box, cabinet;
	String pairPassword;
	TextView tx_fault;
	Rotate mRefresh;
	Button btn_connect, btn_refresh;
	int operation;
	List<MBox> box_lst = new ArrayList<MBox>();
	ListView box_lv;
	LockLstAdapter mBoxApt;
	final int[] state_sequence = { 7, 6, 5, 4, 3, 2, 1, 0, 15, 14, 13, 12, 11,
			10, 9, 8, 23, 22 };
	View l_lsboxes;
	private static final int DIALOG_PASS = 1;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.admin);
		// password
		showDialog(DIALOG_PASS);

		// initial data
		mConnecter = new Connecter(new saber(), this);
		mPrefs = new PreferenceHelper(this);
		box = 0;
		cabinet = 1;
		pairPassword = mPrefs.getPsw();
		tx_fault = (TextView) findViewById(R.id.text_hint);
		btn_connect = (Button) findViewById(R.id.btn_query);
		btn_refresh = (Button) findViewById(R.id.btn_refresh);
		mRefresh = new Rotate(btn_refresh, findViewById(R.id.refresh_view),this);
		box_lv = (ListView) this.findViewById(R.id.lvboxes);
		l_lsboxes = findViewById(R.id.l_lsboxes);
		for (int i = 0; i < Common.BOXES_AMOUNT; i++)
			box_lst.add(new MBox(1, i + 1));
		mBoxApt = new LockLstAdapter(this, R.layout.box_lst_item_row_admin,
				box_lst);
		box_lv.setAdapter(mBoxApt);
		box_lv.setOnItemClickListener(mBoxApt);

		// start
		btn_refresh.setEnabled(false);
		// l_lsboxes.setVisibility(View.INVISIBLE);
		mRefresh.start();
		mConnecter.start();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog = new Dialog(AdminActivity.this);
		switch (id) {
		case DIALOG_PASS:
			dialog.setCancelable(false);
			dialog.setTitle(R.string.password);
			dialog.setContentView(R.layout.dialog_admin_pass);
			final EditText edit_pass = (EditText) dialog
					.findViewById(R.id.edit_psw);
			Button positive = (Button) dialog.findViewById(R.id.btn_ok);
			Button negative = (Button) dialog.findViewById(R.id.btn_cancel);
			positive.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					if (edit_pass.getText().toString()
							.equalsIgnoreCase(Common.ADMIN_PASS)) {
						dialog.dismiss();
					} else {
						Animation shake = AnimationUtils.loadAnimation(
								AdminActivity.this, R.anim.shake);
						edit_pass.startAnimation(shake);
					}
				}
			});
			negative.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					AdminActivity.this.finish();
				}
			});
		}
		return dialog;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_exit:
			mConnecter.clean();
			mRefresh.stop();
			if (!mConnecter.if_receiving)
				Quit.quit(this);
			break;
		case R.id.btn_open_all:
			if (mConnecter.if_connected) {
				mConnecter.send(LockCommand.getPasswordCmd(pairPassword,
						cabinet, box));
				operation = Common.OPERATE_ALL_BOXES;
			} else
				mConnecter.start();
			break;
		case R.id.btn_back:
			mConnecter.clean();
			mRefresh.stop();
			if (!mConnecter.if_receiving)
				finish();
			break;
		case R.id.btn_setting:
			startActivity(new Intent(this, SettingActivity.class));
			break;
		case R.id.btn_refresh:
		case R.id.btn_query:
			btn_refresh.setEnabled(false);
			l_lsboxes.setVisibility(View.INVISIBLE);
			mRefresh.start();
			if (mConnecter.if_connected) {
				mConnecter.send(LockCommand.getPasswordCmd(pairPassword,
						cabinet, box));
				operation = Common.OPERATION_QUERY;
			} else {
				mConnecter.start();
			}
			break;
		}
	}

	private class saber implements ConnHandle {

		@Override
		public void connected(boolean state) {
			// TODO Auto-generated method stub
			tx_fault.setText(R.string.connect_success);
			mRefresh.display(false);
			l_lsboxes.setVisibility(View.VISIBLE);
			// mConnecter.send(LockCommand.getPasswordCmd(pairPassword, cabinet,
			// box));
			// operation = Common.OPERATION_QUERY;
		}

		@Override
		public void sended(boolean state) {
			// TODO Auto-generated method stub
			if (state)
				tx_fault.setText(R.string.send_success);
			else
				tx_fault.setText(R.string.send_failed);
		}

		@Override
		public void disconnected() {
			// TODO Auto-generated method stub
			tx_fault.setText(R.string.connect_failed);
			mRefresh.display(true);
			btn_refresh.setEnabled(true);
			l_lsboxes.setVisibility(View.INVISIBLE);
		}

		@Override
		public void pairing() {
			// TODO Auto-generated method stub

		}

		@Override
		public void paired(boolean state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void searching() {
			// TODO Auto-generated method stub

		}

		@Override
		public void searched() {
			// TODO Auto-generated method stub

		}

		@Override
		public void received(String received_data) {
			// TODO Auto-generated method stub
			if (received_data != null) {
				switch (LockCommand.checkRecvType(received_data)) {
				case Common.RECEIVE_DYNAMIC_PASSWORD_SUCCESS:
					mHandler.sendMessageDelayed(
							mHandler.obtainMessage(operation, received_data),
							512);
					break;
				case Common.RECEIVE_DYNAMIC_PASSWORD_FAILED:
					tx_fault.setText(R.string.device_return_wrong);
					break;
				case Common.RECEIVE_OPEN_DOOR_SUCCESS:
					tx_fault.setText(R.string.open_door_success);
					mBoxApt.notifyDataSetChanged();
					break;
				case Common.RECEIVE_OPEN_DOOR_FAILED:
					tx_fault.setText(R.string.open_door_failed);
					break;
				case Common.RECEIVE_CLOSE_DOOR_SUCCESS:
					tx_fault.setText(R.string.close_door_success);
					break;
				case Common.RECEIVE_CLOSE_DOOR_FAILED:
					tx_fault.setText(R.string.close_door_failed);
					break;
				case Common.MSG_QUERY_SUCCESS:
					String lock_state = HexConvert
							.hexStr2binaryStr(received_data.substring(24, 30));
					String empty_state = HexConvert
							.hexStr2binaryStr(received_data.substring(30, 36));
					for (int i = 0; i < state_sequence.length; i++) {
						box_lst.get(i).if_locked = lock_state
								.charAt(state_sequence[i]) == '1' ? true
								: false;
						box_lst.get(i).if_empty = empty_state
								.charAt(state_sequence[i]) == '1' ? true
								: false;
					}
					mBoxApt.notifyDataSetChanged();
					mRefresh.display(false);
					l_lsboxes.setVisibility(View.VISIBLE);
					btn_refresh.setEnabled(true);
					tx_fault.setText(R.string.query_success);
					break;
				case Common.MSG_QUERY_FAILED:
					break;
				case Common.MSG_OPEN_ALL_SUCCESS:
					tx_fault.setText(R.string.open_door_success);
					mBoxApt.notifyDataSetChanged();
					break;
				case Common.MSG_OPEN_ALL_FAILED:
					tx_fault.setText(R.string.open_door_failed);
					break;
				}
			} else
				tx_fault.setText(R.string.receive_failed);
		}

		@Override
		public void timeout() {
			// TODO Auto-generated method stub

		}

		@Override
		public void found(boolean state) {
			// TODO Auto-generated method stub

		}

		@Override
		public void disconnecting() {
			// TODO Auto-generated method stub
			tx_fault.setText(R.string.disconnecting);
		}
	}

	private class LockLstAdapter extends ArrayAdapter<MBox> implements
			AdapterView.OnItemClickListener {
		Context context;
		int resource;
		List<MBox> objects;

		public LockLstAdapter(Context context, int resource, List<MBox> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			this.context = context;
			this.resource = resource;
			this.objects = objects;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (arg2 != ListView.INVALID_POSITION) {
				// DO THE STUFF YOU WANT TO DO WITH THE position
				MBox mBox = objects.get(arg2);
				operation = Common.OPERATE_OPEN;
				cabinet = mBox.cabinet;
				box = mBox.box;
				mBox.if_locked = false;
				mConnecter.send(LockCommand.getPasswordCmd(pairPassword,
						mBox.cabinet, mBox.box));
			}
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View row = convertView;
			BoxHolder holder = null;
			final MBox mBox = objects.get(position);

			if (row == null) {
				LayoutInflater inflater = ((Activity) context)
						.getLayoutInflater();
				row = inflater.inflate(resource, parent, false);

				holder = new BoxHolder();
				holder.tx_box_nbr = (TextView) row
						.findViewById(R.id.tx_box_nbr);
				holder.lock_state = (ImageView) row
						.findViewById(R.id.imageView1);
				holder.tx_empty = (TextView) row.findViewById(R.id.textView1);
				row.setTag(holder);
			} else {
				holder = (BoxHolder) row.getTag();
			}
			holder.tx_box_nbr.setText(String.valueOf(mBox.get_nbr()));
			if (mBox.if_locked)
				holder.lock_state.setImageResource(R.drawable.ic_locked);
			else
				holder.lock_state.setImageResource(R.drawable.ic_unlocked);
			if (mBox.if_empty)
				holder.tx_empty.setText(R.string.available);
			else
				holder.tx_empty.setText(R.string.filled);
			return row;
		}
	}

	static class BoxHolder {
		TextView tx_box_nbr;
		TextView tx_empty;
		ImageView lock_state;
	}

	@Override
	protected void onDestroy() {
		mRefresh.stop();
		mConnecter.clean();
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mConnecter.clean();
			mRefresh.stop();
			if (!mConnecter.if_receiving)
				finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// Hander
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String received_data = (String) msg.obj;
			switch ((int) msg.what) {
			case Common.OPERATION_QUERY:
				mConnecter.send(LockCommand.queryCmd(pairPassword, cabinet,
						box, received_data));
				break;
			case Common.OPERATE_ALL_BOXES:
				mConnecter.send(LockCommand.openAllCmd(pairPassword, cabinet,
						box, received_data));
				break;
			case Common.OPERATE_OPEN:
				mConnecter.send(LockCommand.openCmd(pairPassword, cabinet, box,
						received_data));
				break;
			}
		}
	};
}