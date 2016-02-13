package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.squareup.otto.Subscribe;
import com.techan.activities.BusService;
import com.techan.activities.SettingsActivity;
import com.techan.activities.fragments.StockListFragment;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.ConnectionStatus;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.memrepo.HistoryRepo;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.actions.PostRefreshAction;
import com.techan.stockDownload.retro.GoldDownloader;
import com.techan.stockDownload.retro.SPDownloader;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

// Don't need to worry about thread safety when accessing the ContentProvider
// because the SQLiteDatabase backing the content provider is thread safe!

public class RefreshTask extends AsyncTask<String, Void, List<StockData>> {
    ContentResolver contentResolver;
    boolean autoRefresh;

    final List<String> symbols = new ArrayList<>();
    final boolean shouldRefreshGold;
    final boolean shouldRefreshSP;
    final AtomicBoolean refreshingGoldData = new AtomicBoolean(false);
    final AtomicBoolean refreshingStockData = new AtomicBoolean(false);
    final AtomicBoolean refreshingSPData = new AtomicBoolean(false);

    final Map<String, Uri> downloadInfoMap = new HashMap<>();

    private void initialize(ContentResolver contentResolver, boolean autoRefresh) {
        BusService.getInstance().register(this);
        this.refreshingStockData.set(false);
        this.refreshingGoldData.set(false);
        this.refreshingSPData.set(false);
        this.contentResolver = contentResolver;
        this.autoRefresh = autoRefresh;
    }

    // Refresh everything.
    public RefreshTask(ContentResolver contentResolver, boolean autoRefresh) {
        initialize(contentResolver, autoRefresh);
        shouldRefreshGold = true;
        shouldRefreshSP = true;

        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_SYMBOL};
        Cursor cursor = contentResolver.query(StockContentProvider.CONTENT_URI, projection, null, null, null);
        if(cursor != null) {
            try {
                // Get all the symbols.
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String symbol = cursor.getString(1);
                    downloadInfoMap.put(symbol, Uri.parse(StockContentProvider.STOCK_URI_STR + Integer.toString(cursor.getInt(0))));

                    symbols.add(symbol);

                    cursor.moveToNext();
                }
            } finally {
                cursor.close();
            }
        }
    }

    // Refresh for particular stock.
    public RefreshTask(ContentResolver contentResolver, Uri uri, String symbol) {
        initialize(contentResolver, false);
        shouldRefreshGold = false;
        shouldRefreshSP = false;

        downloadInfoMap.put(symbol, uri);

        symbols.add(symbol);
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
        return DownloadQuote.download(symbols, Util.getDateStrForDb(Calendar.getInstance()));
    }

    // Once data has been downloaded, update database.
    @Override
    protected void onPostExecute(List<StockData> dataList) {
        if(isCancelled()) {
            refreshingStockData.set(false);
            endRefresh();
            return;
        }

        for(StockData data : dataList) {
            ContentValues values = ContentValuesFactory.createContentValues(data);
            contentResolver.update(downloadInfoMap.get(data.symbol), values, null, null);
        }

        if(action != null) {
            action.execute();
            action = null;
        }

        refreshingStockData.set(false);
        endRefresh();
    }

    @Subscribe
    public void goldRefreshCompleted(GoldDownloader.GoldDownloaderComplete event) {
        refreshingGoldData.set(false);
        endRefresh();
    }

    @Subscribe
    public void spRefreshCompleted(SPDownloader.SPDownloaderComplete event) {
        refreshingSPData.set(false);
        endRefresh();
    }

    public void endRefresh() {
        if(!refreshingStockData.get() && !refreshingGoldData.get() && !refreshingSPData.get()) {
            BusService.getInstance().post(new StockListFragment.RefreshCompleteEvent());
            BusService.getInstance().unregister(this);
        } // else one of the three hasn't completed wait for endRefresh to getGoldRepo called again.
    }

    // Used when we should only be doing stuff on the wifi network. Auto refresh etc uses this.
    // Also used when loading the stock list fragment
    public void download(Context ctx) {
        if(autoRefresh) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
            boolean refreshWifiOnly = sharedPref.getBoolean(SettingsActivity.REFRESH_WIFI_ONLY_KEY, false);
            ConnectionStatus connStatus = Util.isOnline(ctx);
            if(connStatus == ConnectionStatus.OFFLINE) {
                endRefresh();
                return;
            }

            // We are online!

            if(refreshWifiOnly && connStatus != ConnectionStatus.ONLINE_WIFI) {
                endRefresh();
                return;
            }
        }

        KeyStatsDownloader.download("IBM");
        boolean kickedOffGoldRefresh = false;
        boolean goldTrackingEnabled = PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(SettingsActivity.ENABLE_GOLD_TRACKER, false);
        if(shouldRefreshGold && goldTrackingEnabled) {
            kickedOffGoldRefresh = true;
            refreshingGoldData.set(true);
            GoldDownloader.getInstance().get(HistoryRepo.getGoldRepo().getLatestPriceDate());
        }

        boolean kickedOffSPRefresh = false;
        if(shouldRefreshSP && goldTrackingEnabled) {
            kickedOffSPRefresh = true;
            refreshingSPData.set(true);
            SPDownloader.getInstance().get(HistoryRepo.getSPRepo().getLatestPriceDate());
        }

        if(symbols.size() != 0) {
            refreshingStockData.set(true);
            execute();
        } else {
            if(!kickedOffGoldRefresh && !kickedOffSPRefresh) {
                endRefresh();
            }
        }
    }
}
