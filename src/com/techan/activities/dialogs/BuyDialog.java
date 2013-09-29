package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.StockPagerAdapter;
import com.techan.custom.Util;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

import java.util.Calendar;

public class BuyDialog {

    public static void create(final Activity parentActivity, final String symbol, final Uri stockUri, final StockPagerAdapter stockPagerAdapter) {
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

        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.buyDatePicker);
        datePicker.setCalendarViewShown(false);
        setBuyDateOnView(datePicker, profile.buyDate);

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAdd(parentActivity, profile, stockUri, symbol, buyPriceText, shareCountText, datePicker, stockPagerAdapter);
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

    private static void setBuyDateOnView(DatePicker dp, String buyDate) {
        Calendar c;
        if(buyDate == null) {
            c = Calendar.getInstance();
        } else {
            c = Util.getCal(buyDate);
        }

        // set current date into datepicker
        dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
    }

    private static void doAdd(Activity activity, SymbolProfile profile, Uri stockUri, String symbol, EditText buyPriceText, EditText shareCountText, DatePicker datePicker, StockPagerAdapter stockPagerAdapter) {
        boolean showStopLossToast = false;
        String buyPriceStr = buyPriceText.getText().toString();
        String shareCountStr = shareCountText.getText().toString();
        String buyDateStr = Util.getCalStr(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
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

            if(profile.buyDate != null) {
                if(!buyDateStr.equals(profile.buyDate)) {
                    // Buy date is being updated.
                    if(profile.stopLossPercent != null) {
                        showStopLossToast = true;
                    }

                    // If buy date is being updated clear stop loss info.
                    profile.clearStopLossInfo();
                } // else buyDate is null and being set for first time with buyPrice or its the same as before.
            } // else buy date being set for first time. Means buy price was just set too. Means stop loss info doesn't exist yet.
            profile.buyDate = buyDateStr;

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
            profile.buyDate = null;
            profile.stockCount = null;
            profile.clearStopLossInfo();
        }

        // Update profile info.
        ProfileManager.addSymbolData(profile);

        // Update cost basis view.
        stockPagerAdapter.updateCostBasisFragment(profile.buyPrice, profile.buyDate, profile.stockCount, profile.stopLossPercent);

        if(showStopLossToast) {
            Util.showErrorToast(activity, "Stop loss information has been reset. Please update.");
        }
    }
}
