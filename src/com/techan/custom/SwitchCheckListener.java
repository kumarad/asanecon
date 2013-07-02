package com.techan.custom;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;

import com.techan.R;
import com.techan.activities.SettingsActivity;

public class SwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
    protected Activity parentActivity;
    protected View view;

    protected boolean globalNotifications;
    protected SharedPreferences.Editor settingsEditor;
    protected boolean mapToRefresh;
    protected SharedPreferences sharedPreferences;

    protected View warningView;
    protected boolean childNotifications;

    public SwitchCheckListener(Activity parentActivity, View view, boolean globalNotifications, SharedPreferences.Editor settingsEditor, boolean mapToRefresh, SharedPreferences sharedPreferences) {
        this.parentActivity = parentActivity;
        this.view = view;
        this.globalNotifications = globalNotifications;
        this.settingsEditor = settingsEditor;
        this.mapToRefresh = mapToRefresh;
        this.sharedPreferences = sharedPreferences;
    }

    public SwitchCheckListener(View view, boolean globalNotifications, SharedPreferences.Editor settingsEditor, View warningView, boolean childNotifications) {
        this.view = view;
        this.globalNotifications = globalNotifications;
        this.settingsEditor = settingsEditor;
        this.warningView = warningView;
        this.childNotifications = childNotifications;
    }

    protected void handleGlobalPreferences(boolean checked) {
        if(!globalNotifications) {
            settingsEditor.putBoolean(SettingsActivity.ALL_NOTIFICATIONS_KEY, checked);
        }
    }

    protected void handleWarning(boolean checked) {
        if(warningView == null) return;

        if(globalNotifications && childNotifications) {
            if(!checked)
                warningView.setVisibility(View.VISIBLE);
            else
                warningView.setVisibility(View.GONE);
        }
    }

    protected void handleRefresh(boolean checked) {
        if(mapToRefresh && checked) {
            if(!sharedPreferences.getBoolean(SettingsActivity.AUTO_REFRESH_KEY, true)) {
                settingsEditor.putBoolean(SettingsActivity.AUTO_REFRESH_KEY, true);

                String[] intervals = view.getResources().getStringArray(R.array.refreshIntervalValues);
                String interval = intervals[intervals.length-1];
                settingsEditor.putString(SettingsActivity.REFRESH_INTERVAL_KEY, interval);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked) {
            // The toggle is enabled.
            view.setEnabled(true);
            handleWarning(true);
        } else {
            // The toggle is disabled.
            view.setEnabled(false);
            handleWarning(false);
        }

        handleGlobalPreferences(isChecked);
        handleRefresh(isChecked);
    }

    public void commit() {
        settingsEditor.commit();
        if(mapToRefresh) {
            SettingsActivity.activateAutoRefresh(parentActivity);
        }
    }
}
