package com.techan.activities.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.squareup.otto.Subscribe;
import com.techan.R;
import com.techan.activities.BusService;
import com.techan.activities.HomeActivity;
import com.techan.activities.SettingsActivity;
import com.techan.activities.StockDetailFragmentActivity;
import com.techan.activities.dialogs.AddDialog;
import com.techan.activities.dialogs.DeleteAllStocksDialog;
import com.techan.activities.dialogs.DeletePortfolioDialog;
import com.techan.activities.drawer.IDrawerActivity;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.StockCursorAdapter;
import com.techan.database.CursorUtil;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.ContentValuesFactory;
import com.techan.stockDownload.RefreshTask;
import com.techan.thirdparty.EmptyViewSwipeRefreshLayout;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class StockListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    /////////////////////
    // Construction
    /////////////////////

    public static final Integer LOADER_ID = 1;
    // Maps columns from a cursor to TextViews or ImageViews defined in an XML file.
    private StockCursorAdapter adapter;
    private String portfolioName;
    protected EmptyViewSwipeRefreshLayout swipeView;
    protected View rootView;
    boolean appStartup = false;

    protected ListView listView;
    private View progressView;

    private boolean showCostBasis = false;
    private boolean showStopLoss = false;

    protected IDrawerActivity drawerActivity;

    AtomicInteger refreshActions = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.stock_list, container, false);
        setHasOptionsMenu(true);
        portfolioName = this.getArguments().getString(HomeActivity.PORTFOLIO);
        appStartup = this.getArguments().getBoolean(HomeActivity.APP_START_UP);

        progressView = rootView.findViewById(R.id.stockListProgress);
        swipeView = (EmptyViewSwipeRefreshLayout)rootView.findViewById(R.id.stockListNonEmptySwipeLayout);
        swipeView.setSwipeableChildren(R.id.stockListScrollView, R.id.stockListView);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                startRefresh();
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusService.getInstance().register(this);

        // sets the gap between each item in the list.
        listView = (ListView)rootView.findViewById(R.id.stockListView);
        listView.setDividerHeight(2);

        listView.setEmptyView(rootView.findViewById(R.id.stockListEmptyText));
        listView.setOnItemClickListener(this);

        // Check to see if profile data needs to be loaded into db.
        loadFromProfile();

        // Initialize a load manager and set up a cursor to display the summary for all the stocks.
        fillData();

        // Registers a context menu to be shown for the given view.
        // getListView() gets the activity's list view widget.
        //registerForContextMenu(listView);

        //ProfileManager.forceDelete(this);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);

        SettingsActivity.activateAutoRefresh(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        BusService.getInstance().unregister(this);
    }

    public static class RefreshCompleteEvent {}

    private void startRefresh() {
        (new RefreshTask(getActivity().getContentResolver(), false)).download(getActivity());
    }

    @Subscribe
    public void stockDownloadCompleteComplete(RefreshCompleteEvent event) {
        refreshComplete();
    }

    private void refreshComplete() {
        int refreshesComplete = refreshActions.incrementAndGet();
        if(refreshesComplete < 1) {
            return;
        }

        if(swipeView.isRefreshing()) {
            swipeView.setRefreshing(false);
        }

        if(drawerActivity != null) {
            drawerActivity.resetDrawer();
        }

        swipeView.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.INVISIBLE);
    }

    private void loadFromProfile() {
        ContentResolver cr = getActivity().getContentResolver();
        String[] projection = {StocksTable.COLUMN_SYMBOL};
        Cursor cursor = cr.query(StockContentProvider.CONTENT_URI, projection, null, null, null);
        if(cursor == null) {
            return;
        }

        try {
            if (cursor.getCount() == 0) {
                // Nothing in db. Lets see if we find something in the from the profile manager.
                Collection<SymbolProfile> symbolProfiles = ProfileManager.getSymbols(getActivity());
                if (symbolProfiles.size() != 0) {
                    for (SymbolProfile symbolProfile : symbolProfiles) {
                        ContentValues values = ContentValuesFactory.createValuesForRecovery(symbolProfile);
                        cr.insert(StockContentProvider.CONTENT_URI, values);
                    }
                }
            }
        } finally {
            cursor.close();
        }
    }

    public void fillData() {
        // Initialize loader for this activity. Loads stuff from data base asynchronously.
        Loader loader = getLoaderManager().getLoader(LOADER_ID);
        if(loader != null && loader.isStarted()) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_ID, null, this);

            // Create a cursor that maps each stock symbol to the appropriate field on the UI.
            String[] from = new String[] {StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_PRICE, StocksTable.COLUMN_CHANGE};
            int[] to = new int[] { R.id.listSymbol, R.id.listPrice, R.id.listChange};
            adapter = new StockCursorAdapter(getActivity(), R.layout.stock_row, null, from, to, 0);

            listView.setAdapter(adapter);
        }

        // Update from the network.
        if(appStartup) {
            swipeView.setVisibility(View.INVISIBLE);
            progressView.setVisibility(View.VISIBLE);
            startRefresh();

            //Ensures we don't unnecessarily refresh stock quotes when we come back to this list
            //from a stock detail fragment.
            appStartup = false;
        } else {
            swipeView.setVisibility(View.VISIBLE);
            progressView.setVisibility(View.INVISIBLE);
        }
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
           return CursorUtil.getCursorLoader(getActivity(), portfolioName, projection);
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
        MenuItem costVsCurrentItem = menu.findItem(R.id.showCostBasis);
        MenuItem stopLossItem = menu.findItem(R.id.showStopLoss);
        if(showCostBasis) {
            costVsCurrentItem.setTitle(getString(R.string.showCurrentPrice));
        } else {
            costVsCurrentItem.setTitle(getString(R.string.showCostBasis));
        }

        if (showStopLoss) {
            stopLossItem.setTitle(getString(R.string.showCurrentPrice));
        } else {
            stopLossItem.setTitle("Show Stop Loss");
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
            case R.id.settings:
                settings();
                return true;
            case R.id.showCostBasis:
                if(showCostBasis) {
                    item.setTitle(getString(R.string.showCurrentPrice));
                    showCostBasis = false;
                } else {
                    item.setTitle(getString(R.string.showCostBasis));
                    showCostBasis = true;
                    showStopLoss = false;
                }
                adapter.update(showCostBasis, showStopLoss);
                adapter.notifyDataSetInvalidated();
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.showStopLoss:
                if (showStopLoss) {
                    item.setTitle("Show Current Price");
                    showStopLoss = false;
                } else {
                    item.setTitle("Show Stop Loss");
                    showStopLoss = true;
                    showCostBasis = false;
                }
                adapter.update(showCostBasis, showStopLoss);
                adapter.notifyDataSetInvalidated();;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // the id is the id of the cursor row which is _id from the database by convention.

        // Create an intent. Like an action.
        Intent i = new Intent(getActivity(), StockDetailFragmentActivity.class);
        i.putExtra(HomeActivity.PORTFOLIO, portfolioName);

        // StockContentProvider.CONTENT_ITEM_TYPE is the key for the extra info
        // being placed in the intent.
        Uri stockUri = Uri.parse(StockContentProvider.CONTENT_URI + "/" + id);
        i.putExtra(StockContentProvider.CONTENT_ITEM_TYPE, stockUri);

        // First arg is the intent that defines the activity to be started.
        // Second arg identifies the call. The onActivityResult method is invoked
        // with the same identifier.
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    public void setParentActivity(IDrawerActivity activity) {
        drawerActivity = activity;
    }
}
