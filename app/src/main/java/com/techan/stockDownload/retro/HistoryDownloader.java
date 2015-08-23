package com.techan.stockDownload.retro;

import com.techan.custom.Util;

import java.util.Calendar;

public abstract class HistoryDownloader {
    private static final int DAYS_TO_GO_BACK = 360;

    public void get(String lastUpdateDateStr) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        int daysSinceLastUpdate = DAYS_TO_GO_BACK;
        if(lastUpdateDateStr != null) {
            Calendar lastUpdateDate = Util.getCalForDateOnly(lastUpdateDateStr);
            daysSinceLastUpdate = Util.dateDiff(lastUpdateDate, startDate);
            if (daysSinceLastUpdate > DAYS_TO_GO_BACK) {
                daysSinceLastUpdate = DAYS_TO_GO_BACK;
            }
        }

        startDate.add(Calendar.DAY_OF_MONTH, -daysSinceLastUpdate);

        download(startDate, endDate);
    }

    public abstract void download(Calendar startDate, Calendar endDate);
}
