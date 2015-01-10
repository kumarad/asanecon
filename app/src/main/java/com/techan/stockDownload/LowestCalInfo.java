package com.techan.stockDownload;

import java.util.Calendar;

public class LowestCalInfo {
    public Calendar lowestCal;
    public Integer curDateMinusSlDateWhenSlAfter90Days;
    public LowestCalInfo(Calendar lowestCalInfo, Integer slDayCount) {
        this.lowestCal = lowestCalInfo;
        this.curDateMinusSlDateWhenSlAfter90Days = slDayCount;
    }

}
