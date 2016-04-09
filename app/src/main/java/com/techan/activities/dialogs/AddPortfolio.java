package com.techan.activities.dialogs;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.custom.DialogAction;
import com.techan.custom.OkCancelDialog;
import com.techan.custom.Util;
import com.techan.profile.ProfileManager;

public class AddPortfolio {
    public static void create(final HomeActivity parentActivity) {
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.portfolio_add, null);

        AddPortfolioAction action = new AddPortfolioAction(parentActivity, dialogView);
        OkCancelDialog dialog = new OkCancelDialog(parentActivity, dialogView, "Add Portfolio", action);
        dialog.setOk("Add");
        dialog.setCancel("Cancel");
        dialog.show();
    }

    private static class AddPortfolioAction implements DialogAction {

        private final HomeActivity parentActivity;
        private final View view;

        public AddPortfolioAction(HomeActivity parentActivity, View view) {
            this.parentActivity = parentActivity;
            this.view = view;
        }

        @Override
        public void ok(Dialog dialog) {
            EditText addText = (EditText) view.findViewById(R.id.portfolioText);
            String portfolio = addText.getText().toString();
            if (verify(portfolio)) {
                if (!ProfileManager.addPortfolio(parentActivity, portfolio)) {
                    Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile from being updated.");
                } else {
                    parentActivity.resetDrawer();
                }
            }
            dialog.dismiss();
        }

        @Override
        public void cancel(Dialog dialog) {
            dialog.cancel();
        }

        private boolean verify(String portfolio) {
            if (portfolio.length() == 0) {
                Util.showErrorToast(parentActivity, "Nothing input");
                return false;
            }

            // Check to see if the symbol is already in the database. Can't have duplicates.
            if (ProfileManager.getPortfolios(parentActivity).containsKey(portfolio)) {
                Util.showErrorToast(parentActivity, "Portfolio already exists");
                return false;
            }

            return true;
        }
    }

}
