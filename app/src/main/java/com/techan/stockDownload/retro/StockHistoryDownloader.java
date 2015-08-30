package com.techan.stockDownload.retro;

import com.techan.activities.BusService;
import com.techan.memrepo.HistoryRepo;

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
    public HistoryRepo getRepo(String symbol) {
        return HistoryRepo.getStockRepo(symbol);
    }

    public void get(String symbol, String lastUpdatedStr) {
        getInternal(symbol, lastUpdatedStr);
    }
}
