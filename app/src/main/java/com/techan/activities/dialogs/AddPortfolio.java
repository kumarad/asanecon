package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.custom.Util;
import com.techan.profile.ProfileManager;

public class AddPortfolio {
    public static void create(final HomeActivity parentActivity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Add Portfolio");

        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.portfolio_add, null);

        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doAdd(parentActivity, view);
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

    private static void doAdd(HomeActivity parentActivity, View view) {
        EditText addText = (EditText)view.findViewById(R.id.portfolioText);
        String portfolio = addText.getText().toString();
        if(verify(portfolio, parentActivity)) {
            if(!ProfileManager.addPortfolio(parentActivity, portfolio)) {
                Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile from being updated.");
            } else  {
                parentActivity.resetDrawer();
            }
        }
    }

    private static boolean verify(String portfolio, Activity parentActivity) {
        if(portfolio.length() == 0) {
            Util.showErrorToast(parentActivity, "Nothing input");
            return false;
        }

        // Check to see if the symbol is already in the database. Can't have duplicates.
        if(ProfileManager.getPortfolios(parentActivity).containsKey(portfolio)) {
            Util.showErrorToast(parentActivity, "Portfolio already exists");
            return false;
        }

        return true;
    }

}
