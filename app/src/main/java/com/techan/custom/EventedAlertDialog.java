package com.techan.custom;

import android.content.Context;
import android.view.View;

import com.techan.activities.BusService;
import com.techan.stockDownload.actions.PostRefreshAction;

public abstract class EventedAlertDialog extends OkCancelDialog {
    protected PostRefreshAction action;

    public EventedAlertDialog(Context context,
                              View dialogView,
                              String title,
                              DialogAction dialogAction) {
        super(context, dialogView, title, dialogAction);
        BusService.getInstance().register(this);
    }

    @Override
    public void cancel() {
        BusService.getInstance().unregister(this);
        super.cancel();
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
