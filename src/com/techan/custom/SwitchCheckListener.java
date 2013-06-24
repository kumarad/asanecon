package com.techan.custom;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;

import com.techan.activities.SettingsActivity;

public class SwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
    protected View view;

    protected boolean globalNotifications;
    protected SharedPreferences.Editor settingsEditor;

    protected View warningView;
    protected boolean childNotifications;

    public SwitchCheckListener(View view, boolean globalNotifications, SharedPreferences.Editor settingsEditor) {
        this.view = view;
        this.globalNotifications = globalNotifications;
        this.settingsEditor = settingsEditor;
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
    }

    public void commit() {
        settingsEditor.commit();
    }
}
