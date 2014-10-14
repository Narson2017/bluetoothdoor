package org.park.prefs;

import org.park.R;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class settingActivity extends PreferenceActivity {
    @SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        addPreferencesFromResource(R.xml.preferences);
        
        Preference button = (Preference)getPreferenceManager().findPreference("save");      
        if (button != null) {
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                	Toast.makeText(settingActivity.this, R.string.save_success, Toast.LENGTH_SHORT).show();
                    finish();
                    return true;
                }
            });     
        }
    }
}