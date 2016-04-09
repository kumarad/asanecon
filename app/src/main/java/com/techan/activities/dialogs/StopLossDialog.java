package com.techan.activities.dialogs;

import android.content.Context;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.techan.custom.DialogAction;
import com.techan.custom.EventedAlertDialog;
import com.techan.stockDownload.DownloadTrendAndStopLossInfo;

public class StopLossDialog extends EventedAlertDialog {

    public StopLossDialog(Context context,
                          View dialogView,
                          String title,
                          DialogAction dialogAction) {
        super(context, dialogView, title, dialogAction);
    }

    @Subscribe
    public void done(DownloadTrendAndStopLossInfo.StopLossHistoryDownloaderComplete event) {
        if(action != null) {
            action.execute();
        }

        this.dismiss();
    }

}
