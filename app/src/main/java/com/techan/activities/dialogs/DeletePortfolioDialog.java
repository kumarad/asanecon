package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.OkCancelDialog;
import com.techan.custom.DialogAction;
import com.techan.profile.Portfolio;
import com.techan.profile.ProfileManager;

import java.util.Map;

public class DeletePortfolioDialog {
    public static void create(final Activity parentActivity, final String portfolioName) {
        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_portfolio, null);

        DeletePorfolioAction action = new DeletePorfolioAction(parentActivity, dialogView, portfolioName);
        OkCancelDialog dialog = new OkCancelDialog(parentActivity, dialogView, "Delete Portfolio", action);
        dialog.setOk("Delete");
        dialog.setCancel("Cancel");
        dialog.show();
    }

    private static class DeletePorfolioAction implements DialogAction {
        private final Activity parentActivity;
        private final View dialogView;
        private final String portfolioName;

        public DeletePorfolioAction(Activity parentActivity, View dialogView, String portfolioName) {
            this.parentActivity = parentActivity;
            this.dialogView = dialogView;
            this.portfolioName = portfolioName;
        }

        @Override
        public void ok(Dialog dialog) {
            Map<String, Portfolio> portfolios = ProfileManager.getPortfolios(parentActivity);
            Portfolio portfolioToDelete = portfolios.get(portfolioName);
            if (portfolioToDelete != null) {
                CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.deletePortfolioStockCheckbox);
                if (checkBox.isChecked()) {
                    for (String stockSymbol : portfolioToDelete.getSymbols()) {
                        parentActivity.getContentResolver().delete(StockContentProvider.CONTENT_URI,
                                "sym like ?",
                                new String[]{stockSymbol});
                        ProfileManager.removeSymbol(parentActivity, stockSymbol);
                    }

                    // Have to do this after we have gone through the sql delete flow because
                    // this will delete the symbols from the portfolio.
                    ProfileManager.deletePortfolio(parentActivity, portfolioName, true);
                } else {
                    ProfileManager.removePortfolio(parentActivity, portfolioName);
                }

                Intent intent = new Intent(parentActivity, HomeActivity.class);
                parentActivity.startActivity(intent);
            }
        }

        @Override
        public void cancel(Dialog dialog) {
            dialog.cancel();
        }
    }
}
