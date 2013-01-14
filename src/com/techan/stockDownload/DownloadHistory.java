package com.techan.stockDownload;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class DownloadHistory {
    static String URL_PREFIX = "http://ichart.yahoo.com/table.csv?s=";
    static String DAILY_INTERVAL = "&g=d";
    static int DAY_COUNT_10 = 10;
    static int DAY_COUNT_60 = 60;
    static int DAY_COUNT_90 = 90;

    private static String generateUrl(StockData data, Calendar cal, int count) {
        // Generate url
        String url = URL_PREFIX;
        url += data.symbol;

        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        int endMonth = cal.get(Calendar.MONTH); // 0 based month
        int endYear = cal.get(Calendar.YEAR);

        cal.add(Calendar.DAY_OF_MONTH, count * -1);

        int startDay = cal.get(Calendar.DAY_OF_MONTH);
        int startMonth = cal.get(Calendar.MONTH);
        int startYear = cal.get(Calendar.YEAR);

        endDay = endDay -1;
        url += "&a=" + startMonth;
        url += "&b=" + startDay;
        url += "&c=" + startYear;
        url += "&d=" + endMonth;
        url += "&e=" + endDay;
        url += "&f=" + endYear;

        url += DAILY_INTERVAL;

        return url;
    }

    public static void download(StockData data, Calendar cal) {
        String url = generateUrl(data, cal, DAY_COUNT_90);

        boolean trackUpTrend = true;
        double upTrendDayCount = 0;
        double prevDayClose = Double.MAX_VALUE;
        double high60Day = 0;
        double low90Day = Double.MAX_VALUE;

        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpContext localContext = new BasicHttpContext();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            int i = 0;
            String line = reader.readLine();    // First line defines columns.
            while((line = reader.readLine()) != null) {
                i++;
                String[] rowData = line.split(",");

                double high = Double.parseDouble(rowData[2]);
                if(i <= DAY_COUNT_60 && high > high60Day) {
                    high60Day = high;
                }

                double low = Double.parseDouble(rowData[3]);
                if(i <= DAY_COUNT_90 && low < low90Day ) {
                    low90Day = low;
                }

                double close = Double.parseDouble(rowData[4]);
                if(i <= DAY_COUNT_10 && trackUpTrend) {
                    if(close < prevDayClose) {
                        upTrendDayCount++;
                        prevDayClose = close;
                    } else {
                        trackUpTrend = false;
                    }
                }
            }
        } catch(IOException e) {
            getRequest.abort();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(client != null) {
                client.close();
            }
        }

        data.stockTrends = new StockTrends(upTrendDayCount, high60Day, low90Day);
    }


    public static double downloadForUpTrend(StockData data, Calendar cal) {
        String url = generateUrl(data, cal, 10);

        double upTrendDayCount = 0;
        double prevDayClose = Double.MAX_VALUE;

        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpContext localContext = new BasicHttpContext();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = reader.readLine();    // First line defines columns.
            while((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");

                double close = Double.parseDouble(rowData[4]);
                if(close < prevDayClose) {
                    upTrendDayCount++;
                    prevDayClose = close;
                } else {
                    return upTrendDayCount;
                }
            }
        } catch(IOException e) {
            getRequest.abort();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(client != null) {
                client.close();
            }
        }

        return upTrendDayCount;
    }

}

