package com.techan.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class StocksTable {
    // Database table
    public static final String TABLE_STOCKS = "stocks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SYMBOL = "sym";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_LOW = "low";
    public static final String COLUMN_HIGH = "high";
    public static final String COLUMN_PE = "pe";
    public static final String COLUMN_PEG = "peg";
    public static final String COLUMN_MOV_AVG_50 = "movAvg50";
    public static final String COLUMN_MOV_AVG_200 = "movAvg200";
    public static final String COLUMN_TRADING_VOLUME = "tradeVol";
    public static final String COLUMN_NAME = "name";

    public static Set<String> stocksColumns = new HashSet<String>();
    static {
        stocksColumns.add(COLUMN_ID);
        stocksColumns.add(COLUMN_SYMBOL);
        stocksColumns.add(COLUMN_PRICE);
        stocksColumns.add(COLUMN_LOW);
        stocksColumns.add(COLUMN_HIGH);
        stocksColumns.add(COLUMN_PE);
        stocksColumns.add(COLUMN_PEG);
        stocksColumns.add(COLUMN_MOV_AVG_50);
        stocksColumns.add(COLUMN_MOV_AVG_200);
        stocksColumns.add(COLUMN_TRADING_VOLUME);
        stocksColumns.add(COLUMN_NAME);
    }

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " + TABLE_STOCKS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_SYMBOL + " text unique not null, " +
            COLUMN_PRICE + " real default 0, " +
            COLUMN_LOW + " real default 0, " +
            COLUMN_HIGH + " real default 0, " +
            COLUMN_PE + " real default 0, " +
            COLUMN_PEG + " real default 0, " +
            COLUMN_MOV_AVG_50 + " real default 0, " +
            COLUMN_MOV_AVG_200 + " real default 0, " +
            COLUMN_TRADING_VOLUME + " real default 0, " +
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
