package com.techan.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.StockPagerAdapter;
import com.techan.custom.Util;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class BuyDialog {

    public static void create(final Context context, final LayoutInflater inflater, final String symbol, final StockPagerAdapter stockPagerAdapter) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Add purchase info");

        // Get layout inflater
        final View view = inflater.inflate(R.layout.set_buy_price, null);

        final SymbolProfile profile = ProfileManager.getSymbolData(context, symbol);
        final EditText buyPriceText = (EditText)view.findViewById(R.id.set_buy_price);
        if(profile.buyPrice != null) {
            buyPriceText.setText(Double.toString(profile.buyPrice));
        }

        final EditText shareCountText = (EditText)view.findViewById(R.id.set_share_count);
        if(profile.stockCount != null) {
            shareCountText.setText(Integer.toString(profile.stockCount));
        } else {
            shareCountText.setHint("optional");
        }


        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAdd(context, profile, buyPriceText, shareCountText, stockPagerAdapter);
            }
        });
        alertDialog.setNegativeButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doClear(context, profile, stockPagerAdapter);
            }
        });

        alertDialog.create().show();

    }

    private static void doAdd(Context context, SymbolProfile profile, EditText buyPriceText, EditText shareCountText, StockPagerAdapter stockPagerAdapter) {
        boolean showStopLossToast = false;
        String buyPriceStr = buyPriceText.getText().toString();
        String shareCountStr = shareCountText.getText().toString();
        if(!buyPriceStr.equals("")) {
            // Buy price is being set.
            double newBuyPrice = Double.parseDouble(buyPriceStr);
            if(profile.buyPrice != null) {
                if(newBuyPrice != profile.buyPrice) {
                    // Buy price is being updated.
                    if(profile.stopLossPercent != null) {
                        showStopLossToast = true;
                    }

                    // If buy price is being updated clear stop loss info.
                    profile.clearStopLossInfo();
                }
            } // else buy price is being set for the first time. Means stop loss info doesn't exist yet.
            profile.buyPrice = newBuyPrice;

            if(!shareCountStr.equals("")) {
                profile.stockCount = Integer.parseInt(shareCountStr);
            } else {
                profile.stockCount = null;
            }
        } else {
            if(profile.stopLossPercent != null) {
                showStopLossToast = true;
            }

            profile.buyPrice = null;
            profile.stockCount = null;
            profile.clearStopLossInfo();
        }

        // Update profile info.
        ProfileManager.addSymbolData(profile);

        // Update cost basis view.
        stockPagerAdapter.updateCostBasisFragment(profile);

        if(showStopLossToast) {
            Util.showErrorToast(context, "Stop loss information has been reset. Please update.");
        }
    }

    private static void doClear(Context context, SymbolProfile profile, StockPagerAdapter stockPagerAdapter) {
        boolean showStopLossToast = false;
        if(profile.stopLossPercent != null) {
            showStopLossToast = true;
        }

        profile.buyPrice = null;
        profile.stockCount = null;
        profile.clearStopLossInfo();

        // Update profile info.
        ProfileManager.addSymbolData(profile);

        // Update cost basis view.
        stockPagerAdapter.updateCostBasisFragment(profile);

        if(showStopLossToast)
            Util.showErrorToast(context, "Stop loss information has been reset. Please update.");
    }
}
