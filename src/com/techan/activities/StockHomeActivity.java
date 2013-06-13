package com.techan.activities;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.techan.R;
import com.techan.activities.dialogs.AddDialog;
import com.techan.custom.StockCursorAdapter;
import com.techan.contentProvider.StockContentProvider;
import com.techan.database.StocksTable;
import com.techan.stockDownload.RefreshAllTask;

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

//        ProfileManager.forceDelete(getApplicationContext());
    }

    private void fillData() {
        // Initialize loader for this activity. Loads stuff from data base asynchronously.
        getLoaderManager().initLoader(0, null, this);

        // Create a cursor that maps each stock symbol to the appropriate field on the UI.
        String[] from = new String[] {StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_PRICE};
        int[] to = new int[] { R.id.symbol, R.id.price};
        adapter = new StockCursorAdapter(this, R.layout.stock_row, null, from, to, 0);

        setListAdapter(adapter);

        // Update from the network.
        (new RefreshAllTask(this.getContentResolver())).download();
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    /////////////////////////////////////////////////////////////////////
    // Menu on top right that allows insertion of items in list/database.
    /////////////////////////////////////////////////////////////////////
    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

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
                AddDialog.create(this);
                return true;
            case R.id.refresh:
                (new RefreshAllTask(this.getContentResolver())).download();
                return true;
            case R.id.settings:
                settings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void settings() {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    /////////////////////////////////////////////////////////////////////
    // List Item Click Response
    /////////////////////////////////////////////////////////////////////
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // the id is the id of the cursor row which is _id from the database by convention.
        super.onListItemClick(l,v, position, id);

        // Create an intent. Like an action.
//        Intent i = new Intent(this, StockDetailActivity.class);
        Intent i = new Intent(this, StockDetailFragmentActivity.class);


        // StockContentProvider.CONTENT_ITEM_TYPE is the key for the extra info
        // being placed in the intent.
        Uri stockUri = Uri.parse(StockContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(StockContentProvider.CONTENT_ITEM_TYPE, stockUri);

        // First arg is the intent that defines the activity to be started.
        // Second arg identifies the call. The onActivityResult method is invoked
        // with the same identifier.
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // A negative value (RESULT_OK) is invoked which causes startActivity to get called.
        // Nothing special to do here.
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
