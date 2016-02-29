package com.techan.custom;

import android.app.AlertDialog;
import android.content.Context;

import com.squareup.otto.Subscribe;
import com.techan.activities.BusService;
import com.techan.stockDownload.DownloadTrendAndStopLossInfo;
import com.techan.stockDownload.actions.PostRefreshAction;

public class EventedAlertDialog extends AlertDialog {

    private PostRefreshAction action;

    public EventedAlertDialog(Context context) {
        super(context);
        BusService.getInstance().register(this);
    }

    @Subscribe
    public void done(DownloadTrendAndStopLossInfo.StopLossHistoryDownloaderComplete event) {
        if(action != null) {
            action.execute();
        }

        this.dismiss();
    }

    @Override
    public void dismiss() {
        BusService.getInstance().unregister(this);
        super.dismiss();
    }

    public void addAction(PostRefreshAction action)  {
        this.action = action;
    }

}
