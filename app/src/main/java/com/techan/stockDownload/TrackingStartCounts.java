package com.techan.stockDownload;

import java.util.Calendar;

public class TrackingStartCounts {
    private final Calendar startCal;
    private final Integer startCountFor90Day;
    private final Integer startCountFor60Day;
    private final Integer startCountFor10Day;
    private final Integer startCountForSl;

    public TrackingStartCounts(Calendar startCal, Integer startCountFor90Day, Integer startCountForSl) {
        this.startCal = startCal;
        this.startCountFor90Day = startCountFor90Day;
        this.startCountFor60Day = startCountFor90Day + 30;
        this.startCountFor10Day = startCountFor90Day + 80;
        this.startCountForSl = startCountForSl;
    }

    public Calendar getStartCal() {
        return startCal;
    }

    public Integer getStartCountFor90Day() {
        return startCountFor90Day;
    }

    public Integer getStartCountFor60Day() {
        return startCountFor60Day;
    }

    public Integer getStartCountFor10Day() {
        return startCountFor10Day;
    }

    public Integer getStartCountForSl() {
        return startCountForSl;
    }
}
