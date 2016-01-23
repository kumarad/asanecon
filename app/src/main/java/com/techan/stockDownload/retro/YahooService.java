package com.techan.stockDownload.retro;

import java.util.SortedMap;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface YahooService {
    @GET("/table.csv")
    void getHistory(@Query("s") String symbol,
                    @Query("a") int startMonth,
                    @Query("b") int startDay,
                    @Query("c") int startYear,
                    @Query("d") int endMonth,
                    @Query("e") int endDay,
                    @Query("f") int endYear,
                    Callback<SortedMap<String, Double>> callback);

    @GET("/q/ks")
    void getKeyStats(@Query("s") String symbolPlusKeyStats,
                     Callback<Response> callback);

}
