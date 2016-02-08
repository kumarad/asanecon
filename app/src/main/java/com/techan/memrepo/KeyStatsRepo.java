package com.techan.memrepo;

import android.util.LruCache;

import com.techan.stockDownload.StockKeyStats;

public class KeyStatsRepo {
    private static final KeyStatsRepo REPO = new KeyStatsRepo();
    private static final LruCache<String,StockKeyStats> cache = new LruCache<>(50);

    public static KeyStatsRepo getRepo() {
        return REPO;
    }

    private KeyStatsRepo() {}

    public void put(String symbol, StockKeyStats keyStats) {
        cache.put(symbol, keyStats);
    }

    public StockKeyStats get(String symbol) {
        return cache.get(symbol);
    }
}
