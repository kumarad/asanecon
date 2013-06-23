package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class TargetDialog {
    public static void create(final Activity parentActivity, final String symbol) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Set target price");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.set_target_price, null);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
        final EditText targetText = (EditText)view.findViewById(R.id.set_target_price);
        if(profile.targetPrice != null) {
            targetText.setText(Double.toString(profile.targetPrice));
        }

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAdd(profile, targetText);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.create().show();

    }

    private static void doAdd(SymbolProfile profile, EditText targetText) {
        String targetStr = targetText.getText().toString();
        if(!targetStr.equals("")) {
            profile.targetPrice = Double.parseDouble(targetStr);
        }

        if(profile.targetPrice != null) {
            ProfileManager.addSymbolData(profile);
        }
    }
}
