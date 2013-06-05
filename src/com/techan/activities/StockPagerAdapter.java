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

        args.putString(StockPeFragment.PE_VAL, Util.parseDouble(stockCursor, 5));
        args.putString(StockPeFragment.PEG_VAL, Util.parseDouble(stockCursor, 6));
        fragment.setArguments(args);

        return fragment;
    }

    protected Fragment createVolFragment() {
        Fragment fragment = new StockVolumeFragment();
        Bundle args = new Bundle();

        double volDouble = stockCursor.getDouble(9);
        String volString;
        if(volDouble != 0) {
            volString = Long.toString((long) volDouble);
        } else {
            volString = "N/A";
        }

        double avgVolDouble = stockCursor.getDouble(10);
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

        args.putString(StockTrendFragment.MOV_50_VAL, Util.parseDouble(stockCursor, 7));
        args.putString(StockTrendFragment.MOV_200_VAL, Util.parseDouble(stockCursor, 8));
        args.putInt(StockTrendFragment.DAY_COUNT, (int)stockCursor.getDouble(12));
        args.putString(StockTrendFragment.HIGH_60_DAY, Util.parseDouble(stockCursor, 13));
        args.putString(StockTrendFragment.LOW_90_DAY, Util.parseDouble(stockCursor, 14));

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
