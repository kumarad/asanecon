package com.techan.stockDownload;

public class StockData {
    public static String NAME = "s";
    public static String LAST_TRADE_PRICE = "l1";
    public static String PE = "r";

    public String symbol;
    public String priceStr;
    public Double price;
    public Double pe;

    public StockData(String symbol) {
        this.symbol = symbol;
    }
}
