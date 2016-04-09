package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.techan.R;
import com.techan.activities.StockPagerAdapter;
import com.techan.custom.DialogAction;
import com.techan.custom.OkCancelDialog;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class PeDialog {
    public static void create(Activity parentActivity, String symbol, final StockPagerAdapter stockPagerAdapter) {
        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);

        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.set_pe_target, null);

        // Handle edit text view.
        final EditText editText = ((EditText)dialogView.findViewById(R.id.edit_pe_target));
        if(profile.peTarget != null) {
            editText.setText(Double.toString(profile.peTarget));
        }

        // Set cursor to end of text.
        editText.setSelection(editText.length());

        PeDialogAction action = new PeDialogAction(profile, editText, stockPagerAdapter);
        OkCancelDialog dialog = new OkCancelDialog(parentActivity, dialogView, "Set PE Target", action);
        dialog.setOk("Set");
        dialog.setCancel("Clear");
        dialog.show();
    }

    private static class PeDialogAction implements DialogAction {

        private final SymbolProfile profile;
        private final EditText editText;
        private final StockPagerAdapter stockPagerAdapter;

        public PeDialogAction(SymbolProfile profile, EditText editText, StockPagerAdapter stockPagerAdapter) {
            this.profile = profile;
            this.editText = editText;
            this.stockPagerAdapter = stockPagerAdapter;
        }

        @Override
        public void ok(Dialog dialog) {
            String valText = editText.getText().toString();
            if (valText != null && !valText.isEmpty()) {
                Double val = Double.parseDouble(valText);
                if (val != null && val > 0) {
                    profile.peTarget = val;
                } else {
                    profile.peTarget = null;
                }
            } else {
                profile.peTarget = null;
            }

            ProfileManager.addSymbolData(profile);
            stockPagerAdapter.updateCostBasisFragment(profile);
            dialog.cancel();
        }

        @Override
        public void cancel(Dialog dialog) {
            profile.peTarget = null;
            ProfileManager.addSymbolData(profile);
            stockPagerAdapter.updateCostBasisFragment(profile);
            dialog.cancel();
        }
    }

}
