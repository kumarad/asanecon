package com.techan.stockDownload.retro;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface QuandlService {
    @GET("/datasets/BUNDESBANK/BBK01_WT5511.json")
    void getGoldPrice(@Query("trim_start") String startDateStr, @Query("time_end") String endDateStr, Callback<GoldData> callback);
}
