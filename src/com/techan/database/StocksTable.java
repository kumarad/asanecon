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
    public static final int COLUMN_SYMBOL_INDEX = 1;
    public static final String COLUMN_PRICE = "price";
    public static final int COLUMN_PRICE_INDEX = 2;

    public static Set<String> stocksColumns = new HashSet<String>();
    static {
        stocksColumns.add(COLUMN_ID);
        stocksColumns.add(COLUMN_SYMBOL);
        stocksColumns.add(COLUMN_PRICE);
    }

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " + TABLE_STOCKS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_SYMBOL + " text unique not null, " +
            COLUMN_PRICE + " real default 0);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(StocksTable.class.getName(), "Upgrading database");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);
        onCreate(database);
    }
}
