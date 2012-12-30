package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import com.techan.contentProvider.StockContentProvider;
import com.techan.database.StocksTable;

public class QuoteDownloadTask extends AsyncTask<String, Void, StockData> {

    final String symbol;

    final ContentResolver contentResolver;
    final Uri addedUri;

    public QuoteDownloadTask(String symbol, ContentResolver contentResolver, Uri addedUri) {
        this.symbol = symbol;
        this.contentResolver = contentResolver;
        this.addedUri = addedUri;
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

        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_PRICE, data.price);
        //contentResolver.update(StockContentProvider.CONTENT_URI, values, "sym='" + symbol + "'", null);
        Uri uri = Uri.parse(StockContentProvider.STOCK_URI + addedUri);
        contentResolver.update(uri, values, null, null);
    }
}
