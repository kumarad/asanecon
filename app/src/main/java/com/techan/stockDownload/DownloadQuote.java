package com.techan.stockDownload;

import android.net.http.AndroidHttpClient;
import com.techan.custom.Util;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DownloadQuote {

    static String URL_PREFIX = "http://download.finance.yahoo.com/d/quotes.csv?s=";
    static String TAG_PREFIX = "&f=";
    static String SYMBOL_SEPERATOR = "+";

    // If error getting info for stock, will end up with no entry in StockData list for the stock.
    public static Map<String, StockData> download(Set<String> symbols) {
        // Generate url
        String url = URL_PREFIX;
        int symbolCount = 0;
        for(String curSymbol : symbols) {
            if(symbolCount < symbols.size()) {
                url += SYMBOL_SEPERATOR;
            }
            url += curSymbol;
            ++symbolCount;
        }

        url += TAG_PREFIX;

        url += StockData.TAGS;

        Map<String, StockData> stockDataList = new HashMap<>();
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpContext localContext = new BasicHttpContext();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String line = "";
            while((line = reader.readLine()) != null) {
                String[] rowData = line.split(",");

                if(rowData.length < 13) {
                    throw new RuntimeException("Unexpected data from yahoo.");
                }

                String symbol = rowData[0].replace("\"", "");
                StockData stockData = new StockData(symbol);

                stockData.price = Util.parseDouble(rowData[1]);
                stockData.daysLow = Util.parseDouble(rowData[2]);
                stockData.daysHigh = Util.parseDouble(rowData[3]);
                stockData.pe = Util.parseDouble(rowData[4]);
                stockData.peg = Util.parseDouble(rowData[5]);
                stockData.div = Util.parseDouble(rowData[6]);
                stockData.moveAvg50 = Util.parseDouble(rowData[7]);
                stockData.moveAvg200 = Util.parseDouble(rowData[8]);
                stockData.tradingVol = Util.parseDouble(rowData[9]);
                stockData.avgTradingVol = Util.parseDouble(rowData[10]);
                stockData.change = Util.parseDouble(rowData[11]);

                int nameLength = rowData.length - 12;
                StringBuilder nameBuilder = new StringBuilder();
                nameBuilder.append("");
                for(int i = 0; i < nameLength; ++i) {
                    nameBuilder.append(rowData[12+i]);
                }
                stockData.name = nameBuilder.toString();

                stockDataList.put(symbol, stockData);
            }
        } catch(IOException e) {
            getRequest.abort();
            stockDataList.clear();
        } catch(Exception e) {
            e.printStackTrace();
            stockDataList.clear();
        } finally {
            if(client != null) {
                client.close();
            }
        }

        return stockDataList;
    }
}
