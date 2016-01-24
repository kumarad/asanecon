package com.techan.stockDownload;

import com.techan.activities.BusService;
import com.techan.stockDownload.retro.RetrofitService;
import com.techan.stockDownload.retro.YahooService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class KeyStatsDownloader {

    private static String LABEL_REGEX = "<td class=\"yfnc_tablehead1\".+?>(.+?)<.+?</td>";
    private static String VALUE_REGEX = "<td class=\"yfnc_tabledata1\">(.+?)</td>";

    private static Pattern LABEL_PATTERN = Pattern.compile(LABEL_REGEX);
    private static Pattern VALUE_PATTERN = Pattern.compile(VALUE_REGEX);

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

                        Matcher labelMatcher = LABEL_PATTERN.matcher(result);
                        Matcher valueMatcher = VALUE_PATTERN.matcher(result);
                        Map<String, String> keyStatsMap = new HashMap<>();
                        while(labelMatcher.find() && valueMatcher.find()) {
                            String label = labelMatcher.group(1);
                            String value = valueMatcher.group(1);
                            keyStatsMap.put(label, value);
                        }

                        done();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        done();
                    }
                });
    }

}
