package com.techan.activities;

import android.app.ActionBar;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import com.techan.R;
import com.techan.activities.dialogs.BuyDialog;
import com.techan.activities.dialogs.DeleteDialog;
import com.techan.activities.dialogs.PeDialog;
import com.techan.activities.dialogs.StopLossDialog;
import com.techan.activities.dialogs.TargetDialog;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.Constants;
import com.techan.progressbar.SaundProgressBar;

public class StockDetailFragmentActivity extends FragmentActivity {
    private Uri stockUri;
    private Cursor stockCursor;
    private String symbol;

    private ViewPager viewPager;
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

        stockCursor = getContentResolver().query(stockUri, null, null, null, null);

        if(stockCursor.getCount() != 1) {
            throw new RuntimeException("Was not able to find details for stock.");
        }

        stockCursor.moveToFirst();

        TextView symbolView = (TextView) this.findViewById(R.id.lastUpdate);
        String lastUpdateStr = stockCursor.getString(StocksTable.stockColumns.get(StocksTable.COLUMN_LAST_UPDATE));
        symbolView.setText(lastUpdateStr);

        symbol = stockCursor.getString(StocksTable.stockColumns.get(StocksTable.COLUMN_SYMBOL));
        populateGeneralView();

        stockPagerAdapter = new StockPagerAdapter(getSupportFragmentManager(), stockUri, getApplicationContext());
        viewPager = (ViewPager)findViewById(R.id.stock_pager);
        viewPager.setAdapter(stockPagerAdapter);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.stock_pager_title_strip);
        pagerTabStrip.setTabIndicatorColor(Color.parseColor(Constants.ANDROID_BLUE));

        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(null);
        }
    }

    void populateGeneralView() {
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
                StopLossDialog.create(this, symbol, stockUri, stockPagerAdapter);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
