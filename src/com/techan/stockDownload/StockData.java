package com.techan.stockDownload;

public class StockData {
    public static String NAME = "s";
    public static String LAST_TRADE_PRICE = "l1";

    public String symbol;
    public String name;
    public String priceStr;
    //public Double price;                 stockData.price = Double.parseDouble();

    public StockData(String symbol) {
        this.symbol = symbol;
    }
}
