package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.techan.R;
import com.techan.activities.SettingsActivity;
import com.techan.custom.SwitchCheckListener;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class StopLossDialog {
    public static void createError(AlertDialog.Builder alertDialog) {
        // Need buyPrice to set stop loss.
        alertDialog.setTitle("Set buy price first.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Nada.
            }
        });

        alertDialog.create().show();
    }

    public static void create(Activity parentActivity, String symbol) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Trailing stop loss");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_stop_loss, null);

        // Get global notification preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(parentActivity);
        boolean globalNotifications = sharedPref.getBoolean(SettingsActivity.ALL_NOTIFICATIONS_KEY, true);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
        if(profile.buyPrice == null) {
            createError(alertDialog);
            return;
        }

        // Handle number picker.
        final NumberPicker np = ((NumberPicker)view.findViewById(R.id.stop_loss_np));
        np.setMaxValue(100);
        np.setMinValue(0);

        if(profile.stopLossPercent != null) {
            np.setValue(profile.stopLossPercent);
        } else {
            np.setValue(SettingsActivity.STOP_LOSS_DEFAULT);
        }

        // Handle price text.
        TextView buyPriceTextView = (TextView)view.findViewById(R.id.show_buy_price);
        buyPriceTextView.setText(Double.toString(profile.buyPrice));

        final Switch s = (Switch) view.findViewById(R.id.switch_sl_notify);
        if(globalNotifications && profile.stopLossPercent != null) {
            s.setChecked(true);
        } else {
            s.setChecked(false);
            np.setEnabled(false);
        }

        SwitchCheckListener listener = new SwitchCheckListener(np, globalNotifications, sharedPref.edit());
        s.setOnCheckedChangeListener(listener);

        //Pass null as parent view because its a dialog.
        alertDialog.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doAdd(profile, np, s);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Ignore
                    }
                });


        alertDialog.create().show();
    }

    private static void doAdd(SymbolProfile profile, NumberPicker np, Switch s) {
        if(s.isChecked()) {
            profile.stopLossPercent = np.getValue();
        } else {
            profile.stopLossPercent = null;
        }

        ProfileManager.addSymbolData(profile);
    }

}
