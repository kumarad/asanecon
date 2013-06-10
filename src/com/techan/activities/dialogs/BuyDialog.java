package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class BuyDialog {

    public static void create(final Activity parentActivity, final String symbol) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Add purchase info");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.set_buy_price, null);

        //todo inspect the buy price and share count.
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
                doAdd(profile, buyPriceText, shareCountText);
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

    private static void doAdd(SymbolProfile profile, EditText buyPriceText, EditText shareCountText) {
        String buyPriceStr = buyPriceText.getText().toString();
        if(!buyPriceStr.equals("")) {
            profile.buyPrice = Double.parseDouble(buyPriceStr);
        }

        String shareCountStr = shareCountText.getText().toString();
        if(!shareCountStr.equals("")) {
            profile.stockCount = Integer.parseInt(shareCountStr);
        }

        if(profile.buyPrice != null || profile.stockCount != null) {
            ProfileManager.addSymbolData(profile);
        }
    }
}
