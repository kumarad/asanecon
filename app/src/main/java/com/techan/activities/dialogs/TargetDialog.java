package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.StockPagerAdapter;
import com.techan.custom.DialogAction;
import com.techan.custom.OkCancelDialog;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class TargetDialog {

    public static void create(final Activity parentActivity, final String symbol, final StockPagerAdapter stockPagerAdapter) {
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.set_target_price, null);

        final Button lessThanButton = (Button)dialogView.findViewById(R.id.targetLessThan);
        final Button greaterThanButton = (Button)dialogView.findViewById(R.id.targetGreaterThan);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
        final EditText targetText = (EditText)dialogView.findViewById(R.id.set_target_price);
        if(profile.lessThanEqual != null) {
            if (profile.lessThanEqual) {
                lessThanButton.setPressed(true);
            } else {
                greaterThanButton.setPressed(true);
            }
        }

        if(profile.targetPrice != null) {
            targetText.setText(Double.toString(profile.targetPrice));
        }

        final TargetDialogAction action = new TargetDialogAction(profile, targetText, stockPagerAdapter);
        final OkCancelDialog dialog = new OkCancelDialog(parentActivity, dialogView, "Target Price", action);
        dialog.setCancel("Clear");


        lessThanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.ok(dialog, true);
            }
        });

        greaterThanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.ok(dialog, false);
            }
        });



        dialog.show();
    }

    private static class TargetDialogAction implements DialogAction {

        private final SymbolProfile profile;
        private final EditText targetText;
        private final StockPagerAdapter stockPagerAdapter;
        private boolean lessThanSelected;

        public TargetDialogAction(SymbolProfile profile, EditText targetText, StockPagerAdapter stockPagerAdapter) {
            this.profile = profile;
            this.targetText = targetText;
            this.stockPagerAdapter = stockPagerAdapter;
        }

        public void ok(Dialog dialog, boolean lessThan) {
            this.lessThanSelected = lessThan;
            this.ok(dialog);
        }

        @Override
        public void ok(Dialog dialog) {
            String targetStr = targetText.getText().toString();
            if (!targetStr.equals("")) {
                profile.targetPrice = Double.parseDouble(targetStr);
                if (lessThanSelected) {
                    profile.lessThanEqual = true;
                } else {
                    profile.lessThanEqual = false;
                }
            } else {
                profile.targetPrice = null;
                profile.lessThanEqual = null;
            }

            // Update cost basis view.
            stockPagerAdapter.updateCostBasisFragment(profile);

            ProfileManager.addSymbolData(profile);
            dialog.dismiss();
        }

        @Override
        public void cancel(Dialog dialog) {
            profile.targetPrice = null;
            profile.lessThanEqual = null;

            // Update cost basis view.
            stockPagerAdapter.updateCostBasisFragment(profile);

            ProfileManager.addSymbolData(profile);
            dialog.dismiss();
        }
    }
}
