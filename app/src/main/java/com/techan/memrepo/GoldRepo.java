package com.techan.memrepo;

public class GoldRepo extends HistoryRepo {
    private static final GoldRepo GOLD_INSTANCE = new GoldRepo();

    public static GoldRepo getRepo() {
        return GOLD_INSTANCE;
    }

    private Double spotPrice = null;

    public Double getSpotPrice() {
        return spotPrice;
    }

    public void setSpotPrice(Double spotPrice) {
        this.spotPrice = spotPrice;
    }

}
