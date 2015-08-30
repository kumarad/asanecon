package com.techan.stockDownload.retro;

import android.util.Log;

import com.techan.activities.BusService;
import com.techan.custom.Util;
import com.techan.memrepo.HistoryRepo;

import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GoldDownloader extends HistoryDownloader {

    private static final GoldDownloader INSTANCE = new GoldDownloader();

    public static GoldDownloader getInstance() {
        return INSTANCE;
    }

    public static class GoldDownloaderComplete {}

    public void get(String lastUpdateStr) {
        getInternal(null, lastUpdateStr);
    }

    @Override
    public void download(String symbol, Calendar startDate, Calendar endDate) {
        String startDateStr = Util.getDate(startDate);
        String endDateStr = Util.getDate(endDate);

        RetrofitService.quandlRestAdapter.create(QuandlService.class).getGoldPrice(startDateStr, endDateStr, new Callback<GoldData>() {
            @Override
            public void success(GoldData goldData, Response response) {
                HistoryRepo goldRepo = HistoryRepo.getGoldRepo();
                for(String[] cur : goldData.data) {
                    goldRepo.update(cur[0], Double.valueOf(cur[1]));
                }

                BusService.getInstance().post(new GoldDownloaderComplete());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("GOLD", error.getMessage());
                BusService.getInstance().post(new GoldDownloaderComplete());
            }
        });
    }
}
