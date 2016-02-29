package com.techan.stockDownload.retro;

import com.techan.activities.BusService;
import com.techan.memrepo.HistoryRepo;
import com.techan.stockDownload.StockDayPriceInfo;

import java.util.SortedMap;

public class StockHistoryDownloader extends AbstractStockHistoryDownloader {
    private static final StockHistoryDownloader INSTANCE = new StockHistoryDownloader();

    public static StockHistoryDownloader getInstance() {
        return INSTANCE;
    }

    public static class StockHistoryDownloaderComplete {}

    @Override
    public void done() {
        BusService.getInstance().post(new StockHistoryDownloaderComplete());
    }

    @Override
    public void handleHistory(String symbol, SortedMap<String, StockDayPriceInfo> prices) {
        HistoryRepo.getStockRepo(symbol).setHistory(prices);
    }

    public void get(String symbol, String lastUpdatedStr) {
        getInternal(symbol, lastUpdatedStr, 360);
    }
}
