package com.techan.stockDownload;

import com.techan.activities.BusService;
import com.techan.stockDownload.retro.RetrofitService;
import com.techan.stockDownload.retro.YahooService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DownloadQuote {

    static String SYMBOL_SEPERATOR = "+";

    public static class DownloadQuoteComplete {
        private final Map<String, StockData> stockData;

        public DownloadQuoteComplete(Map<String, StockData> stockData) {
            this.stockData = stockData;
        }

        public Map<String, StockData> getStockData() {
            return stockData;
        }
    }

    // If error getting info for stock, will end up with no entry in StockData list for the stock.
    public static void download(Set<String> symbols) {
        String symbolsString = "";
        int symbolCount = 0;
        for(String curSymbol : symbols) {
            symbolsString += curSymbol;
            if(symbolCount < symbols.size()) {
                symbolsString += SYMBOL_SEPERATOR;
            }
            ++symbolCount;
        }

        RetrofitService.yahooQuoteAdapter.create(YahooService.class).downloadQuotes(symbolsString, StockData.TAGS, new Callback<Map<String, StockData>>() {
            @Override
            public void success(Map<String, StockData> stockData, Response response) {
                BusService.getInstance().post(new DownloadQuoteComplete(stockData));
            }

            @Override
            public void failure(RetrofitError error) {
                BusService.getInstance().post(new DownloadQuoteComplete(new HashMap<String, StockData>()));
            }
        });

    }
}
