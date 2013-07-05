package com.techan.stockDownload;

import android.net.http.AndroidHttpClient;

import com.techan.custom.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class DownloadHistory {
    public static final String URL_PREFIX = "http://ichart.yahoo.com/table.csv?s=";
    public static String DAILY_INTERVAL = "&g=d";
    public static final int DAY_COUNT_10 = 10;
    public static final int DAY_COUNT_60 = 60;
    public static final int DAY_COUNT_90 = 90;

    public static final int DATE_INDEX = 0;
    public static final int DAY_HIGH_INDEX = 2;
    public static final int DAY_LOW_INDEX = 3;
    public static final int DAY_CLOSE_INDEX = 4;

    protected static String generateUrl(String symbol, int startMonth, int startDay, int startYear,
                                      int endMonth, int endDay, int endYear) {
        // Generate url
        String url = URL_PREFIX;
        url += symbol;
        url += "&a=" + startMonth;
        url += "&b=" + startDay;
        url += "&c=" + startYear;
        url += "&d=" + endMonth;
        url += "&e=" + endDay;
        url += "&f=" + endYear;

        url += DAILY_INTERVAL;

        return url;
    }

    // Calendar and Yahoo uses 0 based month.
    protected static String generateUrlForDays(String symbol, Calendar curCal, int count) {
        int endDay = curCal.get(Calendar.DAY_OF_MONTH);
        int endMonth = curCal.get(Calendar.MONTH);
        int endYear = curCal.get(Calendar.YEAR);

        Calendar startCal = (Calendar)curCal.clone();
        startCal.add(Calendar.DAY_OF_MONTH, count * -1);

        int startDay = startCal.get(Calendar.DAY_OF_MONTH);
        int startMonth = startCal.get(Calendar.MONTH);
        int startYear = startCal.get(Calendar.YEAR);

        endDay = endDay -1;

        return generateUrl(symbol, startMonth, startDay, startYear, endMonth, endDay, endYear);
    }

    protected static String generateUrlForRange(String symbol, Calendar curCal, Calendar lastCal) {
        int endDay = curCal.get(Calendar.DAY_OF_MONTH);
        int endMonth = curCal.get(Calendar.MONTH); // 0 based month
        int endYear = curCal.get(Calendar.YEAR);

        int startDay = lastCal.get(Calendar.DAY_OF_MONTH);
        int startMonth = lastCal.get(Calendar.MONTH);
        int startYear = lastCal.get(Calendar.YEAR);

        endDay = endDay -1;

        return generateUrl(symbol, startMonth, startDay, startYear, endMonth, endDay, endYear);
    }

    protected static LowestCalInfo lowestDate(Calendar curCal, final String lastSLUpdate) {
        Calendar cal90 = (Calendar)curCal.clone();
        cal90.add(Calendar.DAY_OF_MONTH, DAY_COUNT_90 * -1);

        Calendar calSl = Util.getCal(lastSLUpdate);
        if(calSl.before(cal90)) {
            // Stop loss last update was done longer than 90 days ago.
            return new LowestCalInfo(calSl, null);
        } else {
            // Stop loss last update is within the 90 day period.
            return new LowestCalInfo(cal90, Util.dateDiff(calSl, curCal));
        }
    }

    protected static void handleTrends(HistoryInfo historyInfo, String[] rowData, int count) {
        double closePrice = Double.parseDouble(rowData[DAY_CLOSE_INDEX]);
        if(count <= DAY_COUNT_60 && closePrice > historyInfo.high60Day) {
            historyInfo.high60Day = closePrice;
        }

        if(count <= DAY_COUNT_90 && closePrice < historyInfo.low90Day ) {
            historyInfo.low90Day = closePrice;
        }

        if(count <= DAY_COUNT_10 && historyInfo.trackUpTrend) {
            if(closePrice < historyInfo.prevDayClose) {
                // Previous days close is lower. Stock went up.
                historyInfo.upTrendDayCount++;
                historyInfo.prevDayClose = closePrice;
            } else {
                // Previous days price is higher. Not an uptrend.
                // Stop trying to calculate the uptrend.
                historyInfo.trackUpTrend = false;
                if(count == 2) {
                    historyInfo.upTrendDayCount = 0;
                }
            }
        }
    }

    protected static void handleStopLoss(HistoryInfo historyInfo, String[] rowData, int count, Integer slDayCountLessThan90) {
        if(slDayCountLessThan90 == null || count <= slDayCountLessThan90) {
            double daysHigh = Double.parseDouble(rowData[DAY_HIGH_INDEX]);
            if(daysHigh > historyInfo.historicalHigh) {
                historyInfo.historicalHigh = daysHigh;
            }

            double daysLow = Double.parseDouble(rowData[DAY_LOW_INDEX]);
            if(daysLow < historyInfo.historicalLow) {
                historyInfo.historicalLow = daysLow;
                historyInfo.historicalLowDate = Util.getCalStrFromNoTimeStr(rowData[DATE_INDEX]);
            }
        }
    }

    // StockTrends will be null if exception/error is encountered.
    protected static void downloadInternal(String url, StockData data, HistoryInfo historyInfo, boolean includeSL, Integer slDayCountLessThan90) {
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpContext localContext = new BasicHttpContext();
        HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest, localContext);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            // Data is returned with latest date on top.
            int i = 0;
            String line = reader.readLine();    // First line defines columns.
            while((line = reader.readLine()) != null) {
                i++;
                String[] rowData = line.split(",");

                handleTrends(historyInfo, rowData, i);
                if(includeSL) {
                    handleStopLoss(historyInfo, rowData, i, slDayCountLessThan90);
                }
            }

            StockTrends stockTrends = new StockTrends(historyInfo.upTrendDayCount,
                                                      historyInfo.high60Day,
                                                      historyInfo.low90Day);
            if(includeSL) {
                stockTrends.setHistoricalInfo(historyInfo.historicalHigh,
                                              historyInfo.historicalLow,
                                              historyInfo.historicalLowDate);
            }
            data.stockTrends = stockTrends;
        } catch(IOException e) {
            getRequest.abort();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(client != null) {
                client.close();
            }
        }
    }

    public static void download(StockData data, Calendar curCalDate, final String lastSLUpdate, double historicalHigh, double historicalLow) {
        if(lastSLUpdate != null) {
            LowestCalInfo lowestCalInfo = lowestDate(curCalDate, lastSLUpdate);
            String url = generateUrlForRange(data.symbol, curCalDate, lowestCalInfo.lowestCal);
            HistoryInfo historyInfo = new HistoryInfo(historicalHigh, historicalLow);
            downloadInternal(url, data, historyInfo, true, lowestCalInfo.slDayCountLessThan90);
        } else {
            download(data, curCalDate);
        }
    }

    public static void download(StockData data, Calendar curCalDate) {
        String url = generateUrlForDays(data.symbol, curCalDate, DAY_COUNT_90);
        downloadInternal(url, data, new HistoryInfo(), false, null);
    }
}

