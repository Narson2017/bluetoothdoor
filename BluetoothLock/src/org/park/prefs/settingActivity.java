package org.park.prefs;

import org.park.R;
import org.park.account.AccountActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class settingActivity extends PreferenceActivity {
	public static String BOX = "box";
	public static String CABINET = "cabinet";
	public static String USERNAME = "username";
	public static String PASSWORD = "password";

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		addPreferencesFromResource(R.xml.preferences);

		Preference button = (Preference) getPreferenceManager().findPreference(
				"save");
		if (button != null) {
			button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					SharedPreferences _sharedPreferences = PreferenceManager
							.getDefaultSharedPreferences(getBaseContext());
					String box = _sharedPreferences.getString("locknbr", "");
					String cabinet = _sharedPreferences
							.getString("cabinet", "");
					String username = _sharedPreferences.getString("username",
							"");
					String password = _sharedPreferences.getString("password",
							"");
					Intent mIntent = new Intent(settingActivity.this,
							AccountActivity.class);
					mIntent.putExtra(BOX, box.equals("") ? -1 : Integer
							.valueOf(box).intValue());
					mIntent.putExtra(CABINET, cabinet.equals("") ? -1 : Integer
							.valueOf(cabinet).intValue());
					mIntent.putExtra(USERNAME, username);
					mIntent.putExtra(PASSWORD, password);
					settingActivity.this.startActivity(mIntent);
					return true;
				}
			});
		}
	}
}