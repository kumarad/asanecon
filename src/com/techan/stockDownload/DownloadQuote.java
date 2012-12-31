package com.techan.stockDownload;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DownloadQuote {

    static String URL_PREFIX = "http://download.finance.yahoo.com/d/quotes.csv?s=";
    static String TAG_PREFIX = "&f=";
    static String SYMBOL_SEPERATOR = "+";

    public static List<StockData> download(List<String> symbols, String... tags) {
        // Generate url
        String url = URL_PREFIX;
        for(int i = 0; i < symbols.size(); ++i) {
            if(i > 0 && i < symbols.size()) {
                url += SYMBOL_SEPERATOR;
            }
            url += symbols.get(i);
        }
        url += TAG_PREFIX;

        for(String tag : tags) {
            url += tag;
        }

        List<StockData> stockDataList = new ArrayList<StockData>();
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpContext localContext = new BasicHttpContext();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");

                String symbol = rowData[0].replace("\"", "");
                StockData stockData = new StockData(symbol);
                stockData.priceStr = rowData[1];
                stockData.price = Double.parseDouble(stockData.priceStr);
                stockData.pe = Double.parseDouble(rowData[2]);

                stockDataList.add(stockData);
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

        return stockDataList;
    }
}
