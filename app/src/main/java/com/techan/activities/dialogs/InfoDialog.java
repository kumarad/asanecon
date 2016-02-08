package com.techan.activities.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.techan.R;
import com.techan.custom.CustomTagHandler;

public class InfoDialog {
    public static void setOnClickInfoDialog(final View onClickView, final LayoutInflater inflater, final String info) {
        setOnClickInfoDialog(onClickView, inflater, info, false);
    }

    public static void setOnClickInfoDialog(final View onClickView, final LayoutInflater inflater, final String info, final boolean isHtml) {
        onClickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoDialog.create(v.getContext(), inflater, info, isHtml);
            }
        });
    }

    private static void create(Context context, LayoutInflater inflater, String info, boolean isHtml) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // Get layout inflater
        final View view = inflater.inflate(R.layout.info_dialog, null);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.create();

        final AlertDialog dialog = alertDialogBuilder.create();

        TextView infoView = (TextView)view.findViewById(R.id.info_dialog_text);

        if(isHtml) {
            infoView.setText(Html.fromHtml(info, null, new CustomTagHandler()));
        } else {
            infoView.setText(info);
        }

        View ackView = view.findViewById(R.id.info_dialog_ack);
        ackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
