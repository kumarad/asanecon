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
            symbols.add(cursor.getString(1));
            uris.add(Uri.parse(StockContentProvider.STOCK_URI_STR + Integer.toString(cursor.getInt(0))));
            cursor.moveToNext();
        }
    }

    // Download stock data for the symbol.
    @Override
    protected List<StockData> doInBackground(String... params) {
        return DownloadQuote.download(symbols);
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

            String[] selection = {StocksTable.COLUMN_LOW, StocksTable.COLUMN_HIGH};
            Cursor cursor = contentResolver.query(uris.get(i),selection, null, null, null);
            cursor.moveToFirst();
            if(cursor.getInt(0) == 0 || data.daysLow < cursor.getInt(0))
                values.put(StocksTable.COLUMN_LOW,data.daysLow);

            if(data.daysHigh > cursor.getInt(1))
                values.put(StocksTable.COLUMN_HIGH, data.daysHigh);

            values.put(StocksTable.COLUMN_PE, data.pe);
            values.put(StocksTable.COLUMN_PEG, data.peg);
            values.put(StocksTable.COLUMN_MOV_AVG_50, data.moveAvg50);
            values.put(StocksTable.COLUMN_MOV_AVG_200, data.moveAvg200);
            values.put(StocksTable.COLUMN_TRADING_VOLUME, data.tradingVol);
            values.put(StocksTable.COLUMN_NAME, data.name.replace("\"",""));

            contentResolver.update(uris.get(i), values, null, null);
            ++i;
        }
    }

    public void download() {
        if(symbols.size() != 0) {
            execute();
        }
    }
}
