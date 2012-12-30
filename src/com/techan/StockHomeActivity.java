package com.techan;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.techan.contentProvider.StockContentProvider;
import com.techan.database.StocksTable;
import com.techan.stockDownload.QuoteDownloadTask;

/**
 * Cursor - access to the result of a database query.
 * Loaders - loads data in an activity or fragment asynchronously,
 * LoaderManager - manages one or more loader instances within an activity or fragment.
 * There is only one LoaderManager per activity/fragment.
 */
public class StockHomeActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /////////////////////
    // Construction
    /////////////////////

    // Maps columns from a cursor to TextViews or ImageViews defined in an XML file.
    private StockCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // If nothing to display, use a text view to print out the fact that there is nothing in db.
        // If there is stuff in db, use a list view to display the stocks.
        // Using androids infrastructure that does the handling of empty vs not empty using the
        // android ids in the layout xml.
        setContentView(R.layout.stock_list);

        // sets the gap between each item in the list.
        this.getListView().setDividerHeight(2);

        // Initialize a load manager and set up a cursor to display the summary for all the stocks.
        fillData();

        // Registers a context menu to be shown for the given view.
        // getListView() gets the activity's list view widget.
        registerForContextMenu(getListView());
    }

    private void fillData() {
        // Initialize loader for this activity. Loads stuff from data base asynchronously.
        getLoaderManager().initLoader(0, null, this);

        // Create a cursor that maps each stock symbol to the appropriate field on the UI.
        String[] from = new String[] {StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_PRICE};
        int[] to = new int[] { R.id.symbol, R.id.price};
        adapter = new StockCursorAdapter(this, R.layout.stock_row, null, from, to, 0);

        setListAdapter(adapter);
    }

    /////////////////////
    // LoadManager Stuff
    /////////////////////
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Creates a new loader after initLoader() call.
        // CursorLoader is used to load stuff from the database.

        // Cursor query must have an integer column "_id" for the CursorAdapter to work.
        // The cursors id will be _id.
        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_PRICE};

        // CONTENT_URI = "content://com.techan.contentprovider/stocks"
        return new CursorLoader(this, StockContentProvider.CONTENT_URI, projection,
                                                     null, null, null);
   }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);

        // Update from the network.
        (new QuoteDownloadTask(this.getContentResolver(), data)).execute();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    /////////////////////////////////////////////////////////////////////
    // Menu on top right that allows insertion of items in list/database.
    /////////////////////////////////////////////////////////////////////
    private static final int ACTIVITY_CREATE = 0;

    // Create the menu based on the XML defintion
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.listmenu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                insertStock();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertStock() {
        Intent i = new Intent(this, StockAddActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

}
