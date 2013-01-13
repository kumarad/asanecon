package com.techan.stockDownload;

public class StockTrends {
    String symbol;
    double upTrendDayCount;
    double high60Day;
    double low90Day;

    public StockTrends(String symbol, double upTrendDayCount, double high60Day, double low90Day) {
        this.symbol = symbol;
        this.upTrendDayCount = upTrendDayCount;
        this.high60Day = high60Day;
        this.low90Day = low90Day;
    }
}
