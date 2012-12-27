package com.techan.stockDownload;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class QuoteDownloadTask extends AsyncTask<String, Void, StockData> {

    final String symbol;

    // A download in progress won't prevent a killed activity's TextView from being
    // garbage collected by making this reference a weak reference.
    final WeakReference<TextView> textViewReference;

    public QuoteDownloadTask(String symbol, TextView textView) {
        this.symbol = symbol;
        this.textViewReference = new WeakReference<TextView>(textView);
    }

    // Download stock data for the symbol.
    @Override
    protected StockData doInBackground(String... params) {
        return DownloadQuote.download(symbol, StockData.NAME, StockData.LAST_TRADE_PRICE);
    }

    // Once data has been downloaded, associated it with the appropriate view.
    @Override
    protected void onPostExecute(StockData data) {
        if(isCancelled()) {
            data = null;
        }

        if(textViewReference != null) {
            // Hasn't been garbage collected.
            TextView textView = textViewReference.get();
            if(textView != null) {
                textView.setText(data.priceStr);
            }
        }
    }
}
