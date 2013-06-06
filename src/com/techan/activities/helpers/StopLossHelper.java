package com.techan.activities.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.techan.R;

public class StopLossHelper {
    public static void createDialog(Activity parentActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Set stop loss");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_stop_loss, null);
        NumberPicker np = ((NumberPicker)view.findViewById(R.id.stop_loss_np));
        np.setMaxValue(100);
        np.setMinValue(0);

        //todo
        np.setValue(25);

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
