package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import com.techan.R;

public class StopLossDialog {
    public static class SwitchCheckListener implements CompoundButton.OnCheckedChangeListener {
        private NumberPicker np;
        private EditText editText;
        public SwitchCheckListener(NumberPicker np, EditText editText) {
            this.np = np;
            this.editText = editText;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            if(isChecked) {
                // The toggle is enabled.
                editText.setEnabled(true);
                np.setEnabled(true);
            } else {
                // The toggle is disabled.
                editText.setEnabled(false);
                np.setEnabled(false);
            }
        }
    }

    public static void create(Activity parentActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Trailing stop loss");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_stop_loss, null);


        NumberPicker np = ((NumberPicker)view.findViewById(R.id.stop_loss_np));
        np.setMaxValue(100);
        np.setMinValue(0);
        //todo
        np.setValue(25);

        EditText editText = (EditText)view.findViewById(R.id.edit_buy_price);


        Switch s = (Switch) view.findViewById(R.id.switch_sl_notify);
        s.setChecked(true);
        SwitchCheckListener listener = new SwitchCheckListener(np, editText);
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
