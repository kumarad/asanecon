package com.techan.stockDownload;

import android.content.ContentValues;

import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.SymbolProfile;

import java.util.Calendar;

public class ContentValuesFactory {

    public static ContentValues createContentValues(StockData stockData, boolean doStopLoss) {
        ContentValues values = new ContentValues();

        values.put(StocksTable.COLUMN_PRICE, stockData.price);
        values.put(StocksTable.COLUMN_PE, stockData.pe);
        values.put(StocksTable.COLUMN_PEG, stockData.peg);
        values.put(StocksTable.COLUMN_MOV_AVG_50, stockData.moveAvg50);
        values.put(StocksTable.COLUMN_MOV_AVG_200, stockData.moveAvg200);
        values.put(StocksTable.COLUMN_TRADING_VOLUME, stockData.tradingVol);
        values.put(StocksTable.COLUMN_AVG_TRADING_VOLUME, stockData.avgTradingVol);
        values.put(StocksTable.COLUMN_CHANGE, stockData.change);
        values.put(StocksTable.COLUMN_NAME, stockData.name.replace("\"",""));

        StockTrends curTrends = stockData.stockTrends;
        if(curTrends != null) {
            values.put(StocksTable.COLUMN_UP_TREND_COUNT, curTrends.upTrendDayCount);
            values.put(StocksTable.COLUMN_60_DAY_HIGH, curTrends.high60Day);
            values.put(StocksTable.COLUMN_90_DAY_LOW, curTrends.low90Day);

            if(doStopLoss) {
                values.put(StocksTable.COLUMN_SL_HIGEST_PRICE, curTrends.historicalHigh);
                values.put(StocksTable.COLUMN_SL_LOWEST_PRICE, curTrends.historicalLow);
                values.put(StocksTable.COLUMN_SL_LOWEST_PRICE_DATE, curTrends.historicalLowDate);
            }
        }

        values.put(StocksTable.COLUMN_DAYS_LOW, stockData.daysLow);
        values.put(StocksTable.COLUMN_DAYS_HIGH, stockData.daysHigh);

        values.put(StocksTable.COLUMN_LAST_UPDATE, stockData.dateStr);

        return values;
    }

    public static ContentValues createSlAddValues(double curPrice, double buyPrice) {
        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SL_HIGEST_PRICE, (curPrice > buyPrice ? curPrice : buyPrice));
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE, (curPrice < buyPrice ? curPrice : buyPrice));
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE_DATE, Util.getDateStrForDb(Calendar.getInstance()));
        return values;
    }

    public static ContentValues createValuesForRecovery(SymbolProfile profile) {
        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SYMBOL, profile.symbol);
        values.put(StocksTable.COLUMN_SL_HIGEST_PRICE, profile.buyPrice);
        values.put(StocksTable.COLUMN_SL_LOWEST_PRICE, profile.buyPrice);
        values.put(StocksTable.COLUMN_LAST_UPDATE, profile.stopLossBuyDate);
        return values;
    }


}
