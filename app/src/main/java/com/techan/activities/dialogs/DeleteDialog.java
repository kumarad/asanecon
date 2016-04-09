package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.custom.DialogAction;
import com.techan.custom.OkCancelDialog;
import com.techan.custom.Util;
import com.techan.profile.Portfolio;
import com.techan.profile.ProfileManager;

import java.util.Map;

public class DeleteDialog {
    public static void create(final Activity parentActivity, final Uri stockUri, final String symbol, final String portfolioName) {
        boolean deleteFromEverywhere = false;
        String title;
        if(portfolioName.equals(HomeActivity.ALL_STOCKS)) {
            title = "Delete stock";
            deleteFromEverywhere = true;
        } else {
            title = String.format("Delete stock from %s", portfolioName);
        }

        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.generic_ok_cancel_dialog, null);
        dialogView.findViewById(R.id.genericDialogText).setVisibility(View.GONE);
        DeleteStockAction action = new DeleteStockAction(parentActivity, stockUri, symbol, portfolioName, deleteFromEverywhere);
        OkCancelDialog dialog = new OkCancelDialog(parentActivity, dialogView, title, action);
        dialog.setOk("Confirm");
        dialog.setCancel("Cancel");
        dialog.show();

    }

    private static class DeleteStockAction implements DialogAction {

        private final Activity parentActivity;
        private final Uri stockUri;
        private final String symbol;
        private final String portfolioName;
        private final boolean deleteAll;

        public DeleteStockAction(Activity parentActivity, Uri stockUri, String symbol, String portfolioName, boolean deleteAll) {
            this.parentActivity = parentActivity;
            this.stockUri = stockUri;
            this.symbol = symbol;
            this.portfolioName = portfolioName;
            this.deleteAll = deleteAll;
        }

        @Override
        public void ok(Dialog dialog) {
            if (deleteAll) {
                parentActivity.getContentResolver().delete(stockUri, null, null);
                if (!ProfileManager.removeSymbol(parentActivity.getApplicationContext(), symbol)) {
                    Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile data from being updated.");
                }

                for (Map.Entry<String, Portfolio> portfolioEntry : ProfileManager.getPortfolios(parentActivity).entrySet()) {
                    if (portfolioEntry.getValue().getSymbols().contains(symbol)) {
                        ProfileManager.removeSymbolFromPortfolio(parentActivity, portfolioEntry.getKey(), symbol);
                    }
                }
            } else {
                ProfileManager.removeSymbolFromPortfolio(parentActivity, portfolioName, symbol);

                boolean symbolExistsInOtherPortfolio = false;
                for (Map.Entry<String, Portfolio> portfolioEntry : ProfileManager.getPortfolios(parentActivity).entrySet()) {
                    if (portfolioEntry.getValue().getSymbols().contains(symbol)) {
                        symbolExistsInOtherPortfolio = true;
                        break;
                    }
                }

                if (!symbolExistsInOtherPortfolio) {
                    parentActivity.getContentResolver().delete(stockUri, null, null);
                    if (!ProfileManager.removeSymbol(parentActivity.getApplicationContext(), symbol)) {
                        Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile data from being updated.");
                    }
                }
            }

            dialog.cancel();
            parentActivity.finish();
        }

        @Override
        public void cancel(Dialog dialog) {
            dialog.cancel();
        }
    }
}
