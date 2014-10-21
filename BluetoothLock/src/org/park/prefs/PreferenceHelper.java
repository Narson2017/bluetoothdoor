package org.park.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
	Context mctx;
	SharedPreferences mPrefs;

	public PreferenceHelper(Context c) {
		mctx = c;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(c);
	}

	public void save(String phone, String password, int box, int cabinet) {
		mPrefs.edit().putString("username", phone).commit();
		mPrefs.edit().putString("password", password).commit();
		mPrefs.edit().putString("locknbr", String.valueOf(box)).commit();
		mPrefs.edit().putString("cabinet", String.valueOf(cabinet)).commit();
	}
}
