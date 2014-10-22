package org.park.prefs;

import org.park.util.Common;

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

	public String getPhone() {
		return mPrefs.getString(Common.PREFERENCE_PHONE, "-1");
	}

	public String getPsw() {
		return mPrefs.getString(Common.PREFERENCE_PASSWORD,
				Common.DEFAULT_PAIR_PASSWORD);
	}

	public int getBox() {
		return Integer.valueOf(mPrefs.getString(Common.PREFERENCE_BOX, "-1"))
				.intValue();
	}

	public int getCabinet() {
		return Integer.valueOf(
				mPrefs.getString(Common.PREFERENCE_CABINET, "-1")).intValue();
	}
}
