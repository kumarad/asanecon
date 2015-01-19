package com.techan.activities.fragments;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.techan.R;
import com.techan.activities.HomeActivity;
import com.techan.activities.SettingsActivity;
import com.techan.activities.StockDetailFragmentActivity;
import com.techan.activities.dialogs.AddDialog;
import com.techan.activities.dialogs.DeleteAllStocksDialog;
import com.techan.activities.dialogs.DeletePortfolioDialog;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.StockCursorAdapter;
import com.techan.database.StocksTable;
import com.techan.profile.Portfolio;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.ContentValuesFactory;
import com.techan.stockDownload.RefreshTask;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class StockListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /////////////////////
    // Construction
    /////////////////////

    public static final Integer LOADER_ID = 1;
    // Maps columns from a cursor to TextViews or ImageViews defined in an XML file.
    private StockCursorAdapter adapter;
    private String portfolioName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        portfolioName = this.getArguments().getString(HomeActivity.PORTFOLIO);
        return inflater.inflate(R.layout.stock_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // sets the gap between each item in the list.
        this.getListView().setDividerHeight(2);

        // Check to see if profile data needs to be loaded into db.
        loadFromProfile();

        // Initialize a load manager and set up a cursor to display the summary for all the stocks.
        fillData();

        // Registers a context menu to be shown for the given view.
        // getListView() gets the activity's list view widget.
        registerForContextMenu(getListView());

        //ProfileManager.forceDelete(this);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);

        SettingsActivity.activateAutoRefresh(getActivity());
    }

    private void loadFromProfile() {
        ContentResolver cr = getActivity().getContentResolver();
        String[] projection = {StocksTable.COLUMN_SYMBOL};
        Cursor cursor = cr.query(StockContentProvider.CONTENT_URI, projection, null, null, null);
        if(cursor.getCount() == 0) {
            // Nothing in db. Lets see if we find something in the from the profile manager.
            Collection<SymbolProfile> symbolProfiles = ProfileManager.getSymbols(getActivity());
            if(symbolProfiles.size() != 0) {
                for(SymbolProfile symbolProfile : symbolProfiles) {
                    ContentValues values = ContentValuesFactory.createValuesForRecovery(symbolProfile);
                    cr.insert(StockContentProvider.CONTENT_URI, values);
                }
            }
        }
    }

    public void fillData() {
        // Initialize loader for this activity. Loads stuff from data base asynchronously.
        getLoaderManager().initLoader(LOADER_ID, null, this);

        // Create a cursor that maps each stock symbol to the appropriate field on the UI.
        String[] from = new String[] {StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_PRICE, StocksTable.COLUMN_CHANGE};
        int[] to = new int[] { R.id.listSymbol, R.id.listPrice, R.id.listChange};
        adapter = new StockCursorAdapter(getActivity(), R.layout.stock_row, null, from, to, 0);

        setListAdapter(adapter);

        // Update from the network.
        (new RefreshTask(getActivity(), getActivity().getContentResolver(), false)).download();
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
        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_PRICE, StocksTable.COLUMN_CHANGE};

        if(portfolioName.equals(HomeActivity.ALL_STOCKS)) {
            // CONTENT_URI = "content://com.techan.contentprovider/stocks"
            return new CursorLoader(getActivity(), StockContentProvider.CONTENT_URI, projection,
                                    null, null, null);
        } else {
            Set<String> symbols = Collections.emptySet();
            Map<String, Portfolio> portfolios = ProfileManager.getPortfolios(getActivity());
            for(Map.Entry<String, Portfolio> curPortfolioEntry : portfolios.entrySet()) {
                if(curPortfolioEntry.getKey().equals(portfolioName)) {
                    symbols = curPortfolioEntry.getValue().getSymbols();
                    break;
                }
            }

            int argcount = symbols.size(); // number of IN arguments
            StringBuilder inList = new StringBuilder(argcount * 2);
            for (int i = 0; i < argcount; i++) {
                if (i > 0) inList.append(",");
                inList.append("?");
            }

            // CONTENT_URI = "content://com.techan.contentprovider/stocks"
            return new CursorLoader(getActivity(),
                    StockContentProvider.CONTENT_URI,
                    projection,
                    "sym IN (" + inList.toString() + ")",
                    symbols.toArray(new String[0]),
                    null);
        }
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.listmenu, menu);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean showCostBasis = prefs.getBoolean(SettingsActivity.SHOW_COST_BASIS, false);
        MenuItem costVsCurrentItem = menu.findItem(R.id.showCostBasis);
        if(showCostBasis) {
            costVsCurrentItem.setTitle(getString(R.string.showCurrentPrice));
        } else {
            costVsCurrentItem.setTitle(getString(R.string.showCostBasis));
        }

        MenuItem deleteAllStocksItem = menu.findItem(R.id.deleteAllStocks);
        MenuItem deletePortfolioItem = menu.findItem(R.id.deletePortfolio);
        if(portfolioName.equals(HomeActivity.ALL_STOCKS)) {
            deleteAllStocksItem.setVisible(true);
            deletePortfolioItem.setVisible(false);
        } else {
            deleteAllStocksItem.setVisible(false);
            deletePortfolioItem.setVisible(true);
        }
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert:
                AddDialog.create(this, portfolioName, getLoaderManager());
                return true;
            case R.id.refresh:
                (new RefreshTask(getActivity(), getActivity().getContentResolver(), false)).download();
                return true;
            case R.id.settings:
                settings();
                return true;
            case R.id.showCostBasis:
                boolean showCostBasis = SettingsActivity.getCostBasisSetting(getActivity());
                if(showCostBasis) {
                    item.setTitle(getString(R.string.showCurrentPrice));
                    showCostBasis = false;
                } else {
                    item.setTitle(getString(R.string.showCostBasis));
                    showCostBasis = true;
                }
                SettingsActivity.setCostBasisSetting(getActivity(), showCostBasis);
                adapter.notifyDataSetInvalidated();
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.deletePortfolio:
                DeletePortfolioDialog.create(getActivity(), portfolioName);
                return true;
            case R.id.deleteAllStocks:
                DeleteAllStocksDialog.create(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void settings() {
        Intent i = new Intent(getActivity(), SettingsActivity.class);
        startActivity(i);
    }

    /////////////////////////////////////////////////////////////////////
    // List Item Click Response
    /////////////////////////////////////////////////////////////////////
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // the id is the id of the cursor row which is _id from the database by convention.
        super.onListItemClick(l,v, position, id);

        // Create an intent. Like an action.
        Intent i = new Intent(getActivity(), StockDetailFragmentActivity.class);


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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // A negative value (RESULT_OK) is invoked which causes startActivity to get called.
        // Nothing special to do here.
        super.onActivityResult(requestCode, resultCode, intent);
    }

}
