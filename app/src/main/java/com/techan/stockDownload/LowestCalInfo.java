package com.techan.stockDownload;

import java.util.Calendar;

public class LowestCalInfo {
    public Calendar lowestCal;
    public Integer slDayCountLessThan90;
    public LowestCalInfo(Calendar lowestCalInfo, Integer slDayCount) {
        this.lowestCal = lowestCalInfo;
        this.slDayCountLessThan90 = slDayCount;
    }

}
