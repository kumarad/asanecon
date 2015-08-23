package com.techan.stockDownload.retro;

import android.util.Log;

import com.techan.activities.BusService;
import com.techan.memrepo.HistoryRepo;

import java.util.Calendar;
import java.util.SortedMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SPDownloader extends HistoryDownloader {

    private static final SPDownloader INSTANCE = new SPDownloader();

    public static SPDownloader getInstance() {
        return INSTANCE;
    }

    public static class SPDownloaderComplete {}

    @Override
    public void download(Calendar startDate, Calendar endDate) {
        int endDay = endDate.get(Calendar.DAY_OF_MONTH);
        int endMonth = endDate.get(Calendar.MONTH);
        int endYear = endDate.get(Calendar.YEAR);

        int startDay = startDate.get(Calendar.DAY_OF_MONTH);
        int startMonth = startDate.get(Calendar.MONTH);
        int startYear = startDate.get(Calendar.YEAR);

        endDay = endDay -1;

        RetrofitService.yahooRestAdapter.create(YahooService.class).getHistory("^GSPC",
                startMonth,
                startDay,
                startYear,
                endMonth,
                endDay,
                endYear,
                new Callback<SortedMap<String, Double>>() {
                    @Override
                    public void success(SortedMap<String, Double> prices, Response response) {
                        HistoryRepo spRepo = HistoryRepo.getSPRepo();
                        spRepo.setHistory(prices);

                        BusService.getInstance().post(new SPDownloaderComplete());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("SP", error.getMessage());
                        BusService.getInstance().post(new SPDownloaderComplete());
                    }
                });

    }
}
