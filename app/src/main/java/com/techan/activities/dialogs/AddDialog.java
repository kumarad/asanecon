package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.RefreshTask;

import java.util.Collection;

public class AddDialog {
    public static void create(final Activity parentActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Add stock");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.stock_add, null);

        //todo inspect the buy price and share count.

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAdd(parentActivity, view);
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

    private static void doAdd(Activity parentActivity, View view) {
        EditText addText = (EditText)view.findViewById(R.id.stock_add);
        String symbol = addText.getText().toString().toUpperCase();

        if(verify(symbol, parentActivity)) {
            addInternal(symbol, parentActivity);
        }
    }

    private static boolean verify(String symbol, Activity parentActivity) {
        if(symbol.length() == 0) {
            Util.showErrorToast(parentActivity, "Nothing input.");
            return false;
        }

        if(!symbol.matches("[\\.a-zA-Z0-9]+")) {
            Util.showErrorToast(parentActivity, "Stock symbol can only contain letters, numbers and periods.");
            return false;
        }

        // Check to see if the symbol is already in the database. Can't have duplicates.
        String[] projection = {StocksTable.COLUMN_ID};
        Cursor cursor = parentActivity.getContentResolver().query(StockContentProvider.CONTENT_URI, projection, StocksTable.COLUMN_SYMBOL + "='" + symbol + "'", null, null);
        if(cursor.getCount() != 0) {
            // Already in cursor.
            Util.showErrorToast(parentActivity, "Stock symbol already added.");
            return false;
        }

        return true;
    }

    private static void addInternal(String symbol, Activity parentActivity) {
        // RefreshTask expects symbol profile to exist! So make sure to add to profile manager first.
        if(!ProfileManager.addSymbol(parentActivity.getApplicationContext(), symbol)) {
            // Failure adding symbol to persistent file. Let user know.
            Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile from being updated.");
        }

        //testJSONManager(symbol, parentActivity);

        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SYMBOL, symbol);
        Uri addedUri = parentActivity.getContentResolver().insert(StockContentProvider.CONTENT_URI, values);
        Uri uri = Uri.parse(StockContentProvider.BASE_URI_STR + addedUri);
        (new RefreshTask(parentActivity, parentActivity.getContentResolver(), uri, symbol, true)).execute();
    }

    private static void testJSONManager(String symbol, Activity parentActivity) {
        SymbolProfile symProfile = ProfileManager.getSymbolData(parentActivity.getApplication(), symbol);
        if(symbol.equals("IBM")) {
            symProfile.stopLossPercent = 25;
        } else if(symbol.equals("MSFT")) {
            symProfile.stopLossPercent = 10;
            symProfile.buyPrice = 400.12;
        }

        ProfileManager.addSymbolData(symProfile);

        Collection<SymbolProfile> profiles = ProfileManager.getSymbols(parentActivity.getApplicationContext());
    }
}
