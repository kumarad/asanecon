package com.techan.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.EditText;

import com.techan.R;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);

            addPreferencesFromResource(R.xml.settings);

            final SwitchPreference switchPreference = (SwitchPreference)getPreferenceManager().findPreference("peEnabled");
            final EditTextPreference editPreference = (EditTextPreference)getPreferenceManager().findPreference("peTarget");

            editPreference.setEnabled(switchPreference.isChecked());
            editPreference.setSummary("Global PE target set to " + editPreference.getText());

            switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    editPreference.setEnabled((Boolean) o);
                    return true;
                }
            });

            editPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    editPreference.setSummary("Global PE target set to " + Double.parseDouble((String) o));
                    return true;
                }
            });

            editPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference pref) {
                    EditTextPreference et = (EditTextPreference)pref;
                    et.getEditText().setSelection(et.getText().length());
                    return true;
                }
            });

        }
    }

    // Called before the activity is put in a background state. Save stuff in the bundle.
    // When the activity comes back to the foreground it is passed to onCreate to help recreate
    // the state of the activity.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save stuff here.
    }
}
