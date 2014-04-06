package com.techan.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StocksDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stockstable.db";
    private static final int DATABASE_VERSION = 15;

    public StocksDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called during creation of the database.
    @Override
    public void onCreate(SQLiteDatabase database) {
        StocksTable.onCreate(database);
    }

    // Method is called on upgrade.
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        StocksTable.onUpgrade(database, oldVersion, newVersion);
    }
}
