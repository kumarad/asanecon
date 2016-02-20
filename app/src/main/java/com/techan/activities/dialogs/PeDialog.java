package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.StockPagerAdapter;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class PeDialog {
    public static void create(Activity parentActivity, String symbol, final StockPagerAdapter stockPagerAdapter) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Target PE value for stock");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_pe_target, null);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);

        // Handle edit text view.
        final EditText editText = ((EditText)view.findViewById(R.id.edit_pe_target));
        if(profile.peTarget != null) {
            editText.setText(Double.toString(profile.peTarget));
        }

        // Set cursor to end of text.
        editText.setSelection(editText.length());

        // Create dialog.
        //Pass null as parent view because its a dialog.
        alertDialog.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doAdd(profile, editText, stockPagerAdapter);
                    }
                })
                .setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doClear(profile, stockPagerAdapter);
                    }
                });

        alertDialog.create().show();
    }

    private static void doAdd(SymbolProfile profile, EditText editText, StockPagerAdapter stockPagerAdapter) {
        String valText = editText.getText().toString();
        if(valText != null && !valText.isEmpty()) {
            Double val = Double.parseDouble(valText);
            if (val != null && val > 0) {
                profile.peTarget = val;
            } else {
                profile.peTarget = null;
            }
        } else {
            profile.peTarget = null;
        }

        ProfileManager.addSymbolData(profile);
        stockPagerAdapter.updateCostBasisFragment(profile);
    }

    private static void doClear(SymbolProfile profile, StockPagerAdapter stockPagerAdapter) {
        profile.peTarget = null;
        ProfileManager.addSymbolData(profile);
        stockPagerAdapter.updateCostBasisFragment(profile);
    }

}
