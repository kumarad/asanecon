package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.techan.R;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class PeDialog {
    public static class SwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
        private EditText editText;
        public SwitchCheckListener(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked) {
                // The toggle is enabled.
                editText.setEnabled(true);
            } else {
                // The toggle is disabled.
                editText.setEnabled(false);
            }
        }
    }

    public static void create(Activity parentActivity, String symbol) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Target PE value for stock");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_pe_target, null);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);

        // Handle switch.
        final Switch s = (Switch) view.findViewById(R.id.switch_pe_notify);

        // Handle edit text view.
        final EditText editText = ((EditText)view.findViewById(R.id.edit_pe_target));
        if(profile.peTarget != null) {
            editText.setText(Double.toString(profile.peTarget));
            s.setChecked(true);
        } else {
            SymbolProfile globalProfile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), ProfileManager.GLOBAL_ASANECON);
            editText.setText(Double.toString(globalProfile.peTarget));
            s.setChecked(false);
            editText.setEnabled(false);
        }
        editText.setSelection(editText.length());

        SwitchCheckListener listener = new SwitchCheckListener(editText);
        s.setOnCheckedChangeListener(listener);

        //Pass null as parent view because its a dialog.
        alertDialog.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doAdd(profile, editText, s);
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

    private static void doAdd(SymbolProfile profile, EditText editText, Switch s) {
        if(s.isChecked()) {
            profile.peTarget = Double.parseDouble(editText.getText().toString());
        } else {
            profile.peTarget = null;
        }

        ProfileManager.addSymbolData(profile);
    }

}
