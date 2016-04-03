package com.techan.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.techan.R;

public class OkCancelDialog extends AlertDialog {

    private final View okCancelView;
    private final TextView okView;
    private final TextView cancelView;
    private final DialogAction dialogAction;

    private boolean showOk;
    private boolean showCancel;
    private final Dialog me = this;

    public OkCancelDialog(Activity parentActivity,
                          View dialogView,
                          String title,
                          DialogAction dialogAction) {
        super(parentActivity);

        setView(dialogView);
        this.dialogAction = dialogAction;
        okCancelView =  dialogView.findViewById(R.id.okCancelView);

        final View titleView = dialogView.findViewById(R.id.dialogHeaderView);
        if (title != null && title.length() > 0) {
            titleView.setVisibility(View.VISIBLE);
            TextView textView = (TextView)dialogView.findViewById(R.id.dialogHeader);
            textView.setText(title);
        } else {
            titleView.setVisibility(View.GONE);
        }

        okView = (TextView)dialogView.findViewById(R.id.dialogOk);
        cancelView = (TextView)dialogView.findViewById(R.id.dialogCancel);
    }

    public void setOk(final String text) {
        okView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAction.ok(me);
            }
        });

        okView.setText(text);
        showOk = true;
    }

    public void setCancel(final String text) {
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAction.cancel(me);
            }
        });

        cancelView.setText(text);
        showCancel = true;
    }

    @Override
    public void show() {
        if (!showOk && !showCancel) {
            setVisibility(false, okCancelView);
        } else {
            setVisibility(showOk, okView);
            setVisibility(showCancel, cancelView);
        }

        super.show();
    }


    private void setVisibility(boolean show, View view) {
        if (show) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

}
