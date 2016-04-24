package com.techan.custom;

import android.content.Context;
import android.view.View;

import com.techan.activities.BusService;
import com.techan.stockDownload.actions.PostRefreshAction;

public abstract class EventedAlertDialog extends OkCancelDialog {
    protected PostRefreshAction action;
    private boolean registered;

    public EventedAlertDialog(Context context,
                              View dialogView,
                              String title,
                              DialogAction dialogAction) {
        super(context, dialogView, title, dialogAction);
        BusService.getInstance().register(this);
        registered = true;
    }

    @Override
    public void cancel() {
        if(registered) {
            BusService.getInstance().unregister(this);
            registered = false;
        }

        super.cancel();
    }

    @Override
    public void dismiss() {
        //Handles the case where cancel invokes dismiss.
        if(registered) {
            BusService.getInstance().unregister(this);
            registered = false;
        }

        super.dismiss();
    }

    public void addAction(PostRefreshAction action)  {
        this.action = action;
    }

}
