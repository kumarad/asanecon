package com.techan.memrepo;

import android.util.LruCache;

import com.techan.custom.Util;
import com.techan.stockDownload.StockDayPriceInfo;

import java.util.Calendar;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

public class HistoryRepo {
    private static final HistoryRepo SP_INSTANCE = new HistoryRepo();
    private static final LruCache<String, HistoryRepo> STOCK_CACHE = new LruCache(20);

    public static HistoryRepo getSPRepo() {
        return SP_INSTANCE;
    }

    public static HistoryRepo getStockRepo(String symbol) {
        HistoryRepo historyRepo =  STOCK_CACHE.get(symbol);
        if(historyRepo == null) {
            historyRepo = new HistoryRepo();
            STOCK_CACHE.put(symbol, historyRepo);
        }

        return historyRepo;
    }

    private SortedMap<String, Double> prices = new TreeMap<>();
    private String lastUpdateStr = null;

    public void setHistory(SortedMap<String, StockDayPriceInfo> prices) {
        lastUpdateStr = Util.getDate(Calendar.getInstance());
        for(Map.Entry<String, StockDayPriceInfo> cur : prices.entrySet()) {
            this.prices.put(cur.getKey(), cur.getValue().getClosingPrice());
        }
    }

    public void setHistorySimple(Map<String, Double> prices) {
        lastUpdateStr = Util.getDate(Calendar.getInstance());
        this.prices.putAll(prices);
    }

    //for(Map.Entry<String, Double> entry : goldRepo.getPrices())
    // returns the list in ascending order.
    public SortedMap<String, Double> getPrices() {
        return prices;
    }

    public String getLatestPriceDate() {
        try {
            return prices.lastKey();
        } catch(NoSuchElementException e) {
            return null;
        }
    }

    public Double getLatestPrice() {
        String latestKey = getLatestPriceDate();
        if(latestKey != null) {
            return prices.get(latestKey);
        }

        return null;
    }

    public boolean alreadyUpdatedToday() {
        return lastUpdateStr != null && Util.getDate(Calendar.getInstance()).equals(lastUpdateStr);
    }
}
