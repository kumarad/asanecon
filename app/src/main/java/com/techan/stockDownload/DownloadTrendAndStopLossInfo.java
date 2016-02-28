package com.techan.stockDownload;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.techan.activities.BusService;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.retro.AbstractStockHistoryDownloader;

import java.util.Calendar;
import java.util.Map;
import java.util.SortedMap;

public class DownloadTrendAndStopLossInfo extends AbstractStockHistoryDownloader {
    public static final int DAY_COUNT_10 = 10;
    public static final int DAY_COUNT_60 = 60;
    public static final int DAY_COUNT_90 = 90;

    public static final int DATE_INDEX = 0;
    public static final int DAY_HIGH_INDEX = 2;
    public static final int DAY_LOW_INDEX = 3;
    public static final int DAY_CLOSE_INDEX = 4;

    public static class StopLossHistoryDownloaderComplete {}

    private final ContentResolver contentResolver;
    private final Uri uri;
    private HistoryInfo historyInfo;
    private TrackingStartCounts trackingStartCounts;
    private boolean includeSL;

    public DownloadTrendAndStopLossInfo(String symbol,
                                        Context ctx,
                                        ContentResolver contentResolver,
                                        Uri uri) {
        this.contentResolver = contentResolver;
        this.uri = uri;

        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_LAST_HISTORY_UPDATE, StocksTable.COLUMN_SL_HIGEST_PRICE, StocksTable.COLUMN_SL_LOWEST_PRICE};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if(cursor != null) {
            try {
                SymbolProfile symbolProfile = ProfileManager.getSymbolData(ctx, symbol);
                startDownload(cursor, symbolProfile);
            } finally {
                cursor.close();
            }
        } else {
            done();
        }
    }

    private void startDownload(Cursor cursor,
                                SymbolProfile symbolProfile) {
        Calendar curCalDate = Calendar.getInstance();

        cursor.moveToFirst();
        String lastUpdate = cursor.getString(1);

        double historicalHigh = 0;
        double historicalLow = 0;
        if (symbolProfile.stopLossPercent != null) {
            includeSL = true;
            historicalHigh = cursor.getDouble(2);
            historicalLow = cursor.getDouble(3);
        } else {
            includeSL = false;
        }

        Calendar curCal = (Calendar) Calendar.getInstance().clone();
        if (!Util.isDateSame(lastUpdate, curCal)) {
            trackingStartCounts = trackerCounts(curCalDate, lastUpdate);
            if (includeSL) {
                // If SL information needs to be calculated from a period longer than 90 days ago trackingStartCounts will be set to that date.
                historyInfo = new HistoryInfo(historicalHigh, historicalLow);
                download(symbolProfile.symbol, trackingStartCounts.getStartCal(), curCalDate);
            } else {
                historyInfo = new HistoryInfo();
                download(symbolProfile.symbol, trackingStartCounts.getStartCal(), curCalDate);
            }
        } else {
            done();
        }
    }

    @Override
    public void done() {
        BusService.getInstance().post(new StopLossHistoryDownloaderComplete());
    }

    // Calendar and Yahoo uses 0 based month.
    private TrackingStartCounts trackerCounts(Calendar curCal, final String lastSLUpdate) {
        Calendar cal90 = (Calendar)curCal.clone();
        cal90.add(Calendar.DAY_OF_MONTH, DAY_COUNT_90 * -1);

        Calendar calSl;
        if(lastSLUpdate != null) {
            calSl = Util.getCal(lastSLUpdate);
        } else {
            calSl = cal90;
        }

        if(calSl.before(cal90)) {
            // Stop loss last update was done longer than 90 days ago.
            int totalDaysBack = Util.dateDiff(calSl, curCal);
            int startCountFor90Day = totalDaysBack - Util.dateDiff(cal90, curCal);
            return new TrackingStartCounts(calSl, startCountFor90Day, 0);
        } else {
            // Stop loss last update is within the 90 day period.
            int startCountForSl = Util.dateDiff(cal90, calSl);
            return new TrackingStartCounts(cal90, 0, startCountForSl);
        }
    }

    private void handleTrends(HistoryInfo historyInfo, StockDayPriceInfo priceInfo, int count) {
        double closePrice = priceInfo.getClosingPrice();
        if(count >= trackingStartCounts.getStartCountFor60Day() && closePrice > historyInfo.high60Day) {
            historyInfo.high60Day = closePrice;
        }

        if(count >= trackingStartCounts.getStartCountFor90Day() && closePrice < historyInfo.low90Day ) {
            historyInfo.low90Day = closePrice;
        }

        if(count >= trackingStartCounts.getStartCountFor10Day() && historyInfo.trackUpTrend) {
            if(closePrice < historyInfo.prevDayClose) {
                // Previous days close is lower. Stock went up.
                historyInfo.upTrendDayCount++;
                historyInfo.prevDayClose = closePrice;
            } else {
                // Previous days price is higher. Not an uptrend.
                // Stop trying to calculate the uptrend.
                historyInfo.trackUpTrend = false;
                if(count == 2) {
                    historyInfo.upTrendDayCount = 0;
                }
            }
        }
    }

    private void handleStopLoss(HistoryInfo historyInfo, String dateStr, StockDayPriceInfo priceInfo, int count) {
        if(count >= trackingStartCounts.getStartCountForSl()) {  // Only track those days that are within the range that is between the stop loss start date and the cur date.
            double daysHigh = priceInfo.getHigh();
            if(daysHigh > historyInfo.historicalHigh) {
                historyInfo.historicalHigh = daysHigh;
            }

            double daysLow = priceInfo.getLow();
            if(daysLow < historyInfo.historicalLow) {
                historyInfo.historicalLow = daysLow;
                historyInfo.historicalLowDate = Util.getCalStrFromNoTimeStr(dateStr);
            }
        }
    }

    @Override
    public void handleHistory(String symbol, SortedMap<String, StockDayPriceInfo> prices) {
        int i = 0;
        for(Map.Entry<String, StockDayPriceInfo> entry : prices.entrySet()) {
            i++;
            handleTrends(historyInfo, entry.getValue(), i);
            if(includeSL) {
                handleStopLoss(historyInfo, entry.getKey(), entry.getValue(), i);
            }
        }

        StockTrends stockTrends = new StockTrends(historyInfo.upTrendDayCount,
                                                  historyInfo.high60Day,
                                                  historyInfo.low90Day);
        if(includeSL) {
            stockTrends.setHistoricalInfo(historyInfo.historicalHigh,
                                          historyInfo.historicalLow,
                                          historyInfo.historicalLowDate);
        }

        ContentValues values = ContentValuesFactory.createTrendContentValues(stockTrends, includeSL);
        contentResolver.update(uri, values, null, null);
    }

}

