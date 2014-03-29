package com.techan.stockDownload;

public class StockTrends {
    public double upTrendDayCount;
    public double high60Day;
    public double low90Day;

    public double historicalHigh;
    public double historicalLow;
    public String historicalLowDate;

    public StockTrends(double upTrendDayCount, double high60Day, double low90Day) {
        this.upTrendDayCount = upTrendDayCount;
        this.high60Day = high60Day;
        this.low90Day = low90Day;
    }

    public void setHistoricalInfo(double high, double low, String lowDate) {
        this.historicalHigh = high;
        this.historicalLow = low;
        this.historicalLowDate = lowDate;
    }
}
