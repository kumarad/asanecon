package com.techan.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.techan.R;
import com.techan.alarm.AlarmReceiver;

public class SettingsActivity extends Activity {
    public static final String AUTO_REFRESH_KEY = "autoRefresh";
    public static final String REFRESH_INTERVAL_KEY ="refreshInterval";
    public static final String REFRESH_WIFI_ONLY_KEY = "refreshWifiOnly";
    public static final String SHOW_COST_BASIS = "showCostBasis";
    public static final int STOP_LOSS_DEFAULT = 25;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

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

            final SwitchPreference refreshPref = (SwitchPreference) getPreferenceManager().findPreference(AUTO_REFRESH_KEY);
            final ListPreference refreshPreferenceList = (ListPreference)getPreferenceManager().findPreference(REFRESH_INTERVAL_KEY);
            handleRefreshes(getActivity().getApplicationContext(), refreshPref, refreshPreferenceList);
        }
    }

    private static void handleRefreshes(final Context appContext, final SwitchPreference refreshPref,
                                        final ListPreference refreshPreferenceList) {
        refreshMessage(refreshPreferenceList.getValue(), refreshPreferenceList);

        refreshPreferenceList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                String refreshValue = (String) o;
                refreshMessage(refreshValue, preference);
                if(refreshPref.isChecked()) {
                    AlarmReceiver.cancelAutoRefresh(appContext);
                    AlarmReceiver.setAutoRefresh(appContext, Integer.parseInt(refreshValue));
                }
                return true;
            }
        });

        refreshPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if((Boolean)o) {
                    // Turn auto refresh on.
                    AlarmReceiver.setAutoRefresh(appContext, Integer.parseInt(refreshPreferenceList.getValue()));
                } else {
                    // Turn auto refresh off.
                    AlarmReceiver.cancelAutoRefresh(appContext);
                }

                return true;
            }
        });

    }

    public static void activateAutoRefresh(Activity parent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent);
        boolean autoRefreshEnabled = sharedPreferences.getBoolean(AUTO_REFRESH_KEY, false);
        if(autoRefreshEnabled) {
            String refreshIntervalStr = sharedPreferences.getString(REFRESH_INTERVAL_KEY, null);
            AlarmReceiver.cancelAutoRefresh(parent);
            AlarmReceiver.setAutoRefresh(parent, Integer.parseInt(refreshIntervalStr));
        }
    }

    public static boolean getCostBasisSetting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SettingsActivity.SHOW_COST_BASIS, false);
    }

    public static void setCostBasisSetting(Context context, boolean showCostBasis) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SettingsActivity.SHOW_COST_BASIS, showCostBasis);
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
