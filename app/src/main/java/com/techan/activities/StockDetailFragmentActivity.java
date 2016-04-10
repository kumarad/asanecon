package com.techan.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.techan.R;
import com.techan.activities.dialogs.BuyDialog;
import com.techan.activities.dialogs.DeleteDialog;
import com.techan.activities.dialogs.PeDialog;
import com.techan.activities.dialogs.StopLossDialogFactory;
import com.techan.activities.dialogs.TargetDialog;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.Constants;
import com.techan.progressbar.SaundProgressBar;
import com.techan.stockDownload.DownloadTrendAndStopLossInfo;

public class StockDetailFragmentActivity extends AppCompatActivity {
    private Uri stockUri;
    private String symbol;

    private View progressView;
    private View contentView;

    private StockPagerAdapter stockPagerAdapter;
    private String portfolioName;

    // Bundle passed into onCreate represents saved state
    // for situations where the activity is being restored
    // from being paused for example.
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stock_detail_viewpager);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        stockUri = (Uri)extras.get(StockContentProvider.CONTENT_ITEM_TYPE);
        portfolioName = extras.getString(HomeActivity.PORTFOLIO);

        progressView = findViewById(R.id.stockDetailProgressView);
        contentView = findViewById(R.id.stockDetailContentView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.stockDetailToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        Cursor stockCursor = getContentResolver().query(stockUri, null, null, null, null);
        if(stockCursor != null) {
            try {
                if (stockCursor.getCount() != 1) {
                    throw new RuntimeException("Was not able to find details for stock.");
                }

                stockCursor.moveToFirst();

                symbol = stockCursor.getString(StocksTable.stockColumns.get(StocksTable.COLUMN_SYMBOL));
                populateGeneralView(stockCursor);
            } finally {
                stockCursor.close();
            }
        } // Nothing we can do here. Should never happen.
    }

    @Override
    public void onResume() {
        super.onResume();
        BusService.getInstance().register(this);
        progressView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.INVISIBLE);

        new DownloadTrendAndStopLossInfo(symbol, this, getContentResolver(), stockUri);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            BusService.getInstance().unregister(this);
        } catch(Exception e) {
            // Could be that we are already unregistered  because of the downloadComplete method
            // being invoked in which case we will get an exception.
        }
    }

    @Subscribe
    public void downloadComplete(DownloadTrendAndStopLossInfo.StopLossHistoryDownloaderComplete event) {
        progressView.setVisibility(View.INVISIBLE);
        contentView.setVisibility(View.VISIBLE);

        stockPagerAdapter = new StockPagerAdapter(getSupportFragmentManager(), stockUri, getApplicationContext(), portfolioName, symbol);
        ViewPager viewPager = (ViewPager) findViewById(R.id.stock_pager_title_strip);
        viewPager.setAdapter(stockPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.stock_pager);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setOffscreenPageLimit(StockPagerAdapter.FRAGMENT_COUNT);


        // Unregister because the event might be published as a part of the stop loss flow and we don't want that
        // to cause us to re initiate the entire page adapter.
        BusService.getInstance().unregister(this);
    }

    void populateGeneralView(Cursor stockCursor) {
        TextView symbolView = (TextView) this.findViewById(R.id.detailNameSymbol);
        String name = stockCursor.getString(StocksTable.stockColumns.get(StocksTable.COLUMN_NAME));
        symbolView.setText(name);
        symbolView.append(" ("+symbol+")");

        TextView priceValView = (TextView) this.findViewById(R.id.detailPriceVal);
        Double price = Util.roundTwoDecimals(stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE)));
        priceValView.setText(Double.toString(price));

        TextView changeView = (TextView) this.findViewById(R.id.detailPriceChange);
        double change = Util.roundTwoDecimals(stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_CHANGE)));
        Util.showChange(changeView, change, price, null);

        double low = stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_DAYS_LOW));
        low = Util.roundTwoDecimals(low);
        double high = stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_DAYS_HIGH));
        high = Util.roundTwoDecimals(high);

        TextView lowView = (TextView) this.findViewById(R.id.detailLow);
        lowView.setText(Double.toString(low));

        double progress = ((price-low)/(high-low)) * 100;
        SaundProgressBar regularProgressBar = (SaundProgressBar) this.findViewById(R.id.lowHighBar);
        regularProgressBar.setProgress((int)progress);
        regularProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progressbar));

        TextView highView = (TextView) this.findViewById(R.id.detailHigh);
        highView.setText(Double.toString(high));


        double pe = stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PE));
        TextView peView = (TextView) this.findViewById(R.id.detailPe);
        if(pe != 0) {
            peView.setText("PE: " + Double.toString(pe));
        } else {
            peView.setText("PE: -");
        }

        double div = stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_DIV));
        TextView divView = (TextView) this.findViewById(R.id.detailDiv);
        if(div != 0) {
            divView.setText("Div: " + Double.toString(div));
        } else {
            divView.setText("Div: -");
        }
    }

    /////////////////////////////////////////////////////////////////////
    // Menu on top right that allows deletion of item.
    /////////////////////////////////////////////////////////////////////

    // Create the menu based on the XML defintion
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailmenu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                DeleteDialog.create(this, stockUri, symbol, portfolioName);
                return true;
            case R.id.set_buy_price:
                BuyDialog.create(this, getLayoutInflater(), symbol, stockPagerAdapter);
                return true;
            case R.id.set_target_price:
                TargetDialog.create(this, symbol, stockPagerAdapter);
                return true;
            case R.id.set_pe_target:
                PeDialog.create(this, symbol, stockPagerAdapter);
                return true;
            case R.id.set_stop_loss:
                StopLossDialogFactory.create(this, symbol, stockUri, stockPagerAdapter);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
