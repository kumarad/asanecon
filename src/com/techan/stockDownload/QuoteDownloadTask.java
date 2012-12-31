package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import com.techan.contentProvider.StockContentProvider;
import com.techan.database.StocksTable;

import java.util.ArrayList;
import java.util.List;

public class QuoteDownloadTask extends AsyncTask<String, Void, List<StockData>> {

    final List<String> symbols = new ArrayList<String>();
    final List<Uri> uris = new ArrayList<Uri>();

    final ContentResolver contentResolver;

    public QuoteDownloadTask(ContentResolver contentResolver, Uri addedUri, String symbol) {
        this.contentResolver = contentResolver;
        this.uris.add(addedUri);
        symbols.add(symbol);
    }

    public QuoteDownloadTask(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;

        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_SYMBOL};
        Cursor cursor = contentResolver.query(StockContentProvider.CONTENT_URI, projection, null, null, null);

        // Get all the symbols.
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            symbols.add(cursor.getString(StocksTable.COLUMN_SYMBOL_INDEX));
            uris.add(Uri.parse(StockContentProvider.STOCK_URI_STR + Integer.toString(cursor.getInt(StocksTable.COLUMN_ID_INDEX))));
            cursor.moveToNext();
        }

    }

    // Download stock data for the symbol.
    @Override
    protected List<StockData> doInBackground(String... params) {
        return DownloadQuote.download(symbols, StockData.NAME, StockData.LAST_TRADE_PRICE);
    }

    // Once data has been downloaded, update database.
    @Override
    protected void onPostExecute(List<StockData> dataList) {
        if(isCancelled()) {
            return;
        }

        int i = 0;
        for(StockData data : dataList) {
            ContentValues values = new ContentValues();
            values.put(StocksTable.COLUMN_PRICE, data.price);
            contentResolver.update(uris.get(i), values, null, null);
            ++i;
        }
    }
}
