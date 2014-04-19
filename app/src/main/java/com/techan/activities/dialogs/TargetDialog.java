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

public class TargetDialog {

    static private class ComparatorListener implements DialogInterface.OnClickListener {
        public boolean lessThanEqual = true;
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            if(which == 0) {
                lessThanEqual = true;
            } else {
                lessThanEqual = false;
            }
        }
    }

    public static void create(final Activity parentActivity, final String symbol, final StockPagerAdapter stockPagerAdapter) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Set target price");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.set_target_price, null);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
        final EditText targetText = (EditText)view.findViewById(R.id.set_target_price);
        int comparatorChoice = 0;
        if(profile.targetPrice != null) {
            targetText.setText(Double.toString(profile.targetPrice));
            if(!profile.lessThanEqual)
                comparatorChoice = 1;
        }

        final ComparatorListener compListener = new ComparatorListener();

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAdd(profile, targetText, compListener, stockPagerAdapter);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setSingleChoiceItems(R.array.comparator, comparatorChoice, compListener);
        alertDialog.create().show();

    }

    private static void doAdd(SymbolProfile profile, EditText targetText, ComparatorListener compListener, StockPagerAdapter stockPagerAdapter) {
        String targetStr = targetText.getText().toString();
        if(!targetStr.equals("")) {
            profile.targetPrice = Double.parseDouble(targetStr);
            profile.lessThanEqual = compListener.lessThanEqual;
        } else {
            profile.targetPrice = null;
            profile.lessThanEqual = null;
        }

        // Update cost basis view.
        stockPagerAdapter.updateCostBasisFragment(profile);

        ProfileManager.addSymbolData(profile);
    }
}
