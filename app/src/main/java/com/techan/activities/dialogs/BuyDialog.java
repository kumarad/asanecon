package com.techan.activities.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.StockPagerAdapter;
import com.techan.custom.DialogAction;
import com.techan.custom.OkCancelDialog;
import com.techan.custom.Util;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class BuyDialog {

    public static void create(final Context context, final LayoutInflater inflater, final String symbol, final StockPagerAdapter stockPagerAdapter) {
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

        BuyPriceAction action = new BuyPriceAction(context, profile, buyPriceText, shareCountText, stockPagerAdapter);
        OkCancelDialog dialog = new OkCancelDialog(context, view, "Cost Basis", action);
        dialog.setOk("Save");
        dialog.setCancel("Clear");
        dialog.show();
    }

    private static class BuyPriceAction implements DialogAction {

        private final Context context;
        private final SymbolProfile profile;
        private final EditText buyPriceText;
        private final EditText shareCountText;
        private final StockPagerAdapter stockPagerAdapter;

        public BuyPriceAction(Context context, SymbolProfile profile, EditText buyPriceText, EditText shareCountText, StockPagerAdapter stockPagerAdapter) {
            this.context = context;
            this.profile = profile;
            this.buyPriceText = buyPriceText;
            this.shareCountText = shareCountText;
            this.stockPagerAdapter = stockPagerAdapter;
        }

        @Override
        public void ok(Dialog dialog) {
            boolean showStopLossToast = false;
            String buyPriceStr = buyPriceText.getText().toString();
            String shareCountStr = shareCountText.getText().toString();
            if (!buyPriceStr.equals("")) {
                // Buy price is being set.
                double newBuyPrice = Double.parseDouble(buyPriceStr);
                if (profile.buyPrice != null) {
                    if (newBuyPrice != profile.buyPrice) {
                        // Buy price is being updated.
                        if (profile.stopLossPercent != null) {
                            showStopLossToast = true;
                        }

                        // If buy price is being updated clear stop loss info.
                        profile.clearStopLossInfo();
                    }
                } // else buy price is being set for the first time. Means stop loss info doesn't exist yet.
                profile.buyPrice = newBuyPrice;

                if (!shareCountStr.equals("")) {
                    profile.stockCount = Integer.parseInt(shareCountStr);
                } else {
                    profile.stockCount = null;
                }
            } else {
                if (profile.stopLossPercent != null) {
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

            if (showStopLossToast) {
                Util.showErrorToast(context, "Stop loss information has been reset. Please update.");
            }

            dialog.dismiss();
        }

        @Override
        public void cancel(Dialog dialog) {
            boolean showStopLossToast = false;
            if (profile.stopLossPercent != null) {
                showStopLossToast = true;
            }

            profile.buyPrice = null;
            profile.stockCount = null;
            profile.clearStopLossInfo();

            // Update profile info.
            ProfileManager.addSymbolData(profile);

            // Update cost basis view.
            stockPagerAdapter.updateCostBasisFragment(profile);

            if (showStopLossToast)
                Util.showErrorToast(context, "Stop loss information has been reset. Please update.");

            dialog.dismiss();
        }
    }
}
