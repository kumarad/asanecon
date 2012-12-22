package com.example.techan.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StocksTable {
    // Database table
    public static final String TABLE_STOCKS = "stocks";
    public static final String COLUMN_ID = "_id";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table " + TABLE_STOCKS + "(" +
            COLUMN_ID + " integer primary key autoincrement);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(StocksTable.class.getName(), "Upgrading database");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);
        onCreate(database);
    }
}
