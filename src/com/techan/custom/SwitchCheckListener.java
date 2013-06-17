package com.techan.custom;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.CompoundButton;

import com.techan.activities.SettingsActivity;

public class SwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
    protected View view;

    protected boolean globalNotifications;
    protected SharedPreferences.Editor settingsEditor;

    public SwitchCheckListener(View view, boolean globalNotifications, SharedPreferences.Editor settingsEditor) {
        this.view = view;
        this.globalNotifications = globalNotifications;
        this.settingsEditor = settingsEditor;
    }

    protected void handleGlobalPreferences(boolean checked) {
        if(!globalNotifications) {
            settingsEditor.putBoolean(SettingsActivity.ALL_NOTIFICATIONS_KEY, checked);
            settingsEditor.commit();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if(isChecked) {
            // The toggle is enabled.
            view.setEnabled(true);
        } else {
            // The toggle is disabled.
            view.setEnabled(false);
        }

        handleGlobalPreferences(isChecked);
    }

}
