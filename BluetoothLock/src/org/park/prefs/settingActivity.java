package org.park.prefs;

import org.park.R;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

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
					finish();
					return true;
				}
			});
		}
	}
}