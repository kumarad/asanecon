package com.techan.memrepo;

import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

public class GoldRepo {
    private static final GoldRepo INSTANCE = new GoldRepo();

    public static GoldRepo get() {
        return INSTANCE;
    }

    private final SortedMap<String, Double> prices = new TreeMap<>();

    public void clear() {
        prices.clear();
    }

    public void update(String date, Double price) {
        prices.put(date, price);
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
