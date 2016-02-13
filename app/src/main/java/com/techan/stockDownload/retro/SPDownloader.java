package com.techan.stockDownload.retro;

import com.techan.activities.BusService;
import com.techan.memrepo.HistoryRepo;
import com.techan.stockDownload.StockDayPriceInfo;

import java.util.SortedMap;

public class SPDownloader extends AbstractStockHistoryDownloader {

    private static final String SYMBOL = "^GSPC";

    private static final SPDownloader INSTANCE = new SPDownloader();

    public static SPDownloader getInstance() {
        return INSTANCE;
    }

    public static class SPDownloaderComplete {}

    @Override
    public void done() {
        BusService.getInstance().post(new SPDownloaderComplete());
    }

    @Override
    public void handleHistory(String symbol, SortedMap<String, StockDayPriceInfo> prices) {
        HistoryRepo.getSPRepo().setHistory(prices);
    }

    public void get(String lastUpdatedStr) {
        getInternal(SYMBOL, lastUpdatedStr, 360);
    }
}
