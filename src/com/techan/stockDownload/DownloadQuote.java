package com.techan.stockDownload;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DownloadQuote {

    static String URL_PREFIX = "http://download.finance.yahoo.com/d/quotes.csv?s=";
    static String TAG_PREFIX = "&f=";

    public static StockData download(String symbol, String... tags) {
        String url = URL_PREFIX + symbol + TAG_PREFIX;
        for(String tag : tags) {
            url += tag;
        }

        StockData stockData = new StockData(symbol);
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpContext localContext = new BasicHttpContext();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String result = "";
            String line = "";
            while((line = reader.readLine()) != null) {
                result += line + ",";
                String[] rowData = result.split(",");

                stockData.name = rowData[0].replace("\"", "");
                stockData.priceStr = rowData[1];
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


        //TODO Should probably have value saved off in data base for cases
        //TODO where the network failed us.
        return stockData;
    }
}
