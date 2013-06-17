package com.techan.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.WindowManager;

import com.techan.R;

public class SettingsActivity extends Activity {
    public static final String AUTO_REFRESH_KEY = "autoRefresh";
    public static final String REFRESH_INTERVAL_KEY ="refreshInterval";
    public static final String ALL_NOTIFICATIONS_KEY = "allNotifications";
    public static final String PE_ENABLED_KEY ="peEnabled";
    public static final String PE_TARGET_KEY = "peTarget";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    private static void refreshMessage(String refreshValue, Preference preference) {
        if(refreshValue.equals("1"))
            preference.setSummary("Refresh every " + refreshValue + " hr");
        else
            preference.setSummary("Refresh every " + refreshValue + " hrs");
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);

            addPreferencesFromResource(R.xml.settings);

            final ListPreference refreshPreferenceList = (ListPreference)getPreferenceManager().findPreference(REFRESH_INTERVAL_KEY);

            refreshMessage(refreshPreferenceList.getValue(), refreshPreferenceList);

            refreshPreferenceList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    String refreshValue = (String) o;
                    refreshMessage(refreshValue, preference);
                    return true;
                }
            });


            final SwitchPreference allNotificationsPref = (SwitchPreference)getPreferenceManager().findPreference("allNotifications");
            final SwitchPreference peSwitchPreference = (SwitchPreference)getPreferenceManager().findPreference("peEnabled");
            final EditTextPreference peEditPreference = (EditTextPreference)getPreferenceManager().findPreference("peTarget");

            handleAllNotifications(peSwitchPreference, peEditPreference, allNotificationsPref);
            handlePeNotifications(peSwitchPreference, peEditPreference, allNotificationsPref);
        }
    }

    private static void handleAllNotifications(final SwitchPreference peSwitchPreference, final EditTextPreference peEditPreference, final SwitchPreference allNotificationsPref) {
        allNotificationsPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                peSwitchPreference.setEnabled((Boolean) o);
                peEditPreference.setEnabled((Boolean) o);
                return true;
            }
        });
    }

    private static void handlePeNotifications(final SwitchPreference peSwitchPreference, final EditTextPreference peEditPreference, final SwitchPreference allNotificationsPref) {

        if(!allNotificationsPref.isChecked()) {
            peSwitchPreference.setEnabled(false);
            peEditPreference.setEnabled(false);
        } else {
            peEditPreference.setEnabled(peSwitchPreference.isChecked());
        }
        peEditPreference.setSummary("Global PE target set to " + peEditPreference.getText());

        peSwitchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                peEditPreference.setEnabled((Boolean) o);
                return true;
            }
        });

        peEditPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                peEditPreference.setSummary("Global PE target set to " + Double.parseDouble((String) o));
                return true;
            }
        });

        peEditPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference pref) {
                EditTextPreference et = (EditTextPreference) pref;
                et.getEditText().setSelection(et.getText().length());
                return true;
            }
        });
    }
}
