package com.techan.stockDownload;

import com.techan.activities.BusService;
import com.techan.stockDownload.retro.RetrofitService;
import com.techan.stockDownload.retro.YahooService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KeyStatsDownloader {

    public static class KeyStatsDownloaderComplete {}

    public static void done() {
        BusService.getInstance().post(new KeyStatsDownloaderComplete());
    }

    public static void download(String symbol) {
        RetrofitService.yahooScraper.create(YahooService.class).
                getKeyStats(String.format("%s+Key+Statistics", symbol), new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        //Try to get response body
                        BufferedReader reader;
                        StringBuilder sb = new StringBuilder();
                        try {

                            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));

                            String line;

                            try {
                                while ((line = reader.readLine()) != null) {
                                    sb.append(line);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        String result = sb.toString();
                        System.out.println(result);
                        done();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        done();
                    }
                });
    }

}
