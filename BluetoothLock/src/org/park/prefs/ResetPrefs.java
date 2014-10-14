package org.park.prefs;

import org.park.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class ResetPrefs extends DialogPreference {

	public ResetPrefs(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		// Set the layout here
		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);
		setTitle(R.string.reset);
		setDialogMessage(R.string.sure);
		setDialogIcon(null);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		// When the user selects "OK", persist the new value
		if (positiveResult) {
			// User selected OK
			SharedPreferences mPrefs = PreferenceManager
					.getDefaultSharedPreferences(getContext());
			mPrefs.edit().putString("username", "").commit();
			mPrefs.edit().putString("password", "").commit();
			mPrefs.edit().putString("locknbr", "").commit();
		} else {
			// User selected Cancel
		}
	}
}
