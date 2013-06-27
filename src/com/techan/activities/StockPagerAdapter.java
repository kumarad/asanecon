package com.techan.activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techan.R;
import com.techan.activities.fragments.StockCostBasisFragment;
import com.techan.activities.fragments.StockPeFragment;
import com.techan.activities.fragments.StockTrendFragment;
import com.techan.activities.fragments.StockVolumeFragment;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

import java.util.HashMap;
import java.util.Map;

// Returns a fragment corresponding to one of the sections/tabs/pages.
public class StockPagerAdapter extends FragmentPagerAdapter {
    public static final int FRAGMENT_COUNT = 4;
    public static final String COST_BASIS = "Cost Basis";
    public static final int COST_BASIS_INDEX = 0;
    public static final String PE = "PE";
    public static final int PE_INDEX = 1;
    public static final String VOL = "Volume";
    public static final int VOL_INDEX = 2;
    public static final String TREND = "Trends";
    public static final int TREND_INDEX = 3;

    private String[] fragmentTypes = new String[FRAGMENT_COUNT];
    private Map<String, Fragment> fragments = new HashMap<String,Fragment>();
    private Cursor stockCursor;
    private Context ctx;

    public StockPagerAdapter(FragmentManager fm, Cursor stockCursor, Context ctx) {
        super(fm);
        this.stockCursor = stockCursor;
        this.ctx = ctx;
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

        String symbol = stockCursor.getString(StocksTable.stockColumns.get(StocksTable.COLUMN_SYMBOL));
        SymbolProfile profile = ProfileManager.getSymbolData(ctx, symbol);
        if(profile.buyPrice != null)
            args.putDouble(StockCostBasisFragment.COST_VAL, profile.buyPrice);
        if(profile.stockCount != null)
            args.putInt(StockCostBasisFragment.COUNT_VAL, profile.stockCount);


        Double price = Util.roundTwoDecimals(stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE)));
        args.putDouble(StockCostBasisFragment.CUR_PRICE, price);

        fragment.setArguments(args);
        return fragment;
    }

    public void updateCostBasisFragment(final Double buyPrice, final Integer stockCount) {
        StockCostBasisFragment fragment = (StockCostBasisFragment)fragments.get(COST_BASIS);
        fragment.update(buyPrice, stockCount);
    }

    protected Fragment createPeFragment() {
        Fragment fragment = new StockPeFragment();
        Bundle args = new Bundle();

        args.putString(StockPeFragment.PE_VAL, Util.parseDouble(stockCursor, StocksTable.stockColumns.get(StocksTable.COLUMN_PE)));
        args.putString(StockPeFragment.PEG_VAL, Util.parseDouble(stockCursor, StocksTable.stockColumns.get(StocksTable.COLUMN_PEG)));
        fragment.setArguments(args);

        return fragment;
    }

    protected Fragment createVolFragment() {
        Fragment fragment = new StockVolumeFragment();
        Bundle args = new Bundle();

        double volDouble = stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_TRADING_VOLUME));
        String volString;
        if(volDouble != 0) {
            volString = Long.toString((long) volDouble);
        } else {
            volString = "N/A";
        }

        double avgVolDouble = stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_AVG_TRADING_VOLUME));
        String avgVolString;
        if(avgVolDouble != 0) {
            avgVolString = Long.toString((long) avgVolDouble);
        } else {
            avgVolString ="N/A";
        }

        args.putString(StockVolumeFragment.VOLUME, volString);
        args.putString(StockVolumeFragment.AVG_VOLUME, avgVolString);

        fragment.setArguments(args);
        return fragment;
    }

    protected Fragment createTrendFragment() {
        Fragment fragment = new StockTrendFragment();
        Bundle args = new Bundle();

        args.putString(StockTrendFragment.MOV_50_VAL, Util.parseDouble(stockCursor, StocksTable.stockColumns.get(StocksTable.COLUMN_MOV_AVG_50)));
        args.putString(StockTrendFragment.MOV_200_VAL, Util.parseDouble(stockCursor, StocksTable.stockColumns.get(StocksTable.COLUMN_MOV_AVG_200)));
        args.putInt(StockTrendFragment.DAY_COUNT, (int)stockCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_UP_TREND_COUNT)));
        args.putString(StockTrendFragment.HIGH_60_DAY, Util.parseDouble(stockCursor, StocksTable.stockColumns.get(StocksTable.COLUMN_60_DAY_HIGH)));
        args.putString(StockTrendFragment.LOW_90_DAY, Util.parseDouble(stockCursor, StocksTable.stockColumns.get(StocksTable.COLUMN_90_DAY_LOW)));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return createCostBasisFragment();
            case 1:
                return createPeFragment();
            case 2:
                return createVolFragment();
            case 3:
                return createTrendFragment();
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
                return (fragmentTypes[position] = COST_BASIS);
            case 1:
                return (fragmentTypes[position] = PE);
            case 2:
                return (fragmentTypes[position] = VOL);
            case 3:
                return (fragmentTypes[position] = TREND);
        }

        return null;
    }
}
