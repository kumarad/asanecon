package com.techan.stockDownload.retro;

import android.util.Log;

import com.techan.activities.BusService;
import com.techan.custom.Util;
import com.techan.memrepo.GoldRepo;

import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GoldDownloader {

    private static final int DAYS_TO_GO_BACK = 30;

    public static class GoldDownloaderComplete {}

    public static void get(String lastUpdateDateStr) {
        Calendar curDate = Calendar.getInstance();
        String endDateStr = Util.getDate(curDate);

        int daysSinceLastUpdate = DAYS_TO_GO_BACK;
        if(lastUpdateDateStr != null) {
            Calendar lastUpdateDate = Util.getCalForDateOnly(lastUpdateDateStr);
            daysSinceLastUpdate = Util.dateDiff(lastUpdateDate, curDate);
            if (daysSinceLastUpdate > DAYS_TO_GO_BACK) {
                daysSinceLastUpdate = DAYS_TO_GO_BACK;
            }
        }

        curDate.add(Calendar.DAY_OF_MONTH, -daysSinceLastUpdate);
        String startDateStr = Util.getDate(curDate);

        RetrofitService.quandlRestAdapter.create(QuandlService.class).getGoldPrice(startDateStr, endDateStr, new Callback<GoldData>() {
            @Override
            public void success(GoldData goldData, Response response) {
                GoldRepo goldRepo = GoldRepo.get();
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
