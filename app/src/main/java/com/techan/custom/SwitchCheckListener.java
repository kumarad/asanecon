package com.techan.custom;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;

import com.techan.activities.SettingsActivity;

public class SwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
    protected View checkDependantView;

    // Indicates whether global notifications are enabled/disabled.
    protected boolean globalNotifications;
    // Provides state of global child notification.  PE notification is child of global notification.
    protected boolean childNotifications;

    // Provides ability to change global preferences.
    protected SharedPreferences.Editor settingsEditor;

    // Warning view if one exists.
    protected View warningView;

    public SwitchCheckListener(View checkDependantView,
                               boolean globalNotifications,
                               SharedPreferences.Editor settingsEditor,
                               View warningView,
                               boolean childNotifications) {
        this.checkDependantView = checkDependantView;
        this.globalNotifications = globalNotifications;
        this.settingsEditor = settingsEditor;

        this.warningView = warningView;
        this.childNotifications = childNotifications;
    }

    protected void handleWarning(boolean checked) {
        // No warning to handle.
        if(warningView == null) return;

        // Shows a warning that a global child notification is going to take effect
        // if switch is being unchecked.
        if(globalNotifications && childNotifications) {
            if(!checked) {
                // Global version of this setting is enabled. Warn user that the global settings will
                // be used.
                warningView.setVisibility(View.VISIBLE);
            } else {
                // Global version of this setting will be override by this local settings.
                warningView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked) {
            // The toggle is enabled.
            checkDependantView.setEnabled(true);
            handleWarning(true);
        } else {
            // The toggle is disabled.
            checkDependantView.setEnabled(false);
            handleWarning(false);
        }
    }

    public void commit() {
        settingsEditor.commit();
    }
}
