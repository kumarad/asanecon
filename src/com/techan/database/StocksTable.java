package com.techan.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class StocksTable {
    // Database table
    public static final String TABLE_STOCKS = "stocks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SYMBOL = "sym";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DAYS_LOW = "daysLow";
    public static final String COLUMN_DAYS_HIGH = "daysHigh";
    public static final String COLUMN_PE = "pe";
    public static final String COLUMN_PEG = "peg";
    public static final String COLUMN_MOV_AVG_50 = "movAvg50";
    public static final String COLUMN_MOV_AVG_200 = "movAvg200";
    public static final String COLUMN_TRADING_VOLUME = "tradeVol";
    public static final String COLUMN_AVG_TRADING_VOLUME = "avgTradeVol";
    public static final String COLUMN_CHANGE = "change";
    public static final String COLUMN_UP_TREND_COUNT = "upTrendCount";
    public static final String COLUMN_60_DAY_HIGH = "high60Day";
    public static final String COLUMN_90_DAY_LOW = "low90Day";
    public static final String COLUMN_LAST_UPDATE = "lastUpdate";
    public static final String COLUMN_SL_HIGEST_PRICE = "slHighestPrice";
    public static final String COLUMN_SL_LOWEST_PRICE = "slLowestPrice";
    public static final String COLUMN_SL_LOWEST_PRICE_DATE = "slLowestPriceDate";
    public static final String COLUMN_NAME = "name";


    public static Map<String, Integer> stockColumns = new HashMap<String, Integer>();
    static {
        int i = 0;
        stockColumns.put(COLUMN_ID, i++);
        stockColumns.put(COLUMN_SYMBOL, i++);
        stockColumns.put(COLUMN_PRICE, i++);
        stockColumns.put(COLUMN_DAYS_LOW, i++);
        stockColumns.put(COLUMN_DAYS_HIGH, i++);
        stockColumns.put(COLUMN_PE, i++);
        stockColumns.put(COLUMN_PEG, i++);
        stockColumns.put(COLUMN_MOV_AVG_50, i++);
        stockColumns.put(COLUMN_MOV_AVG_200, i++);
        stockColumns.put(COLUMN_TRADING_VOLUME, i++);
        stockColumns.put(COLUMN_AVG_TRADING_VOLUME, i++);
        stockColumns.put(COLUMN_CHANGE, i++);
        stockColumns.put(COLUMN_UP_TREND_COUNT, i++);
        stockColumns.put(COLUMN_60_DAY_HIGH, i++);
        stockColumns.put(COLUMN_90_DAY_LOW, i++);
        stockColumns.put(COLUMN_LAST_UPDATE, i++);
        stockColumns.put(COLUMN_SL_HIGEST_PRICE, i++);
        stockColumns.put(COLUMN_SL_LOWEST_PRICE, i++);
        stockColumns.put(COLUMN_SL_LOWEST_PRICE_DATE, i++);
        stockColumns.put(COLUMN_NAME, i++);
    }

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " + TABLE_STOCKS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_SYMBOL + " text unique not null, " +
            COLUMN_PRICE + " real default 0, " +
            COLUMN_DAYS_LOW + " real default 0, " +
            COLUMN_DAYS_HIGH + " real default 0, " +
            COLUMN_PE + " real default 0, " +
            COLUMN_PEG + " real default 0, " +
            COLUMN_MOV_AVG_50 + " real default 0, " +
            COLUMN_MOV_AVG_200 + " real default 0, " +
            COLUMN_TRADING_VOLUME + " real default 0, " +
            COLUMN_AVG_TRADING_VOLUME + " real default 0, " +
            COLUMN_CHANGE + " real default 0, " +
            COLUMN_UP_TREND_COUNT + " real default 0, " +
            COLUMN_60_DAY_HIGH + " real default 0, " +
            COLUMN_90_DAY_LOW + " real default 0," +
            COLUMN_LAST_UPDATE + " text, " +
            COLUMN_SL_HIGEST_PRICE + " real default 0, " +
            COLUMN_SL_LOWEST_PRICE + " real default 0, " +
            COLUMN_SL_LOWEST_PRICE_DATE + " real default 0," +
            COLUMN_NAME + " text);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(StocksTable.class.getName(), "Upgrading database");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);
        onCreate(database);
    }
}
