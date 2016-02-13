package com.techan.stockDownload;

import android.content.ContentValues;

import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.SymbolProfile;

import java.util.Calendar;

public class ContentValuesFactory {

    public static ContentValues createContentValues(StockData stockData) {
        ContentValues values = new ContentValues();

        values.put(StocksTable.COLUMN_PRICE, stockData.price);
        values.put(StocksTable.COLUMN_PE, stockData.pe);
        values.put(StocksTable.COLUMN_PEG, stockData.peg);
        values.put(StocksTable.COLUMN_DIV, stockData.div);
        values.put(StocksTable.COLUMN_MOV_AVG_50, stockData.moveAvg50);
        values.put(StocksTable.COLUMN_MOV_AVG_200, stockData.moveAvg200);
        values.put(StocksTable.COLUMN_TRADING_VOLUME, stockData.tradingVol);
        values.put(StocksTable.COLUMN_AVG_TRADING_VOLUME, stockData.avgTradingVol);
        values.put(StocksTable.COLUMN_CHANGE, stockData.change);
        values.put(StocksTable.COLUMN_NAME, stockData.name.replace("\"", ""));

        values.put(StocksTable.COLUMN_DAYS_LOW, stockData.daysLow);
        values.put(StocksTable.COLUMN_DAYS_HIGH, stockData.daysHigh);

        values.put(StocksTable.COLUMN_LAST_HISTORY_UPDATE, stockData.dateStr);

        return values;
    }

    public static ContentValues createTrendContentValues(StockTrends trends, boolean doStopLoss) {
        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_UP_TREND_COUNT, trends.upTrendDayCount);
        values.put(StocksTable.COLUMN_60_DAY_HIGH, trends.high60Day);
        values.put(StocksTable.COLUMN_90_DAY_LOW, trends.low90Day);

        if(doStopLoss) {
            values.put(StocksTable.COLUMN_SL_HIGEST_PRICE, trends.historicalHigh);
            values.put(StocksTable.COLUMN_SL_LOWEST_PRICE, trends.historicalLow);
            values.put(StocksTable.COLUMN_SL_LOWEST_PRICE_DATE, trends.historicalLowDate);
        }

        return values;
    }

    // Invoked when starting to track the stop loss for a stock using the StopLossDialog.
    // Since the stop loss tracking is being started from the current date we set the highest price
    // to the higher value between the current price and buy price. We set the lowest price to the lower
    // value. When the history is downloaded using DownloadHistory, we compare the history against these
    // values to detect new lows and highs.
    public static ContentValues createSlAddValuesSameDate(double curPrice, double buyPrice) {
        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SL_HIGEST_PRICE, (curPrice > buyPrice ? curPrice : buyPrice));
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE, (curPrice < buyPrice ? curPrice : buyPrice));
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE_DATE, Util.getDateStrForDb(Calendar.getInstance()));
        return values;
    }

    // Invoked when starting to track the stop loss for a stock using the StopLossDialog.
    // This method is invoked if the date to start tracking the stop loss is earlier than the
    // current date. We set the start high/low for stop loss purposes to the buy price and when
    // DownloadHistory is invoked it compares to the buy price to detect the lowest low and the
    // highest high.
    public static ContentValues createSlAddValuesDiffDate(double buyPrice, String slTrackingStartDate) {
        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SL_HIGEST_PRICE, buyPrice);
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE, buyPrice);
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE_DATE, slTrackingStartDate);

        // Need to set last updated to an earlier date to ensure RefreshTask doesn't
        // think its already updated.
        values.put(StocksTable.COLUMN_LAST_HISTORY_UPDATE, slTrackingStartDate);
        return values;
    }

    // Invoked when we have lost our db copy and we are recovering from the profile
    // stored in our json backup file.
    public static ContentValues createValuesForRecovery(SymbolProfile profile) {
        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SYMBOL, profile.symbol);
        values.put(StocksTable.COLUMN_SL_HIGEST_PRICE, profile.buyPrice);
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE, profile.buyPrice);
        values.put(StocksTable.COLUMN_LAST_HISTORY_UPDATE, profile.slTrackingStartDate);
        return values;
    }


}
