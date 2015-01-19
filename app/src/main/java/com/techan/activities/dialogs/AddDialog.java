package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.activities.fragments.StockListFragment;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.StockCursorAdapter;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.RefreshTask;

import java.util.Collection;

public class AddDialog {
    public static void create(final StockListFragment stockListFragment, final String portfolioName, final LoaderManager loaderManager) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(stockListFragment.getActivity());
        if(portfolioName.equals(HomeActivity.ALL_STOCKS)) {
            alertDialog.setTitle("Add stock");
        } else {
            alertDialog.setTitle("Add stock to " + portfolioName);
        }

        // Get layout inflater
        LayoutInflater inflater = stockListFragment.getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.stock_add, null);

        //todo inspect the buy price and share count.

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAdd(stockListFragment, view, portfolioName, loaderManager);
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

    private static void doAdd(StockListFragment stockListFragment, View view, String portfolioName, LoaderManager loaderManager) {
        EditText addText = (EditText)view.findViewById(R.id.stock_add);
        String symbol = addText.getText().toString().toUpperCase();

        if(verify(symbol, stockListFragment.getActivity())) {
            addInternal(symbol, stockListFragment, portfolioName, loaderManager);
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

    private static void addInternal(String symbol, StockListFragment stockListFragment, String portfolioName, LoaderManager loaderManager) {
        // RefreshTask expects symbol profile to exist! So make sure to add to profile manager first.
        if(!ProfileManager.addSymbol(stockListFragment.getActivity(), symbol)) {
            // Failure adding symbol to persistent file. Let user know.
            Util.showErrorToast(stockListFragment.getActivity(), "Oops. Something on your device prevented profile from being updated.");
        }

        if(!portfolioName.equals(HomeActivity.ALL_STOCKS)) {
            if (!ProfileManager.addSymbolToPortfolio(stockListFragment.getActivity(), portfolioName, symbol)) {
                Util.showErrorToast(stockListFragment.getActivity(), "Oops. Something on your device prevented profile from being updated.");
            }
        } // else not being added to a specific portfolio.

        //testJSONManager(symbol, parentActivity);

        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SYMBOL, symbol);
        Uri addedUri = stockListFragment.getActivity().getContentResolver().insert(StockContentProvider.CONTENT_URI, values);
        Uri uri = Uri.parse(StockContentProvider.BASE_URI_STR + addedUri);
        (new RefreshTask(stockListFragment.getActivity(), stockListFragment.getActivity().getContentResolver(), uri, symbol, true)).execute();
        loaderManager.restartLoader(StockListFragment.LOADER_ID, null,stockListFragment);
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
