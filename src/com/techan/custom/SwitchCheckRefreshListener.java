package com.techan.custom;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;

import com.techan.R;
import com.techan.activities.SettingsActivity;

public class SwitchCheckRefreshListener extends SwitchCheckListener {
    protected Activity parentActivity;

    // Provides access to read global preferences.
    protected SharedPreferences sharedPreferences;

    protected boolean autoRefreshEnabled;
    protected boolean enableRefreshForStopLoss = false;
    protected boolean impactsRefresh = false;

    public SwitchCheckRefreshListener(View view,
                               boolean globalNotifications,
                               SharedPreferences.Editor settingsEditor,
                               View warningView,
                               Activity parentActivity,
                               SharedPreferences sharedPreferences,
                               boolean impactsRefresh) {
        super(view, globalNotifications, settingsEditor, warningView, false);

        this.parentActivity = parentActivity;
        this.sharedPreferences = sharedPreferences;

        this.autoRefreshEnabled = sharedPreferences.getBoolean(SettingsActivity.AUTO_REFRESH_KEY, false);
        this.impactsRefresh = impactsRefresh;
    }

    @Override
    protected void handleWarning(boolean checked) {
        if(warningView == null) return;

        if(!autoRefreshEnabled) {
            if(checked) {
                // Auto refresh is off. Warn user.
                warningView.setVisibility(View.VISIBLE);
            } else {
                // Auto refresh is already on. No warning needs to be shown.
                warningView.setVisibility(View.GONE);
            }
        }
    }

    protected void handleRefresh(boolean checked) {
        if(impactsRefresh) {
            if(checked) {
                if(!autoRefreshEnabled) {
                    // Auto refresh is disabled. Enable it.
                    settingsEditor.putBoolean(SettingsActivity.AUTO_REFRESH_KEY, true);

                    String[] intervals = checkDependantView.getResources().getStringArray(R.array.refreshIntervalValues);
                    String interval = intervals[intervals.length-1];
                    settingsEditor.putString(SettingsActivity.REFRESH_INTERVAL_KEY, interval);

                    enableRefreshForStopLoss = true;
                    return;
                }
            }

            enableRefreshForStopLoss = false;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        super.onCheckedChanged(compoundButton, isChecked);

        handleRefresh(isChecked);
    }

    @Override
    public void commit() {
        super.commit();

        if(impactsRefresh && enableRefreshForStopLoss)
            SettingsActivity.activateAutoRefresh(parentActivity);
    }
}