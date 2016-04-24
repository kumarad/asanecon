package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.techan.R;
import com.techan.custom.DialogAction;
import com.techan.database.StocksTable;
import com.techan.memrepo.HistoryRepo;
import com.techan.stockDownload.actions.StockHistoryDownloadAction;
import com.techan.stockDownload.retro.StockHistoryDownloader;

public class HistoryDialogFactory {

    private static class HistoryDialogAction implements DialogAction {

        @Override
        public void ok(Dialog dialog) {
            dialog.dismiss();
        }

        @Override
        public void cancel(Dialog dialog) {
            dialog.dismiss();
        }
    }

    public static void create(final Activity parentActivity, final Uri stockUri, final String symbol) {
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.stock_history, null);

        final LineChart lineChart = (LineChart)dialogView.findViewById(R.id.stockHistoryChart);
        final View progressView = dialogView.findViewById(R.id.stockHistoryProgress);
        final View contentView = dialogView.findViewById(R.id.stockHistoryChartLayout);

        contentView.setVisibility(View.INVISIBLE);


        // Update db with stop loss information.
        ContentResolver cr = parentActivity.getContentResolver();
        Cursor cursor = cr.query(stockUri, null, null, null, null);
        Double change = null;
        if (cursor != null) {
            try {
                cursor.moveToFirst();
                change = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_CHANGE));
            } finally {
                cursor.close();
            }
        }

        int color;
        if (change != null) {
            if (change < 0) {
                color = parentActivity.getResources().getColor(R.color.asaneconRed);
            } else {
                color = parentActivity.getResources().getColor(R.color.asaneconGreen);
            }
        } else {
            return;
        }

        final StockHistoryDownloadAction postRefreshAction = new StockHistoryDownloadAction(symbol, lineChart, progressView, contentView, color);
        HistoryRepo repo = HistoryRepo.getStockRepo(symbol);
        String lastUpdateDateStr = null;
        if(repo != null) {
            if(repo.alreadyUpdatedToday()) {
                postRefreshAction.execute();
            } else if(repo.getPrices().size() > 0) {
                lastUpdateDateStr = repo.getLatestPriceDate();
            }
        }

        StockHistoryDownloader downloader = StockHistoryDownloader.getInstance();
        downloader.get(symbol, lastUpdateDateStr);


        HistoryDialog dialog = new HistoryDialog(parentActivity, dialogView, null, new HistoryDialogAction());
        dialog.addAction(postRefreshAction);
        dialog.setOk(" X ");
        dialog.show();
    }

}
