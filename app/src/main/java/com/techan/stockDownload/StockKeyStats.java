package com.techan.stockDownload;

public class StockKeyStats {
    private final long timestamp;
    private final Double enterpriseValueMultiple;
    private final Double peg;
    private final Double bookValue;
    private final Double beta;
    private final Double currentRatio;
    private final Double operatingMargin;
    private final Double debtToEquityRatio;
    private final Double roa;
    private final Double roe;

    public StockKeyStats(long timestamp,
                         Double enterpriseValueMultiple,
                         Double peg,
                         Double bookValue,
                         Double beta,
                         Double currentRatio,
                         Double operatingMargin,
                         Double debtToEquityRatio,
                         Double roa,
                         Double roe) {
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

    public Double getEnterpriseValueMultiple() {
        return enterpriseValueMultiple;
    }

    public Double getPeg() {
        return peg;
    }

    public Double getBookValue() {
        return bookValue;
    }

    public Double getBeta() {
        return beta;
    }

    public Double getCurrentRatio() {
        return currentRatio;
    }

    public Double getOperatingMargin() {
        return operatingMargin;
    }

    public Double getDebtToEquityRatio() {
        return debtToEquityRatio;
    }

    public Double getRoa() {
        return roa;
    }

    public Double getRoe() {
        return roe;
    }
}
