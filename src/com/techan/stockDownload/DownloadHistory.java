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

    public static StockHighs download(String symbol) {
        // Generate url
        String url = URL_PREFIX;
        url += symbol;

        Calendar cal = Calendar.getInstance();
        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        int endMonth = cal.get(Calendar.MONTH); // 0 based month
        int endYear = cal.get(Calendar.YEAR);

        cal.add(Calendar.DAY_OF_MONTH, DAY_COUNT_60 * -1);

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

        double high10Day = 0;
        double high60Day = 0;

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

                double close = Double.parseDouble(rowData[4]);

                if(i <= DAY_COUNT_10 && close > high10Day) {
                    high10Day = close;
                }

                if(i <= DAY_COUNT_60 && close > high60Day) {
                    high60Day = close;
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

        return new StockHighs(symbol, high10Day, high60Day);

    }
}

