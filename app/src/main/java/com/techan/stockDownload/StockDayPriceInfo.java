package com.techan.stockDownload;

public class StockDayPriceInfo {
    private final Double closingPrice;
    private final Double high;
    private final Double low;

    public StockDayPriceInfo(Double closingPrice, Double high, Double low) {
        this.closingPrice = closingPrice;
        this.high = high;
        this.low = low;
    }

    public Double getClosingPrice() {
        return closingPrice;
    }

    public Double getHigh() {
        return high;
    }

    public Double getLow() {
        return low;
    }

}
