package com.techan.stockDownload.retro;

import com.techan.stockDownload.StockData;
import com.techan.stockDownload.StockDayPriceInfo;

import java.util.Map;
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
                    @Query("g") String dailyInterval,
                    Callback<SortedMap<String, StockDayPriceInfo>> callback);

    @GET("/q/ks")
    void getKeyStats(@Query("s") String symbolPlusKeyStats,
                     Callback<Response> callback);

    @GET("/d/quotes.csv")
    void downloadQuotes(@Query("s") String symbols,
                        @Query("f") String tags,
                        Callback<Map<String, StockData>> callback);

}
