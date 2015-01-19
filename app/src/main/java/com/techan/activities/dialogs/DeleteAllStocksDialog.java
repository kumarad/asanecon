package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.profile.ProfileManager;

public class DeleteAllStocksDialog {
    public static void create(final Activity parentActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Delete All Stocks");

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete(parentActivity);
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

    private static void doDelete(Activity parentActivity) {
        if(!ProfileManager.deleteProfile(parentActivity.getApplicationContext())) {
            Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile data from being reset.");
        }

        parentActivity.getContentResolver().delete(StockContentProvider.CONTENT_URI, null, null);
    }
}
