package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
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

    public static void create(final Activity parentActivity, final String symbol, final StockPagerAdapter stockPagerAdapter) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Add purchase info");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.set_buy_price, null);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
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
                doAdd(parentActivity, profile, buyPriceText, shareCountText, stockPagerAdapter);
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

    private static void doAdd(Activity activity, SymbolProfile profile, EditText buyPriceText, EditText shareCountText, StockPagerAdapter stockPagerAdapter) {
        boolean showStopLossToast = false;
        String buyPriceStr = buyPriceText.getText().toString();
        String shareCountStr = shareCountText.getText().toString();
        if(!buyPriceStr.equals("")) {
            double newBuyPrice = Double.parseDouble(buyPriceStr);
            if(profile.buyPrice != null && newBuyPrice != profile.buyPrice) {
                if(profile.stopLossPercent != null) {
                    showStopLossToast = true;
                }

                // If buy price is being updated clear stop loss info.
                profile.clearStopLossInfo();
            }

            profile.buyPrice = newBuyPrice;

            if(!shareCountStr.equals("")) {
                profile.stockCount = Integer.parseInt(shareCountStr);
            } else {
                profile.stockCount = null;
            }
        } else {
            if(!shareCountStr.equals("")) {
                Util.showErrorToast(activity, "Share price ignored without buy price.");
            }

            profile.buyPrice = null;
            profile.stockCount = null;
            profile.clearStopLossInfo();
        }

        ProfileManager.addSymbolData(profile);
        stockPagerAdapter.updateCostBasisFragment(profile.buyPrice, profile.stockCount);

        if(showStopLossToast) {
            Util.showErrorToast(activity, "Stop loss information has been reset. Please update.");
        }
    }
}
