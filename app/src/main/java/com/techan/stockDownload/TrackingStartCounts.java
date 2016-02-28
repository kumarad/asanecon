package com.techan.stockDownload;

import com.techan.custom.Util;

import java.util.Calendar;

public class TrackingStartCounts {
    public static final int DAY_COUNT_10 = 10;
    public static final int DAY_COUNT_60 = 60;
    public static final int DAY_COUNT_90 = 90;

    private final Calendar cal90;
    private boolean within90DayRange = false;

    private final Calendar cal60;
    private boolean within60DayRange = false;

    private final Calendar calSl;
    private boolean withinSlRange = false;

    private Calendar startCal;
    private int startCountFor10Day;

    public TrackingStartCounts(Calendar curCal, Calendar calSl) {
        this.calSl = calSl;

        cal90 = (Calendar)curCal.clone();
        cal90.add(Calendar.DAY_OF_MONTH, DAY_COUNT_90 * -1);

        cal60 = (Calendar)curCal.clone();
        cal60.add(Calendar.DAY_OF_MONTH, DAY_COUNT_60 * -1);


        if(calSl != null && calSl.before(cal90)) {
            // Stop loss last update was done longer than 90 days ago.
            startCal = calSl;
        } else {
            // Stop loss last update is within the 90 day period.
            startCal = cal90;
        }
    }

    void initializeCounts(int actualEntries) {
        startCountFor10Day = actualEntries - DAY_COUNT_10;
    }

    public Calendar getStartCal() {
        return startCal;
    }

    public Integer getStartCountFor10Day() {
        return startCountFor10Day;
    }

    public boolean within90DayRange(String curDate) {
        if(within90DayRange) {
            return true;
        } else {
            if(!Util.getCal(Util.getCalStrFromNoTimeStr(curDate)).before(cal90)) {
                within90DayRange = true;
            }

            return within90DayRange;
        }
    }

    public boolean within60DayRange(String curDate) {
        if(within60DayRange) {
            return true;
        } else {
            if(!Util.getCal(Util.getCalStrFromNoTimeStr(curDate)).before(cal60)) {
                within60DayRange = true;
            }

            return within60DayRange;
        }
    }

    public boolean withinSlRange(String curDate) {
        if(withinSlRange) {
            return true;
        } else {
            if(!Util.getCal(Util.getCalStrFromNoTimeStr(curDate)).before(calSl)) {
                withinSlRange = true;
            }

            return withinSlRange;
        }
    }

}
