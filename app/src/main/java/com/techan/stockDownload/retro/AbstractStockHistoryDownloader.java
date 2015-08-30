package com.techan.stockDownload.retro;

import android.util.Log;

import com.techan.memrepo.HistoryRepo;

import java.util.Calendar;
import java.util.SortedMap;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public abstract class AbstractStockHistoryDownloader extends HistoryDownloader {

    public abstract void done();
    public abstract HistoryRepo getRepo(String symbol);

    @Override
    public void download(final String symbol, Calendar startDate, Calendar endDate) {
        int endDay = endDate.get(Calendar.DAY_OF_MONTH);
        int endMonth = endDate.get(Calendar.MONTH);
        int endYear = endDate.get(Calendar.YEAR);

        int startDay = startDate.get(Calendar.DAY_OF_MONTH);
        int startMonth = startDate.get(Calendar.MONTH);
        int startYear = startDate.get(Calendar.YEAR);

        if(endDay == startDay && endMonth == startMonth && endYear == startYear) {
            done();
            return;
        }

        endDay = endDay -1;

        RetrofitService.yahooRestAdapter.create(YahooService.class).getHistory(symbol,
                startMonth,
                startDay,
                startYear,
                endMonth,
                endDay,
                endYear,
                new Callback<SortedMap<String, Double>>() {
                    @Override
                    public void success(SortedMap<String, Double> prices, Response response) {
                        getRepo(symbol).setHistory(prices);


                        done();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Stock history", error.getMessage());
                        done();
                    }
                });


    }
}
