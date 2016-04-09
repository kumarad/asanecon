package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.DialogAction;
import com.techan.custom.OkCancelDialog;
import com.techan.custom.Util;
import com.techan.profile.ProfileManager;

public class DeleteAllStocksDialog {
    public static void create(final Activity parentActivity) {
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.generic_ok_cancel_dialog, null);
        dialogView.findViewById(R.id.genericDialogText).setVisibility(View.GONE);
        DeleteAllStocksAction action = new DeleteAllStocksAction(parentActivity);
        OkCancelDialog dialog = new OkCancelDialog(parentActivity, dialogView, "Delete all stocks and portfolios.", action);
        dialog.setOk("OK");
        dialog.setCancel("Cancel");
        dialog.show();
    }

    private static class DeleteAllStocksAction implements DialogAction {

        private final Activity parentActivity;

        public DeleteAllStocksAction(Activity parentActivity) {
            this.parentActivity = parentActivity;
        }

        @Override
        public void ok(Dialog dialog) {
            if (!ProfileManager.deleteProfile(parentActivity.getApplicationContext())) {
                Util.showErrorToast(parentActivity, "Oops. Something on your device prevented profile data from being reset.");
            }

            parentActivity.getContentResolver().delete(StockContentProvider.CONTENT_URI, null, null);

            if (parentActivity instanceof HomeActivity) {
                ((HomeActivity) parentActivity).resetDrawer();
            }

            dialog.dismiss();
        }

        @Override
        public void cancel(Dialog dialog) {
            dialog.dismiss();
        }
    }
}
