package com.techan.activities.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.techan.R;

public class PeHelper {
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

    public static void createDialog(Activity parentActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Set target PE value for stock");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_pe_target, null);

        // Handle edit text view.
        EditText editText = ((EditText)view.findViewById(R.id.edit_pe_target));
        //todo
        editText.setText(Double.toString(4.5));
        editText.setSelection(editText.length());

        // Handle switch.
        Switch s = (Switch) view.findViewById(R.id.switch_pe_notify);
        s.setChecked(true);
        SwitchCheckListener listener = new SwitchCheckListener(editText);
        s.setOnCheckedChangeListener(listener);


        //Pass null as parent view because its a dialog.
        alertDialog.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO
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
}
