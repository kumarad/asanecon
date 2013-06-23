package com.techan.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.techan.activities.fragments.StockPeFragment;
import com.techan.activities.fragments.StockTrendFragment;
import com.techan.activities.fragments.StockVolumeFragment;
import com.techan.custom.Util;
import com.techan.database.StocksTable;

// Returns a fragment corresponding to one of the sections/tabs/pages.
public class StockPagerAdapter extends FragmentPagerAdapter {
    public static final int FRAGMENT_COUNT = 3;

    private Cursor stockCursor;

    public StockPagerAdapter(FragmentManager fm, Cursor stockCursor) {
        super(fm);
        this.stockCursor = stockCursor;
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
                return createPeFragment();
            case 1:
                return createVolFragment();
            case 2:
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
                return "PE";
            case 1:
                return "VOL";
            case 2:
                return "TR";
        }

        return null;
    }
}
