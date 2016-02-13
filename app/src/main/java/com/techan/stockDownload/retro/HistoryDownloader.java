package com.techan.stockDownload.retro;

import com.techan.custom.Util;

import java.util.Calendar;

public abstract class HistoryDownloader {

    protected void getInternal(String symbol, String lastUpdateDateStr, int days) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        int daysSinceLastUpdate = days;
        if(lastUpdateDateStr != null) {
            Calendar lastUpdateDate = Util.getCalForDateOnly(lastUpdateDateStr);
            daysSinceLastUpdate = Util.dateDiff(lastUpdateDate, startDate);
            if (daysSinceLastUpdate > days) {
                daysSinceLastUpdate = days;
            }
        }

        startDate.add(Calendar.DAY_OF_MONTH, -daysSinceLastUpdate);

        download(symbol, startDate, endDate);
    }

    public abstract void download(String symbol, Calendar startDate, Calendar endDate);
}
