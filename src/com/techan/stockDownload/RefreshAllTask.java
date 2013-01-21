package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.database.StocksTable;

import java.util.*;

public class RefreshAllTask extends AsyncTask<String, Void, List<StockData>> {

    final List<String> symbols = new ArrayList<String>();
    final List<Uri> uris = new ArrayList<Uri>();

    String lastUpdate;
    final List<Double> upTrend_Counts = new ArrayList<Double>();
    final List<Double> highs_60Day = new ArrayList<Double>();
    final List<Double> lows_90Day = new ArrayList<Double>();

    final ContentResolver contentResolver;

    // Assumes being used for refresh of all symbols.
    public RefreshAllTask(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;

        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_LAST_UPDATE, StocksTable.COLUMN_UP_TREND_COUNT, StocksTable.COLUMN_60_DAY_HIGH, StocksTable.COLUMN_90_DAY_LOW};
        Cursor cursor = contentResolver.query(StockContentProvider.CONTENT_URI, projection, null, null, null);

        // Get all the symbols.
        cursor.moveToFirst();

        // Assumes that it is being invoked for refresh of all symbols and that
        // when home activity is created everything is refreshed!
        if(!cursor.isAfterLast()) {
            lastUpdate = cursor.getString(2);
        }

        while(!cursor.isAfterLast()) {
            symbols.add(cursor.getString(1));
            uris.add(Uri.parse(StockContentProvider.STOCK_URI_STR + Integer.toString(cursor.getInt(0))));
            upTrend_Counts.add(cursor.getDouble(3));
            highs_60Day.add(cursor.getDouble(4));
            lows_90Day.add(cursor.getDouble(5));
            cursor.moveToNext();
        }
    }

    // Download stock data for the symbol.
    @Override
    protected List<StockData> doInBackground(String... params) {
        // Get current date and time.
        Calendar curCal = Calendar.getInstance();
        Date curDate = curCal.getTime();
        String curDateStr = Util.formater.format(curDate);

        // Download real time data for stock symbols.
        List<StockData> dataList = DownloadQuote.download(symbols, curDateStr);

        // Download trend data if needed.
        if(!Util.isDateSame(lastUpdate, curCal)) {
            for(StockData data : dataList) {
                DownloadHistory.download(data, curCal);
            }
        }

        return dataList;
    }

    // Once data has been downloaded, update database.
    @Override
    protected void onPostExecute(List<StockData> dataList) {
        if(isCancelled()) {
            return;
        }

        int i = 0;
        for(StockData data : dataList) {
            ContentValues values = ContentValuesFactory.createContentValues(data, false);

            String[] selection = {StocksTable.COLUMN_LOW, StocksTable.COLUMN_HIGH};
            Cursor cursor = contentResolver.query(uris.get(i),selection, null, null, null);
            cursor.moveToFirst();
            if(cursor.getInt(0) == 0 || data.daysLow < cursor.getInt(0))
                values.put(StocksTable.COLUMN_LOW,data.daysLow);

            if(data.daysHigh > cursor.getInt(1))
                values.put(StocksTable.COLUMN_HIGH, data.daysHigh);

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
