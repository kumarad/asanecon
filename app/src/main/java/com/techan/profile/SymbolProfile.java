package com.techan.profile;

public class SymbolProfile {
    public String symbol;

    ///////////////////////////////////////////////////////////
    public Double buyPrice = null;
    public Integer stockCount = null;
    public String buyDate = null;

    ///////////////////////////////////////////////////////////
    public Integer stopLossPercent = null;
    public Boolean stopLossTrailing = null;


    ///////////////////////////////////////////////////////////
    public Double targetPrice = null;
    public Boolean lessThanEqual = null;
    public Double peTarget = null;

    // Needed by jackson
    public SymbolProfile() {}

    public SymbolProfile(String symbol) {
        this.symbol = symbol;
    }

    public void setStopLossInfo(Integer stopLossPercent, boolean trailing) {
        this.stopLossPercent = stopLossPercent;
        this.stopLossTrailing = trailing;
    }

    public void clearStopLossInfo() {
        stopLossPercent = null;
        stopLossTrailing = null;
    }
}
