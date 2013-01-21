package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import com.techan.custom.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DownloadNewSymbolTask extends AsyncTask<String, Void, StockData> {

    final String symbol;
    final Uri uri;

    final ContentResolver contentResolver;

    // Assumes being used for add.
    public DownloadNewSymbolTask(ContentResolver contentResolver, Uri addedUri, String symbol) {
        this.symbol = symbol;
        this.uri = addedUri;
        this.contentResolver = contentResolver;
    }

    // Download stock data for the symbol.
    @Override
    protected StockData doInBackground(String... params) {
        // Get current date and time.
        Calendar curCal = Calendar.getInstance();
        Date curDate = curCal.getTime();
        String curDateStr = Util.formater.format(curDate);

        // Download real time data for stock symbols.
        List<String> symbolList = new ArrayList<String>();
        symbolList.add(symbol);
        List<StockData> dataList = DownloadQuote.download(symbolList, curDateStr);

        if(dataList.size() != 1) {
            throw new RuntimeException("DownloadQuote returned more than 1 symbol data.");
        }

        StockData stockData = dataList.get(0);

        // Download trend data if needed.
        DownloadHistory.download(stockData, curCal);

        return stockData;
    }

    // Once data has been downloaded, update database.
    @Override
    protected void onPostExecute(StockData stockData) {
        if(isCancelled()) {
            return;
        }

        ContentValues values = ContentValuesFactory.createContentValues(stockData, true);
        contentResolver.update(uri, values, null, null);
    }

    public void download() {
        if(symbol != null)
            execute();
    }
}
