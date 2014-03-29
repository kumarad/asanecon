package com.techan.contentProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.techan.database.StocksDatabaseHelper;
import com.techan.database.StocksTable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StockContentProvider  extends ContentProvider {

    private StocksDatabaseHelper database;

    // content://com.techan.contentprovider/stocks
    private static final String AUTHORITY = "com.techan.contentprovider";  // Matches what is placed in manifesto
    private static final String BASE_PATH = "stocks";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    public static final String BASE_URI_STR = "content://" + AUTHORITY + "/";
    public static final String STOCK_URI_STR = "content://" + AUTHORITY + "/" + BASE_PATH + "/";

    // Content resolver - single global instance in your application that provides access to your (and other app) content providers.
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/stocks";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/stock";

    // URI Handling
    // Codes used dor UriMatcher.
    private static final int STOCKS = 10;   // query for all stocks in database
    private static final int STOCKS_ID = 20;    // query for a specific stock in database.
    public static final UriMatcher myUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    // Third arg is the "code" that is returned when a URI is matched against the given components.
    static {
        myUriMatcher.addURI(AUTHORITY, BASE_PATH, STOCKS);
        myUriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", STOCKS_ID);
    }

    @Override
    public boolean onCreate() {
        database = new StocksDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Verify columns requested in projection exist.
        checkColumns(projection);

        // Build the sql query.
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(StocksTable.TABLE_STOCKS);
        int uriType = myUriMatcher.match(uri);
        switch(uriType) {
            case STOCKS :
                break;
            case STOCKS_ID :
                queryBuilder.appendWhere(StocksTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Query the database.
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Watches for changes to the data in the database for the query.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        //TODO: not sure if I should be doing something else here.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = database.getWritableDatabase();
        int uriType = myUriMatcher.match(uri);
        long id;
        switch(uriType) {
            case STOCKS :
                // Insert a stock symbol.
                id = db.insertOrThrow(StocksTable.TABLE_STOCKS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify the observer that a row has changed to allow for updates.
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the URI for the entry just added to the database.
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int uriType = myUriMatcher.match(uri);
        int rowsDeleted;
        switch(uriType) {
            case STOCKS:
                // Delete all stocks that match the selection criteria
                rowsDeleted = db.delete(StocksTable.TABLE_STOCKS, selection, selectionArgs);
                break;
            case STOCKS_ID:
                // Delete all stock with the specified id that match the selection criteria
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    // No selection criteria.
                    rowsDeleted = db.delete(StocksTable.TABLE_STOCKS,
                                            StocksTable.COLUMN_ID + "=" + id,
                                            null);
                } else {
                    rowsDeleted = db.delete(StocksTable.TABLE_STOCKS,
                                            StocksTable.COLUMN_ID + "=" + id + " and " + selection,
                                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify the observer that a row has changed to allow for updates.
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the number of rows that have been deleted.
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = database.getWritableDatabase();
        int uriType = myUriMatcher.match(uri);
        int rowsUpdated;
        switch(uriType) {
            case STOCKS:
                rowsUpdated = db.update(StocksTable.TABLE_STOCKS, values, selection, selectionArgs);
                break;
            case STOCKS_ID:
                String id = uri.getLastPathSegment();
                if(TextUtils.isEmpty(selection)) {
                    // No selection criteria.
                    rowsUpdated = db.update(StocksTable.TABLE_STOCKS,
                                            values,
                                            StocksTable.COLUMN_ID + "=" + id,
                                            null);
                } else {
                    rowsUpdated = db.update(StocksTable.TABLE_STOCKS,
                                            values,
                                            StocksTable.COLUMN_ID + "=" + id + " and " + selection,
                                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Notify the observer that a row has changed to allow for updates.
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the number of rows that have been deleted.
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        if(projection != null) {
            Set<String> requested = new HashSet<String>(Arrays.asList(projection));
            for(String col : requested) {
                if(!StocksTable.stockColumns.containsKey(col)) {
                    throw new IllegalArgumentException("Unknown columns in projection: " + col);
                }

            }
        }
    }
}
