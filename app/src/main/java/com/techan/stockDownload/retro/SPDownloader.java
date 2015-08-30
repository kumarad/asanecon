package com.techan.stockDownload.retro;

import com.techan.activities.BusService;
import com.techan.memrepo.HistoryRepo;

public class SPDownloader extends AbstractStockHistoryDownloader {

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
    public HistoryRepo getRepo(String symbol) {
        return HistoryRepo.getSPRepo();
    }

    public void get(String lastUpdatedStr) {
        getInternal("^GSPC", lastUpdatedStr);
    }
}
