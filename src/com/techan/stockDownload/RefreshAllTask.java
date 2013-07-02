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
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

import java.util.*;

// Don't need to worry about thread safety when accessing the ContentProvider
// because the SQLiteDatabase backing the content provider is thread safe!
public class RefreshAllTask extends AsyncTask<String, Void, List<StockData>> {

    final List<String> symbols = new ArrayList<String>();
    final List<Uri> uris = new ArrayList<Uri>();

    String lastUpdate;
    final List<Double> upTrend_Counts = new ArrayList<Double>();
    final List<Double> highs_60Day = new ArrayList<Double>();
    final List<Double> lows_90Day = new ArrayList<Double>();

    final ContentResolver contentResolver;
    final boolean auto;


    final Context ctx;

    // Assumes being used for refresh of all symbols.
    public RefreshAllTask(Context ctx, ContentResolver contentResolver, boolean auto) {
        this.ctx = ctx;
        this.contentResolver = contentResolver;
        this.auto = auto;

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
            ContentValues values = ContentValuesFactory.createContentValues(data);

            // Update the highest price seen for this stock.
            // We assume auto refresh is on otherwise might miss days and screw up stop loss math.
            // Only start tracking highest price if stopLossPercent is set indicating notification is wanted.
            // --- This ensures we start tracking from when stop loss notifications are enabled!
            SymbolProfile profile = ProfileManager.getSymbolData(ctx,data.symbol);
            if(profile.stopLossPercent != null) {
                if(!PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(SettingsActivity.AUTO_REFRESH_KEY, false)) {
                    // Auto refresh has been disabled. Disable stop loss notifications.
                    profile.stopLossPercent = null;
                    ProfileManager.addSymbolData(profile);
                } else {
                    if(data.daysHigh > profile.highestPrice) {
                        profile.highestPrice = data.daysHigh;
                        ProfileManager.addSymbolData(profile);
                    }
                }
            }
//            String[] selection = {StocksTable.COLUMN_DAYS_LOW, StocksTable.COLUMN_DAYS_HIGH};
//            Cursor cursor = contentResolver.query(uris.get(i),selection, null, null, null);
//            cursor.moveToFirst();
//            if(cursor.getInt(0) == 0 || data.daysLow < cursor.getInt(0))
//                values.put(StocksTable.COLUMN_DAYS_LOW,data.daysLow);
//
//            if(data.daysHigh > cursor.getInt(1))
//                values.put(StocksTable.COLUMN_DAYS_HIGH, data.daysHigh);
//

            contentResolver.update(uris.get(i), values, null, null);
            ++i;
        }
    }

    public void download() {
        if(auto) {
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
        }
    }
}
