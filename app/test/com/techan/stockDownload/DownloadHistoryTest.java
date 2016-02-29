package com.techan.stockDownload;

import com.techan.custom.Util;
import com.xtremelabs.robolectric.RobolectricTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class DownloadHistoryTest {

    public static class DownloadHistoryTester extends DownloadHistory {
        protected static String generateUrlForDaysTest(String symbol, Calendar curCal, int count) {
            return generateUrlForDays(symbol, curCal, count);
        }

        protected static String generateUrlForRangeTest(String symbol, Calendar curCal, Calendar lastCal) {
            return null;
        }

        protected static TrackingStartCounts lowestDateTest(Calendar curCal, String lastSLUpdate) {
            return lowestDate(curCal, lastSLUpdate);
        }

        protected static void handleTrendsTest(HistoryInfo historyInfo, String[] rowData, int count) {
            handleTrends(historyInfo, rowData, count);
        }

        protected static void handleStopLossTest(HistoryInfo historyInfo, String[] rowData, int count, Integer slDayCountLessThan90) {
            handleStopLoss(historyInfo, rowData, count, slDayCountLessThan90);
        }
    }

    public static String testUrl(String symbol, String startMonth, String startDay, String startYear, String endMonth, String endDay, String endYear) {
        return "http://ichart.yahoo.com/table.csv?s="+symbol+""
                +"&a="+Integer.toString(Integer.parseInt(startMonth) - 1)
                +"&b="+startDay
                +"&c="+startYear
                +"&d="+Integer.toString(Integer.parseInt(endMonth)-1)
                +"&e="+Integer.toString(Integer.parseInt(endDay)-1)
                +"&f="+endYear
                +"&g=d";
    }

    @Test
    public void testGenerateUrlForDays() {
        String symbol = "test";
        String startYear = "2013";
        String startMonth = "11";
        String startDay = "2";
        String startCalStr = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";
        Calendar startCal = Util.getCal(startCalStr);

        String endYear = "2013";
        String endMonth = "12";
        String endDay = "4";
        String endCalStr = endYear + "-" + endMonth + "-" + endDay + " 00:00:00";
        Calendar endCal = Util.getCal(endCalStr);

        String expectedUrl = testUrl(symbol, startMonth, startDay, startYear, endMonth, endDay, endYear);

        int diff = Util.dateDiff(startCal, endCal);

        String url = DownloadHistoryTester.generateUrlForDaysTest(symbol, endCal, diff);
        assertThat(expectedUrl, equalTo(url));
    }

    @Test
    public void testGenerateUrlForRange() {
        String symbol = "test";
        String startYear = "2013";
        String startMonth = "11";
        String startDay = "2";
        String startCalStr = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";
        Calendar startCal = Util.getCal(startCalStr);

        String endYear = "2013";
        String endMonth = "12";
        String endDay = "4";
        String endCalStr = endYear + "-" + endMonth + "-" + endDay + " 00:00:00";
        Calendar endCal = Util.getCal(endCalStr);

        String expectedUrl = testUrl(symbol, startMonth, startDay, startYear, endMonth, endDay, endYear);

        String url = DownloadHistoryTester.generateUrlForRangeTest(symbol, endCal, startCal);
        assertThat(expectedUrl, equalTo(url));
    }

    @Test
    public void testLowestDateWhenLowerThan90() {
        String startYear = "2012";
        String startMonth = "11";
        String startDay = "2";
        String startCalStr = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";
        Calendar startCal = Util.getCal(startCalStr);

        String endYear = "2013";
        String endMonth = "12";
        String endDay = "4";
        String endCalStr = endYear + "-" + endMonth + "-" + endDay + " 00:00:00";
        Calendar endCal = Util.getCal(endCalStr);

        TrackingStartCounts info = DownloadHistoryTester.lowestDateTest(endCal, startCalStr);
        assertThat(info.lowestCal, equalTo(startCal));
        assertThat(info.curDateMinusSlDate, equalTo(null));
    }

    @Test
    public void testLowestDateWhenHigherThan90() {
        String startYear = "2013";
        String startMonth = "12";
        String startDay = "1";
        String startCalStr = startYear + "-" + startMonth + "-" + startDay + " 00:00:00";

        String endYear = "2013";
        String endMonth = "12";
        String endDay = "4";
        String endCalStr = endYear + "-" + endMonth + "-" + endDay + " 00:00:00";
        Calendar endCal = Util.getCal(endCalStr);

        TrackingStartCounts info = DownloadHistoryTester.lowestDateTest(endCal, startCalStr);
        endCal.add(Calendar.DAY_OF_MONTH, DownloadHistory.DAY_COUNT_90 * -1);
        assertThat(info.lowestCal, equalTo(endCal));
        assertThat(info.curDateMinusSlDate, equalTo(3));
    }

    @Test
    public void testYahooHistoricalUrl() throws Exception {
        String symbol = "IBM";
        Calendar curCal = Calendar.getInstance();
        String url = DownloadHistoryTester.generateUrlForDaysTest(symbol, curCal, 2);

        URL yahoo = new URL(url);
        URLConnection yc = yahoo.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yc.getInputStream()));
        try {

            // Data is returned with latest date on top.
            String line = in.readLine();
            String[] rowData = line.split(",");
            assertThat(rowData[DownloadHistory.DATE_INDEX], equalTo("Date"));
            assertThat(rowData[DownloadHistory.DAY_HIGH_INDEX], equalTo("High"));
            assertThat(rowData[DownloadHistory.DAY_LOW_INDEX], equalTo("Low"));
            assertThat(rowData[DownloadHistory.DAY_CLOSE_INDEX], equalTo("Close"));

            line = in.readLine();
            rowData = line.split(",");
            Calendar top = Util.getCal(Util.getCalStrFromNoTimeStr(rowData[0]));
            curCal.add(Calendar.DAY_OF_MONTH, -1);
            assertThat(curCal.get(Calendar.DAY_OF_MONTH), equalTo(top.get(Calendar.DAY_OF_MONTH)));
            assertThat(curCal.get(Calendar.MONTH), equalTo(top.get(Calendar.MONTH)));
            assertThat(curCal.get(Calendar.YEAR), equalTo(top.get(Calendar.YEAR)));

            line = in.readLine();
            rowData = line.split(",");
            Calendar bottom = Util.getCal(rowData[0] + " 00:00:00");

            assertThat(top.after(bottom), equalTo(true));

        } catch(IOException e) {
            throw e;
        } catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void test60DayHigh() {
        HistoryInfo historyInfo = new HistoryInfo();
        String highs[][] = new String[100][5];
        for(int i = 0; i < 100; i++) {
            if(i == 5) {
                highs[i][DownloadHistory.DAY_CLOSE_INDEX] = "700";
            }
            if(i == 10) {
                highs[i][DownloadHistory.DAY_CLOSE_INDEX] = "800.89";
            } else if(i == 70) {
                highs[i][DownloadHistory.DAY_CLOSE_INDEX] ="1000.11";
            } else if(i == 99) {
                highs[i][DownloadHistory.DAY_CLOSE_INDEX] ="2000.11";
            } else {
                highs[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(i);
            }
        }

        for(int i = 0; i < 100; i++) {
            DownloadHistoryTester.handleTrendsTest(historyInfo, highs[i], i);
        }

        assertThat(historyInfo.high60Day, equalTo(800.89));
    }


    @Test
    public void test90DayHigh() {
        HistoryInfo historyInfo = new HistoryInfo();
        String lows[][] = new String[100][5];
        for(int i = 0; i < 100; i++) {
            if(i == 10) {
                lows[i][DownloadHistory.DAY_CLOSE_INDEX] = "-2000.00";
            } else if(i == 70) {
                lows[i][DownloadHistory.DAY_CLOSE_INDEX] ="-3000.00";
            } else if(i == 99) {
                lows[i][DownloadHistory.DAY_CLOSE_INDEX] ="-5000.00";
            } else {
                lows[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(i);
            }
        }

        for(int i = 0; i < 100; i++) {
            DownloadHistoryTester.handleTrendsTest(historyInfo, lows[i], i);
        }

        assertThat(historyInfo.low90Day, equalTo(-3000.00));
    }

    @Test
    public void test0DayUpTrend() {
        HistoryInfo historyInfo = new HistoryInfo();
        String prices[][] = new String[100][5];
        int j = 100;
        for(int i = 0; i < 100; i++) {
            if(i == 1) {
                prices[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(200);
            } else {
                prices[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(j--);
            }
        }

        for(int i = 0; i < 100; i++) {
            DownloadHistoryTester.handleTrendsTest(historyInfo, prices[i], i+1);
        }

        assertThat(historyInfo.upTrendDayCount, equalTo(0.0));
    }

    @Test
    public void test5DayUpTrend() {
        HistoryInfo historyInfo = new HistoryInfo();
        String prices[][] = new String[100][5];
        int j = 100;
        for(int i = 0; i < 100; i++) {
            if(i == 5) {
                prices[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(200);
            } else {
                prices[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(j--);
            }
        }

        for(int i = 0; i < 100; i++) {
            DownloadHistoryTester.handleTrendsTest(historyInfo, prices[i], i+1);
        }

        assertThat(historyInfo.upTrendDayCount, equalTo(5.0));
    }

    @Test
    public void test10DayUpTrend() {
        HistoryInfo historyInfo = new HistoryInfo();
        String prices[][] = new String[100][5];
        int j = 100;
        for(int i = 0; i < 100; i++) {
            if(i == 10) {
                prices[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(200);
            } else {
                prices[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(j--);
            }
        }

        for(int i = 0; i < 100; i++) {
            DownloadHistoryTester.handleTrendsTest(historyInfo, prices[i], i+1);
        }

        assertThat(historyInfo.upTrendDayCount, equalTo(10.0));
    }

    @Test
    public void test100DayUpTrend() {
        HistoryInfo historyInfo = new HistoryInfo();
        String prices[][] = new String[100][5];
        int j = 100;
        for(int i = 0; i < 100; i++) {
            prices[i][DownloadHistory.DAY_CLOSE_INDEX] = Double.toString(j--);
        }

        for(int i = 0; i < 100; i++) {
            DownloadHistoryTester.handleTrendsTest(historyInfo, prices[i], i+1);
        }

        assertThat(historyInfo.upTrendDayCount, equalTo(10.0));
    }

    @Test
    public void testStopLossForCount() {
        Calendar curCal = Calendar.getInstance();
        String lowCal = null;
        HistoryInfo historyInfo = new HistoryInfo();
        String prices[][] = new String[100][5];
        int j = 100;
        for(int i = 0; i < 100; i++) {
            curCal.add(Calendar.DAY_OF_MONTH, -1);
            prices[i][DownloadHistory.DATE_INDEX] = Util.getDate(curCal);

            if(i == 5) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(200);
            } else if(i == 40) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(500);
            } else if(i == 60) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(550);
            } else if(i == 95) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(700);
            } else {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(j-1);
            }

            if(i == 5) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-100);
            } else if(i == 40) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-300);
            } else if(i == 61) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-350);
            } else if(i == 96) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-900);
                lowCal = Util.getCalStrFromNoTimeStr(Util.getDate(curCal));
            } else {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(j-1);
            }

            j--;

        }

        for(int i = 1; i <= 100; i++) {
            DownloadHistoryTester.handleStopLossTest(historyInfo, prices[i-1], i, null);
        }

        assertThat(historyInfo.historicalHigh, equalTo(700.0));
        assertThat(historyInfo.historicalLow, equalTo(-900.0));
        assertThat(historyInfo.historicalLowDate, equalTo(lowCal));
    }

    @Test
    public void testStopLossForSLDayCount() {
        HistoryInfo historyInfo = new HistoryInfo();
        String prices[][] = new String[100][5];
        int j = 100;
        for(int i = 0; i < 100; i++) {
            if(i == 5) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(200);
            } else if(i == 40) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(500);
            } else if(i == 60) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(550);
            } else if(i == 95) {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(700);
            } else {
                prices[i][DownloadHistory.DAY_HIGH_INDEX] = Double.toString(j-1);
            }

            if(i == 5) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-100);
            } else if(i == 40) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-300);
            } else if(i == 61) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-350);
            } else if(i == 96) {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(-900);
            } else {
                prices[i][DownloadHistory.DAY_LOW_INDEX] = Double.toString(j-1);
            }

            j--;

        }

        for(int i = 1; i <= 100; i++) {
            DownloadHistoryTester.handleStopLossTest(historyInfo, prices[i-1], i, new Integer(80));
        }

        assertThat(historyInfo.historicalHigh, equalTo(550.0));
        assertThat(historyInfo.historicalLow, equalTo(-350.0));
    }

}
