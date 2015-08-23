package com.techan.memrepo;

import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

public class HistoryRepo {
    private static final HistoryRepo GOLD_INSTANCE = new HistoryRepo();
    private static final HistoryRepo SP_INSTANCE = new HistoryRepo();

    public static HistoryRepo getGoldRepo() {
        return GOLD_INSTANCE;
    }

    public static HistoryRepo getSPRepo() {
        return SP_INSTANCE;
    }

    private SortedMap<String, Double> prices = new TreeMap<>();

    public void update(String date, Double price) {
        prices.put(date, price);
    }

    public void setHistory(SortedMap<String, Double> prices) {
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
}
