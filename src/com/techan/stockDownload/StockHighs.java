package com.techan.stockDownload;

public class StockHighs {
    String symbol;
    double high10Day;
    double high60Day;

    public StockHighs(String symbol, double high10Day, double high60Day) {
        this.symbol = symbol;
        this.high10Day = high10Day;
        this.high60Day = high60Day;
    }
}
