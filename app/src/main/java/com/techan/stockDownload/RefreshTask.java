package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.squareup.otto.Subscribe;
import com.techan.activities.BusService;
import com.techan.activities.SettingsActivity;
import com.techan.activities.fragments.StockListFragment;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.ConnectionStatus;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.memrepo.GoldRepo;

import java.util.*;

// Don't need to worry about thread safety when accessing the ContentProvider
// because the SQLiteDatabase backing the content provider is thread safe!
public class RefreshTask {
    private static final String GOLD_SYMBOL = "XAUUSD=X";
    private ContentResolver contentResolver;
    private boolean autoRefresh;

    private final Set<String> symbols = new HashSet<>();

    private final Map<String, Uri> downloadInfoMap = new HashMap<>();
    private final boolean downloadGoldSpotPrice;
    private boolean goldPriceRequestedAsSymbol;

    private void initialize(ContentResolver contentResolver, boolean autoRefresh) {
        BusService.getInstance().register(this);
        this.contentResolver = contentResolver;
        this.autoRefresh = autoRefresh;
    }

    // Refresh everything.
    public RefreshTask(ContentResolver contentResolver, boolean autoRefresh) {
        this.downloadGoldSpotPrice = true;
        initialize(contentResolver, autoRefresh);

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
        downloadGoldSpotPrice = false;
        initialize(contentResolver, false);

        downloadInfoMap.put(symbol, uri);

        symbols.add(symbol);
    }

    // Once data has been downloaded, update database.
    @Subscribe
    public void done(DownloadQuote.DownloadQuoteComplete event) {
        Map<String, StockData> dataMap = event.getStockData();

        if(downloadGoldSpotPrice) {
            // We need to make sure we add the spot price to the GoldRepo
            StockData data = dataMap.get(GOLD_SYMBOL);
            if(data != null) {
                GoldRepo.getRepo().setSpotPrice(data.price);
            }

            if(!goldPriceRequestedAsSymbol) {
                // The gold price isn't required for regular stock tracking.
                // So make sure we don't add it to the DB.
                dataMap.remove(GOLD_SYMBOL);
            }
        }

        for(StockData data : dataMap.values()) {
            ContentValues values = ContentValuesFactory.createContentValues(data);
            contentResolver.update(downloadInfoMap.get(data.symbol), values, null, null);
        }

        endRefresh();
    }

    private void endRefresh() {
        BusService.getInstance().post(new StockListFragment.RefreshCompleteEvent());
        BusService.getInstance().unregister(this);
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

        if(symbols.size() != 0) {
            // Download real time data for stock symbols.
            if (symbols.contains(GOLD_SYMBOL)) {
                // The user added the stock symbol outside of the gold tracker.
                goldPriceRequestedAsSymbol = true;
            }
        }

        if (downloadGoldSpotPrice){
            // We need to add the symbol since we need it to track the spot price. Might already be there. But thats ok.
            symbols.add(GOLD_SYMBOL);
        }

        if (symbols.size() != 0) {
            DownloadQuote.download(symbols);
        } else {
            endRefresh();
        }
    }
}
