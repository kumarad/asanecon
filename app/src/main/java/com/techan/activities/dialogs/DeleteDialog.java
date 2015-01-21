package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.techan.activities.HomeActivity;
import com.techan.custom.Util;
import com.techan.profile.Portfolio;
import com.techan.profile.ProfileManager;

import java.util.Map;

public class DeleteDialog {
    public static void create(final Activity parentActivity, final Uri stockUri, final String symbol) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Deleting stock from profile");
        alertDialog.setMessage("Click yes to confirm");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteStock(parentActivity, stockUri, symbol);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.create().show();
    }

    private static final int ACTIVITY_CREATE = 0;

    private static void deleteStock(Activity parentActivity, Uri stockUri, String symbol) {
        parentActivity.getContentResolver().delete(stockUri, null, null);

        if(!ProfileManager.removeSymbol(parentActivity.getApplicationContext(), symbol)) {
            Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile data from being updated.");
        }

        for(Map.Entry<String, Portfolio> entry : ProfileManager.getPortfolios(parentActivity).entrySet()) {
            if(entry.getValue().getSymbols().contains(symbol)) {
                ProfileManager.removeSymbolFromPortfolio(parentActivity, entry.getKey(), symbol);
            }
        }

        Intent i = new Intent(parentActivity, HomeActivity.class);
        parentActivity.startActivityForResult(i, ACTIVITY_CREATE);
    }

}
