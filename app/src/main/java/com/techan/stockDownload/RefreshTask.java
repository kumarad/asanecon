package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.techan.activities.SettingsActivity;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.ConnectionStatus;
import com.techan.custom.ISwipeRefreshDelegate;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.actions.PostRefreshAction;

import java.util.*;

// Don't need to worry about thread safety when accessing the ContentProvider
// because the SQLiteDatabase backing the content provider is thread safe!

public class RefreshTask extends AsyncTask<String, Void, List<StockData>> {

    final Context ctx;
    final ContentResolver contentResolver;
    final boolean autoRefresh;

    final Calendar curCal;
    final String curDateStr;

    final List<String> symbols = new ArrayList<>();

    static class DownloadInfo {
        Uri uri;
        String lastUpdate;
        Integer stopLossPercent;
        Double historicalHigh;      // used for sl tracking
        Double historicalLow;       // used for sl tracking
    }

    final Map<String, DownloadInfo> downloadInfoMap = new HashMap<>();
    private ISwipeRefreshDelegate delegate = null;

    public RefreshTask(Context ctx, ContentResolver contentResolver, boolean autoRefresh, ISwipeRefreshDelegate delegate) {
        this(ctx, contentResolver, autoRefresh);
        this.delegate = delegate;
    }

    // Refresh all.
    public RefreshTask(Context ctx, ContentResolver contentResolver, boolean autoRefresh) {
        this.ctx = ctx;
        this.contentResolver = contentResolver;
        this.autoRefresh = autoRefresh;

        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_LAST_UPDATE, StocksTable.COLUMN_SL_HIGEST_PRICE, StocksTable.COLUMN_SL_LOWEST_PRICE};
        Cursor cursor = contentResolver.query(StockContentProvider.CONTENT_URI, projection, null, null, null);

        // Get all the symbols.
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            DownloadInfo info = new DownloadInfo();

            info.uri = Uri.parse(StockContentProvider.STOCK_URI_STR + Integer.toString(cursor.getInt(0)));
            String symbol = cursor.getString(1);
            info.lastUpdate = cursor.getString(2);

            SymbolProfile symbolProfile = ProfileManager.getSymbolData(ctx, symbol);
            info.stopLossPercent = symbolProfile.stopLossPercent;
            if(info.stopLossPercent != null) {
                info.historicalHigh = cursor.getDouble(3);
                info.historicalLow = cursor.getDouble(4);
            }

            downloadInfoMap.put(symbol, info);
            symbols.add(symbol);

            cursor.moveToNext();
        }

        // Get current date and time.
        curCal = (Calendar)Calendar.getInstance().clone();
        curDateStr = Util.getDateStrForDb(curCal);
    }

    // Refresh for particular stock.
    public RefreshTask(Context ctx, ContentResolver contentResolver, Uri uri, String symbol, boolean isAdd) {
        this.ctx = ctx;
        this.contentResolver = contentResolver;
        this.autoRefresh = false;

        DownloadInfo info = new DownloadInfo();

        info.uri = uri;

        if(isAdd) {
            info.lastUpdate = null;
            info.stopLossPercent = null;
        } else {
            String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_LAST_UPDATE, StocksTable.COLUMN_SL_HIGEST_PRICE, StocksTable.COLUMN_SL_LOWEST_PRICE};
            Cursor cursor = contentResolver.query(info.uri, projection, null, null, null);
            cursor.moveToFirst();
            info.lastUpdate = cursor.getString(1);

            SymbolProfile symbolProfile = ProfileManager.getSymbolData(ctx, symbol);
            info.stopLossPercent = symbolProfile.stopLossPercent;
            if(info.stopLossPercent != null) {
                info.historicalHigh = cursor.getDouble(2);
                info.historicalLow = cursor.getDouble(3);
            }
        }

        downloadInfoMap.put(symbol, info);
        symbols.add(symbol);

        // Get current date and time.
        curCal = (Calendar)Calendar.getInstance().clone();
        curDateStr = Util.getDateStrForDb(curCal);
    }

    private PostRefreshAction action = null;
    public void addAction(PostRefreshAction action)  {
        if(this.action == null) {
            this.action = action;
        } else {
            throw new RuntimeException("Only one action supported at a time.");
        }
    }

    // Download stock data for the symbol.
    @Override
    protected List<StockData> doInBackground(String... params) {
        // Download real time data for stock symbols.
        List<StockData> dataList = DownloadQuote.download(symbols, curDateStr);

        // Download trend data if needed.
        for(StockData data : dataList) {
            DownloadInfo info = downloadInfoMap.get(data.symbol);
            String lastUpdate = info.lastUpdate;
            Integer stopLossPercent = info.stopLossPercent;

            // isDateSame will return false when null is passed for lastUpdate.
            // When a stock is added, lastUpdate = null;
            // When stock is recovered from profile, lastUpdate is null if no stop loss tracking
            //                                       lastUpdate is buy price date for stop loss if tracking
            // When refreshed, last update is last time both trends and stop loss info was updated.
            if(!Util.isDateSame(lastUpdate, curCal)) {
                if(stopLossPercent != null) {
                    DownloadHistory.download(data, curCal, lastUpdate, info.historicalHigh, info.historicalLow);
                } else {
                    DownloadHistory.download(data, curCal);
                }
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

        for(StockData data : dataList) {
            DownloadInfo info = downloadInfoMap.get(data.symbol);
            ContentValues values = ContentValuesFactory.createContentValues(data, (info.stopLossPercent != null));
            contentResolver.update(info.uri, values, null, null);
        }

        if(action != null) {
            action.execute();
            action = null;
        }

        if(delegate != null)
            delegate.done();
    }

    // Used when we should only be doing stuff on the wifi network. Auto refresh etc uses this.
    public void download() {
        if(autoRefresh) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
            boolean refreshWifiOnly = sharedPref.getBoolean(SettingsActivity.REFRESH_WIFI_ONLY_KEY, false);
            ConnectionStatus connStatus = Util.isOnline(ctx);
            if(connStatus == ConnectionStatus.OFFLINE) {
                return;
            }

            // We are online!

            if(refreshWifiOnly && connStatus != ConnectionStatus.ONLINE_WIFI) {
                return;
            }
        }

        if(symbols.size() != 0) {
            execute();
        } else {
            if(delegate != null) {
                delegate.done();
            }
        }
    }
}
