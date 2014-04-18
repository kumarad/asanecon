package com.techan.activities;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.techan.activities.fragments.StockCostBasisFragment;
import com.techan.activities.fragments.StockTrendFragment;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

import java.util.HashMap;
import java.util.Map;

// Returns a fragment corresponding to one of the sections/tabs/pages.
public class StockPagerAdapter extends FragmentPagerAdapter {
    public static final int FRAGMENT_COUNT = 2;
    public static final String COST_BASIS = "Cost Basis";
    public static final String TREND = "Trends";

    private String[] fragmentTypes = new String[FRAGMENT_COUNT];
    private Map<String, Fragment> fragments = new HashMap<String,Fragment>();
    private Uri stockUri;
    private Context ctx;

    // Only use for create not update.
    private Cursor createCursor;

    public StockPagerAdapter(FragmentManager fm, Uri stockUri, Context ctx) {
        super(fm);
        this.stockUri = stockUri;
        this.ctx = ctx;
        createCursor = ctx.getContentResolver().query(stockUri, null, null, null, null);
        createCursor.moveToFirst();
    }

    @Override
    public Object instantiateItem (ViewGroup container, int position) {
        Fragment fragment = (Fragment)super.instantiateItem(container, position);
        fragments.put(fragmentTypes[position], fragment);
        return fragment;
    }

    protected Fragment createCostBasisFragment() {
        Fragment fragment = new StockCostBasisFragment();
        Bundle args = new Bundle();

        String symbol = createCursor.getString(StocksTable.stockColumns.get(StocksTable.COLUMN_SYMBOL));
        SymbolProfile profile = ProfileManager.getSymbolData(ctx, symbol);
        if(profile.buyPrice != null) {
            args.putDouble(StockCostBasisFragment.COST_VAL, profile.buyPrice);
        }

        if(profile.stockCount != null)
            args.putInt(StockCostBasisFragment.COUNT_VAL, profile.stockCount);

        Double price = Util.roundTwoDecimals(createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE)));
        args.putDouble(StockCostBasisFragment.CUR_PRICE, price);

        if(profile.stopLossPercent != null) {
            args.putInt(StockCostBasisFragment.SL_PERCENT, profile.stopLossPercent);
            double high = createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_SL_HIGEST_PRICE));
            args.putDouble(StockCostBasisFragment.HIGH_PRICE, high);
            args.putString(StockCostBasisFragment.SL_TRACKING_START_DATE, profile.slTrackingStartDate);
        }

        if(profile.targetPrice != null) {
            args.putDouble(StockCostBasisFragment.TARGET_PRICE, profile.targetPrice);
            args.putBoolean(StockCostBasisFragment.TARGET_LESS_THAN_EQUAL, profile.lessThanEqual);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public void updateCostBasisFragment(final Double buyPrice,
                                        final String slTrackingStartDate,
                                        final Integer stockCount,
                                        final Integer slPercent,
                                        final Double targetPrice,
                                        final Boolean lessThanEqual) {
        StockCostBasisFragment fragment = (StockCostBasisFragment)fragments.get(COST_BASIS);

        Cursor cursor = ctx.getContentResolver().query(stockUri, null, null, null, null);
        cursor.moveToFirst();
        Double curPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE));
        Double highPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_SL_HIGEST_PRICE));
        fragment.update(curPrice, buyPrice, slTrackingStartDate, stockCount, slPercent, highPrice, targetPrice, lessThanEqual);
    }

    protected Fragment createTrendFragment() {
        Fragment fragment = new StockTrendFragment();
        Bundle args = new Bundle();

        args.putDouble(StockTrendFragment.CUR_PRICE, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE)));
        args.putDouble(StockTrendFragment.MOV_50_VAL, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_MOV_AVG_50)));
        args.putDouble(StockTrendFragment.MOV_200_VAL, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_MOV_AVG_200)));
        args.putInt(StockTrendFragment.DAY_COUNT, (int) createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_UP_TREND_COUNT)));
        args.putDouble(StockTrendFragment.HIGH_60_DAY, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_60_DAY_HIGH)));
        args.putDouble(StockTrendFragment.LOW_90_DAY, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_90_DAY_LOW)));
        args.putDouble(StockTrendFragment.PEG, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PEG)));
        args.putDouble(StockTrendFragment.VOLUME ,createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_TRADING_VOLUME)));
        args.putDouble(StockTrendFragment.AVG_VOLUME, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_AVG_TRADING_VOLUME)));
        args.putDouble(StockTrendFragment.CHANGE, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_CHANGE)));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return createTrendFragment();
            case 1:
                return createCostBasisFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return (fragmentTypes[position] = TREND);
            case 1:
                return (fragmentTypes[position] = COST_BASIS);
        }

        return null;
    }
}
