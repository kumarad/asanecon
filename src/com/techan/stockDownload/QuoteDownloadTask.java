package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.database.StocksTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class QuoteDownloadTask extends AsyncTask<String, Void, List<StockData>> {

    final List<String> symbols = new ArrayList<String>();
    final List<Uri> uris = new ArrayList<Uri>();

    String lastUpdate;
    List<Double> upTrend_Counts;
    List<Double> highs_60Day;
    List<Double> lows_90Day;

    final ContentResolver contentResolver;

    // Assumes being used for add.
    public QuoteDownloadTask(ContentResolver contentResolver, Uri addedUri, String symbol) {
        this.contentResolver = contentResolver;
        this.uris.add(addedUri);
        symbols.add(symbol);
    }

    // Assumes being used for refresh of all symbols.
    public QuoteDownloadTask(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;

        this.upTrend_Counts = new ArrayList<Double>();
        this.highs_60Day = new ArrayList<Double>();
        this.lows_90Day = new ArrayList<Double>();

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
        if(lastUpdate == null) {
            for(StockData data : dataList) {
                DownloadHistory.download(data, curCal);
            }
        } else {
            boolean sameDayUpdate = Util.isDateSame(lastUpdate, curCal);
            int i = 0;
            double low90Day;
            double high60Day;
            double upTrendCount;
            for(StockData data : dataList) {
                if(!sameDayUpdate)
                    upTrendCount = DownloadHistory.downloadForUpTrend(data, curCal);
                else
                    upTrendCount = upTrend_Counts.get(i);

                high60Day = highs_60Day.get(i);
                if(data.daysHigh > highs_60Day.get(i))
                    high60Day = data.daysHigh;

                low90Day = lows_90Day.get(i);
                if(data.daysLow < low90Day)
                    low90Day = data.daysLow;

                data.stockTrends = new StockTrends(upTrendCount, low90Day, high60Day);
                i++;
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
            values.put(StocksTable.COLUMN_AVG_TRADING_VOLUME, data.avgTradingVol);
            values.put(StocksTable.COLUMN_CHANGE, data.change);
            values.put(StocksTable.COLUMN_NAME, data.name.replace("\"",""));
            values.put(StocksTable.COLUMN_LAST_UPDATE, data.dateStr);

            StockTrends curTrends = data.stockTrends;
            values.put(StocksTable.COLUMN_UP_TREND_COUNT, curTrends.upTrendDayCount);
            values.put(StocksTable.COLUMN_60_DAY_HIGH, curTrends.high60Day);
            values.put(StocksTable.COLUMN_90_DAY_LOW, curTrends.low90Day);

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
