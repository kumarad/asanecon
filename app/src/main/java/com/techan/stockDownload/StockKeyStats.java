package com.techan.stockDownload;

public class StockKeyStats {
    private final long timestamp;
    private final double enterpriseValueMultiple;
    private final double peg;
    private final double bookValue;
    private final double beta;
    private final double currentRatio;
    private final double operatingMargin;
    private final double debtToEquityRatio;
    private final double roa;
    private final double roe;

    public StockKeyStats(long timestamp,
                         double enterpriseValueMultiple,
                         double peg,
                         double bookValue,
                         double beta,
                         double currentRatio,
                         double operatingMargin,
                         double debtToEquityRatio,
                         double roa,
                         double roe) {
        this.timestamp = timestamp;
        this.enterpriseValueMultiple = enterpriseValueMultiple;
        this.peg = peg;
        this.bookValue = bookValue;
        this.beta = beta;
        this.currentRatio = currentRatio;
        this.operatingMargin = operatingMargin;
        this.debtToEquityRatio = debtToEquityRatio;
        this.roa = roa;
        this.roe = roe;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getEnterpriseValueMultiple() {
        return enterpriseValueMultiple;
    }

    public double getPeg() {
        return peg;
    }

    public double getBookValue() {
        return bookValue;
    }

    public double getBeta() {
        return beta;
    }

    public double getCurrentRatio() {
        return currentRatio;
    }

    public double getOperatingMargin() {
        return operatingMargin;
    }

    public double getDebtToEquityRatio() {
        return debtToEquityRatio;
    }

    public double getRoa() {
        return roa;
    }

    public double getRoe() {
        return roe;
    }
}
