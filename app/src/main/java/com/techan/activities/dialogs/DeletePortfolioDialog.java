package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.profile.Portfolio;
import com.techan.profile.PortfolioManager;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfileManager;

import java.util.Map;
import java.util.Set;

public class DeletePortfolioDialog {
    public static void create(final Activity parentActivity, final String portfolioName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Delete Portfolio");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.delete_portfolio, null);

        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDelete(parentActivity, dialogView, portfolioName);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.create().show();
    }

    private static void doDelete(Activity parentActivity, View dialogView, String portfolioName) {
        Map<String,Portfolio> portfolios = ProfileManager.getPortfolios(parentActivity);
        Portfolio portfolioToDelete = portfolios.get(portfolioName);
        if(portfolioToDelete != null) {
            ProfileManager.removePortfolio(parentActivity, portfolioName);

            CheckBox checkBox = (CheckBox) dialogView.findViewById(R.id.deletePortfolioStockCheckbox);
            if (checkBox.isChecked()) {
                for(String symbolToDelete : portfolioToDelete.getSymbols()) {
                    for (Map.Entry<String, Portfolio> curEntry : portfolios.entrySet()) {
                        if (curEntry.getValue().getSymbols().contains(symbolToDelete)) {
                            //todo this is not performant - should fix
                            ProfileManager.removeSymbolFromPortfolio(parentActivity, curEntry.getKey(), symbolToDelete);
                        }
                    }
                }

                for(String stockSymbol : portfolioToDelete.getSymbols()) {
                    parentActivity.getContentResolver().delete(StockContentProvider.CONTENT_URI,
                            "sym like ?",
                            new String[]{stockSymbol});
                    ProfileManager.removeSymbol(parentActivity, stockSymbol);
                }
            }

            Intent intent = new Intent(parentActivity, HomeActivity.class);
            parentActivity.startActivity(intent);
        }
    }
}
