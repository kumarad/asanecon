package com.techan.profile;

import java.util.Set;

public class SymbolProfile {
    public String symbol;

    ///////////////////////////////////////////////////////////
    public Double buyPrice = null;
    public Integer stockCount = null;

    ///////////////////////////////////////////////////////////
    public Integer stopLossPercent = null;
    public Boolean stopLossTrailing = null;
    public String slTrackingStartDate = null;

    ///////////////////////////////////////////////////////////
    public Double targetPrice = null;
    public Boolean lessThanEqual = null;
    public Double peTarget = null;

    // Needed by jackson
    public SymbolProfile() {}

    public SymbolProfile(String symbol) {
        this.symbol = symbol;
    }

    public void setStopLossInfo(Integer stopLossPercent, boolean trailing, String curDate) {
        this.stopLossPercent = stopLossPercent;
        this.stopLossTrailing = trailing;
        this.slTrackingStartDate = curDate;
    }

    public void clearStopLossInfo() {
        stopLossPercent = null;
        stopLossTrailing = null;
        slTrackingStartDate = null;
    }
}
