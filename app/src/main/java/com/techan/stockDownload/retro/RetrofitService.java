package com.techan.stockDownload.retro;

import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class RetrofitService {
    public static final RestAdapter quandlRestAdapter = new RestAdapter.Builder()
                                                                .setEndpoint("http://www.quandl.com/api/v1")
                                                                .setConverter(new JacksonConverter())
                                                                .setClient(new OkClient(createHttpClient()))
                                                                .build();

    public static final RestAdapter yahooHistoryRestAdapter = new RestAdapter.Builder()
            .setEndpoint("http://ichart.yahoo.com")
            .setConverter(new YahooHistoryCSVConverter())
            .build();

    public static final RestAdapter yahooScraper = new RestAdapter.Builder()
            .setEndpoint("http://finance.yahoo.com").build();

    public static final RestAdapter yahooQuoteAdapter = new RestAdapter.Builder()
            .setEndpoint("http://download.finance.yahoo.com")
            .setConverter(new YahooQuoteCSVConverter())
            .build();

    private static OkHttpClient createHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setFollowSslRedirects(true);
        return client;
    }
}
