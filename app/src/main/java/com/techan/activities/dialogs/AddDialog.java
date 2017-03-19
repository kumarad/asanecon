package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.activities.fragments.StockListFragment;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.DialogAction;
import com.techan.custom.OkCancelDialog;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.stockDownload.RefreshTask;

public class AddDialog {
    public static void create(final StockListFragment stockListFragment, final String portfolioName, final LoaderManager loaderManager) {
        // Get layout inflater
        LayoutInflater inflater = stockListFragment.getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.stock_add, null);

        String title;
        if(portfolioName.equals(HomeActivity.ALL_STOCKS)) {
            title = "Add stock";
        } else {
            title = "Add stock to " + portfolioName;
        }

        AddStockAction action = new AddStockAction(stockListFragment, dialogView, portfolioName, loaderManager);
        OkCancelDialog dialog = new OkCancelDialog(stockListFragment.getActivity(), dialogView, title, action);
        dialog.setOk("Add");
        dialog.setCancel("Cancel");
        dialog.show();
    }

    private static class AddStockAction implements DialogAction {

        private final StockListFragment stockListFragment;
        private final View view;
        private final String portfolioName;
        private final LoaderManager loaderManager;

        public AddStockAction(StockListFragment stockListFragment, View view, String porfolioName, LoaderManager loaderManager ) {
            this.stockListFragment = stockListFragment;
            this.view = view;
            this.portfolioName = porfolioName;
            this.loaderManager = loaderManager;
        }

        @Override
        public void ok(Dialog dialog) {
            EditText addText = (EditText)view.findViewById(R.id.stock_add);
            String symbol = addText.getText().toString().toUpperCase();

            if(verify(symbol)) {
                addInternal(symbol);
            }

            dialog.dismiss();
        }

        @Override
        public void cancel(Dialog dialog) {
            dialog.cancel();
        }

        private boolean verify(String symbol) {
            if(symbol.length() == 0) {
                Util.showErrorToast(stockListFragment.getActivity(), "Nothing input.");
                return false;
            }

            if(!symbol.matches("[\\.a-zA-Z0-9=-]+")) {
                Util.showErrorToast(stockListFragment.getActivity(), "Stock symbol can only contain letters, numbers and periods.");
                return false;
            }

            return true;
        }

        private void addInternal(String symbol) {
            Activity parentActivity = stockListFragment.getActivity();

            // Check to see if the symbol is already in the database. Can't have duplicates.
            boolean alreadyInDb = false;
            String[] projection = {StocksTable.COLUMN_ID};
            Cursor cursor = parentActivity.getContentResolver().query(StockContentProvider.CONTENT_URI, projection, StocksTable.COLUMN_SYMBOL + "='" + symbol + "'", null, null);
            if (cursor == null) {
                return;
            }

            if(cursor.getCount() != 0) {
                alreadyInDb = true;
            }

            cursor.close();

            if(!alreadyInDb) {
                // If the stock was in the profile then it would have been loaded into the db on start up.
                // So the fact that this symbol is not in the db means we need to add it to the profile here.

                // RefreshTask expects symbol profile to exist! So make sure to add to profile manager first.
                if (!ProfileManager.addSymbol(stockListFragment.getActivity(), symbol)) {
                    // Failure adding symbol to persistent file. Let user know.
                    Util.showErrorToast(stockListFragment.getActivity(), "Oops. Something on your device prevented profile from being updated.");
                }
            }

            if(!portfolioName.equals(HomeActivity.ALL_STOCKS)) {
                if(ProfileManager.getPortfolios(parentActivity).get(portfolioName).getSymbols().contains(symbol)) {
                    Util.showErrorToast(parentActivity, "Stock symbol already added.");
                } else {
                    if(!ProfileManager.addSymbolToPortfolio(stockListFragment.getActivity(), portfolioName, symbol)) {
                        Util.showErrorToast(stockListFragment.getActivity(), "Oops. Something on your device prevented profile from being updated.");
                    }
                }
            } else {
                if(alreadyInDb) {
                    Util.showErrorToast(parentActivity, "Stock symbol already added.");
                }
            }

            if(!alreadyInDb) {
                // Stock needs to be added to the database.
                ContentValues values = new ContentValues();
                values.put(StocksTable.COLUMN_SYMBOL, symbol);
                Uri addedUri = stockListFragment.getActivity().getContentResolver().insert(StockContentProvider.CONTENT_URI, values);
                Uri uri = Uri.parse(StockContentProvider.BASE_URI_STR + addedUri);
                (new RefreshTask(stockListFragment.getActivity().getContentResolver(), uri, symbol)).download(stockListFragment.getActivity());
            }

            loaderManager.restartLoader(StockListFragment.LOADER_ID, null, stockListFragment);
        }
    }

}
