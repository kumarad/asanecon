package com.techan.stockDownload;

public class HistoryInfo {
    // Used internally by DownloadTrendAndStopLossInfo.
    public double prevDayClose = Double.MAX_VALUE;
    public boolean trackUpTrend = true;

    // Trends
    public double upTrendDayCount = 0;
    public double high60Day = 0;
    public double low90Day = Double.MAX_VALUE;

    // Stop Loss Info
    public double historicalHigh = 0;
    public double historicalLow = Double.MAX_VALUE;
    public String historicalLowDate =  null;

    public HistoryInfo(Double historicalHigh, Double historicalLow) {
        if(historicalHigh != null) {
            this.historicalHigh = historicalHigh;
        }

        if(historicalLow != null) {
            this.historicalLow = historicalLow;
        }
    }

    public HistoryInfo() {}
}
