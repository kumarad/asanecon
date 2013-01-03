package com.techan.stockDownload;

public class StockData {
    public static String SYMBOL = "s";
    public static String LAST_TRADE_PRICE = "l1";
    public static String DAYS_LOW = "g";
    public static String DAYS_HIGH = "h";
    public static String PE = "r";
    public static String PEG = "r5";
    public static String MOV_AVG_50 = "m3";
    public static String MOV_AVG_200 = "m4";
    public static String TRADING_VOLUME = "v";
    public static String NAME = "n";

    public static String TAGS = SYMBOL +
                                LAST_TRADE_PRICE +
                                DAYS_LOW +
                                DAYS_HIGH +
                                PE +
                                PEG +
                                MOV_AVG_50 +
                                MOV_AVG_200 +
                                TRADING_VOLUME +
                                NAME;


    public String symbol;
    public Double price;
    public Double daysLow;
    public Double daysHigh;
    public Double pe;
    public Double peg;
    public Double moveAvg50;
    public Double moveAvg200;
    public Double tradingVol;
    public String name;

    public StockData(String symbol) {
        this.symbol = symbol;
    }
}
