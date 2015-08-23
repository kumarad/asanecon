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

    public static final RestAdapter yahooRestAdapter = new RestAdapter.Builder()
            .setEndpoint("http://ichart.yahoo.com")
            .setConverter(new YahooCSVConverter())
            .build();

    private static OkHttpClient createHttpClient() {
        OkHttpClient client = new OkHttpClient();
        client.setFollowSslRedirects(true);
        return client;
    }
}
