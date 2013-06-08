package com.techan.activities;

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
import com.techan.activities.dialogs.DeleteDialog;
import com.techan.activities.dialogs.PeDialog;
import com.techan.activities.dialogs.StopLossDialog;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;

public class StockDetailFragmentActivity extends FragmentActivity {
    private Uri stockUri;
    private Cursor stockCursor;
    private String symbol;

    private ViewPager viewPager;
    private StockPagerAdapter stockPagerAdapter;

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

        stockCursor = getContentResolver().query(stockUri, null, null, null, null);

        if(stockCursor.getCount() != 1) {
            throw new RuntimeException("Was not able to find details for stock.");
        }

        stockCursor.moveToFirst();

        TextView symbolView = (TextView) this.findViewById(R.id.lastUpdate);
        String lastUpdateStr = stockCursor.getString(15);
        symbolView.setText(lastUpdateStr);

        populateGeneralView();

        stockPagerAdapter = new StockPagerAdapter(getSupportFragmentManager(), stockCursor);
        viewPager = (ViewPager)findViewById(R.id.stock_pager);
        viewPager.setAdapter(stockPagerAdapter);

        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.stock_pager_title_strip);
        pagerTabStrip.setTabIndicatorColor(Color.parseColor("#33b5e5"));
    }

    void populateGeneralView() {
        TextView symbolView = (TextView) this.findViewById(R.id.detailNameSymbol);
        symbol = stockCursor.getString(1);
        String name = stockCursor.getString(16);
        symbolView.setText(name);
        symbolView.append(" ("+symbol+")");

        TextView priceValView = (TextView) this.findViewById(R.id.detailPriceVal);
        priceValView.setText(Util.parseDouble(stockCursor, 2));

        TextView changeView = (TextView) this.findViewById(R.id.detailPriceChange);
        double change = stockCursor.getDouble(11);
        if(change < 0) {
            changeView.setTextColor(Color.RED);
        } else if(change > 0) {
            changeView.setTextColor(Color.GREEN);
        }
        changeView.setText("(");
        changeView.append(Double.toString(change));
        changeView.append(")");

        TextView lowView = (TextView) this.findViewById(R.id.detailLow);
        lowView.setText("Low: ");
        TextView lowValView = (TextView) this.findViewById(R.id.detailLowVal);
        lowValView.setText(Util.parseDouble(stockCursor, 3));

        TextView highView = (TextView) this.findViewById(R.id.detailHigh);
        highView.setText("High: ");
        TextView highValView = (TextView) this.findViewById(R.id.detailHighVal);
        highValView.setText(Util.parseDouble(stockCursor, 4));
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
                DeleteDialog.create(this, stockUri, symbol);
                return true;
            case R.id.set_pe_target:
                PeDialog.create(this);
                return true;
            case R.id.set_stop_loss:
                StopLossDialog.create(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
