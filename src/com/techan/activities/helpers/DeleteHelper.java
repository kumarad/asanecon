package com.techan.activities.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.techan.activities.StockHomeActivity;
import com.techan.custom.Util;
import com.techan.profile.JSONManager;

public class DeleteHelper {
    public static void createDialog(final Activity parentActivity, final Uri stockUri, final String symbol) {
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

        if(!JSONManager.removeSymbol(parentActivity.getApplicationContext(), symbol)) {
            Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile data from being updated.");
        }

        Intent i = new Intent(parentActivity, StockHomeActivity.class);
        parentActivity.startActivityForResult(i, ACTIVITY_CREATE);
    }

}
