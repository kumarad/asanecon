package com.techan.activities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.ViewGroup;

import com.techan.R;
import com.techan.activities.fragments.StockCostBasisFragment;
import com.techan.activities.fragments.StockKeyStatsFragment;
import com.techan.activities.fragments.StockTrendFragment;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

import java.util.HashMap;
import java.util.Map;

// Returns a fragment corresponding to one of the sections/tabs/pages.
public class StockPagerAdapter extends FragmentPagerAdapter {
    public static final int FRAGMENT_COUNT = 3;
    public static final String COST_BASIS = "Cost Basis";
    public static final String TREND = "Trends";
    public static final String KEY_STATS = "Key Statistics";

    private String[] fragmentTypes = new String[FRAGMENT_COUNT];
    private CharSequence[] icons = new CharSequence[FRAGMENT_COUNT];

    private Map<String, Fragment> fragments = new HashMap<>();
    private Uri stockUri;
    private Context ctx;
    private String portfolioName;
    private String symbol;

    public StockPagerAdapter(FragmentManager fm, Uri stockUri, Context ctx, String portfolioName, String symbol) {
        super(fm);
        this.stockUri = stockUri;
        this.ctx = ctx;
        this.portfolioName = portfolioName;
        this.symbol = symbol;

        fragmentTypes[0] = TREND;
        fragmentTypes[1] = COST_BASIS;
        fragmentTypes[2] = KEY_STATS;

        setIcon(0, R.drawable.ic_trending_up_white_24dp);
        setIcon(1, R.drawable.ic_account_balance_white_24dp);
        setIcon(2, R.drawable.ic_equalizer_white_24dp);
    }

    private void setIcon(int index, int id) {
        Drawable image = ContextCompat.getDrawable(ctx, id);
        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        icons[index] = sb;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment)super.instantiateItem(container, position);
        fragments.put(fragmentTypes[position], fragment);
        return fragment;
    }

    protected Fragment createCostBasisFragment() {
        StockCostBasisFragment fragment = new StockCostBasisFragment();
        fragment.setPortfolioName(portfolioName);
        fragment.setStockPagerAdapter(this);
        Bundle args = new Bundle();

        Cursor createCursor = ctx.getContentResolver().query(stockUri, null, null, null, null);
        if(createCursor != null) {
            try {
                createCursor.moveToFirst();

                String symbol = createCursor.getString(StocksTable.stockColumns.get(StocksTable.COLUMN_SYMBOL));
                fragment.setSymbol(symbol);
                args.putString(StockCostBasisFragment.SYMBOL, symbol);

                SymbolProfile profile = ProfileManager.getSymbolData(ctx, symbol);
                if (profile.buyPrice != null) {
                    args.putDouble(StockCostBasisFragment.COST_VAL, profile.buyPrice);
                }

                if (profile.stockCount != null)
                    args.putInt(StockCostBasisFragment.COUNT_VAL, profile.stockCount);

                Double price = Util.roundTwoDecimals(createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE)));
                args.putDouble(StockCostBasisFragment.CUR_PRICE, price);

                if (profile.stopLossPercent != null) {
                    args.putInt(StockCostBasisFragment.SL_PERCENT, profile.stopLossPercent);
                    double high = createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_SL_HIGEST_PRICE));
                    args.putDouble(StockCostBasisFragment.HIGH_PRICE, high);
                    args.putString(StockCostBasisFragment.SL_TRACKING_START_DATE, profile.slTrackingStartDate);
                }

                if (profile.targetPrice != null && profile.lessThanEqual != null) {
                    args.putDouble(StockCostBasisFragment.TARGET_PRICE, profile.targetPrice);
                    args.putBoolean(StockCostBasisFragment.TARGET_LESS_THAN_EQUAL, profile.lessThanEqual);
                }

                if (profile.peTarget != null) {
                    args.putDouble(StockCostBasisFragment.TARGET_PE, profile.peTarget);
                    args.putDouble(StockCostBasisFragment.CUR_PE, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PE)));
                }

                fragment.setArguments(args);
            } finally {
                createCursor.close();
            }
        }

        return fragment;
    }

    public void updateCostBasisFragment(final SymbolProfile profile) {
        StockCostBasisFragment fragment = (StockCostBasisFragment)fragments.get(COST_BASIS);

        Cursor cursor = ctx.getContentResolver().query(stockUri, null, null, null, null);
        if(cursor != null) {
            try {
                cursor.moveToFirst();
                Double curPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE));
                Double highPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_SL_HIGEST_PRICE));
                Double curPE = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PE));
                fragment.update(curPrice, highPrice, curPE, profile);
            } finally {
                cursor.close();
            }
        }
    }

    protected Fragment createTrendFragment() {
        Fragment fragment = new StockTrendFragment();
        Bundle args = new Bundle();

        Cursor createCursor = ctx.getContentResolver().query(stockUri, null, null, null, null);
        if(createCursor != null) {
            try {
                createCursor.moveToFirst();

                args.putDouble(StockTrendFragment.CUR_PRICE, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE)));
                args.putDouble(StockTrendFragment.MOV_50_VAL, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_MOV_AVG_50)));
                args.putDouble(StockTrendFragment.MOV_200_VAL, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_MOV_AVG_200)));
                args.putInt(StockTrendFragment.DAY_COUNT, (int) createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_UP_TREND_COUNT)));
                args.putDouble(StockTrendFragment.HIGH_60_DAY, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_60_DAY_HIGH)));
                args.putDouble(StockTrendFragment.LOW_90_DAY, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_90_DAY_LOW)));
                args.putDouble(StockTrendFragment.PEG, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PEG)));
                args.putDouble(StockTrendFragment.VOLUME, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_TRADING_VOLUME)));
                args.putDouble(StockTrendFragment.AVG_VOLUME, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_AVG_TRADING_VOLUME)));
                args.putDouble(StockTrendFragment.CHANGE, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_CHANGE)));
            } finally {
                createCursor.close();
            }
        }

        fragment.setArguments(args);
        return fragment;
    }

    protected Fragment createKeyStatsFragment() {
        StockKeyStatsFragment fragment = new StockKeyStatsFragment();

        Cursor createCursor = ctx.getContentResolver().query(stockUri, null, null, null, null);
        if(createCursor != null) {
            try {
                createCursor.moveToFirst();

                fragment.setSymbolAndPrice(symbol, createCursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE)));
            } finally {
                createCursor.close();
            }
        }

        return fragment;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return createTrendFragment();
            case 1:
                return createCostBasisFragment();
            case 2:
                return createKeyStatsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return icons[position];
    }
}
