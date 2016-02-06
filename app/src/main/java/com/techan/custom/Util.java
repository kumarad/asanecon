package com.techan.custom;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Util {

    public static void createBar(Activity activity, ProgressBar bar,  String color, int progressInt) {
        // Define a shape with rounded corners
        final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

        // Sets the progressBar color
        pgDrawable.getPaint().setColor(Color.parseColor(color));

        // Adds the drawable to your progressBar
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        bar.setProgressDrawable(progress);
        bar.setBackground(activity.getResources().getDrawable(android.R.drawable.progress_horizontal));
        bar.setProgress(progressInt);

    }

    public static void createBar(Fragment fragment, ProgressBar bar,  String color, int progressInt) {
        // Define a shape with rounded corners
        final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

        // Sets the progressBar color
        pgDrawable.getPaint().setColor(Color.parseColor(color));

        // Adds the drawable to your progressBar
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        bar.setProgressDrawable(progress);
        bar.setBackground(fragment.getResources().getDrawable(android.R.drawable.progress_horizontal));
        bar.setProgress(progressInt);
    }

    public static double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch(Exception e) {
            return 0.0;
        }
    }

    public static String doubleToString(Double value) {
        if(value == null) {
            return "-";
        } else {
            return value.toString();
        }
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static String parseDouble(Cursor c, int index) {
        double d = c.getDouble(index);
        if(d == 0) {
            return "N/A";
        }

        return Double.toString(d);
    }

    public static SimpleDateFormat DATE_TIME_FORMATER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat DATE_ONLY_FORMATER = new SimpleDateFormat("yyyy-MM-dd");

    public static String getCalStrFromNoTimeStr(String date) {
        return date + " 00:00:00";
    }

    public static String getCalStr(int year, int month, int day) {
        return getCalStrFromNoTimeStr(year + "-" + month + "-" + day);
    }

    public static String getDate(Calendar cal) {
        String str = cal.get(Calendar.YEAR) + "-";
        str += (cal.get(Calendar.MONTH) + 1) + "-";
        str += (cal.get(Calendar.DAY_OF_MONTH) + 1);
        return str;
    }

    public static Calendar getCurCalWithZeroTime() {
        Calendar curCal = (Calendar)Calendar.getInstance().clone();
        curCal.set(Calendar.HOUR_OF_DAY,0);
        curCal.set(Calendar.MINUTE,0);
        curCal.set(Calendar.SECOND,0);
        curCal.set(Calendar.MILLISECOND,0);

        return curCal;
    }

    public static String getDateFromDateTimeStr(String dateTime) {
        return dateTime.split(" ")[0];
    }

    public static String getDateStrForDb(Calendar cal) {
        Date curDate = cal.getTime();
        return Util.DATE_TIME_FORMATER.format(curDate);
    }

    public static Calendar getCal(final String lastUpdate) {
        try {
            Date date = DATE_TIME_FORMATER.parse(lastUpdate);
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            return cal;
        } catch(ParseException e) {
            throw new RuntimeException("Exception parsing date from db.", e);
        }
    }

    public static Calendar getCalForDateOnly(final String lastUpdate) {
        try {
            Date date = DATE_ONLY_FORMATER.parse(lastUpdate);
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            return cal;
        } catch(ParseException e) {
            throw new RuntimeException("Exception parsing date from db.", e);
        }
    }


    public static boolean isDateSame(String lastUpdate, Calendar curCal) {
        if(lastUpdate != null) {
            Calendar dbCal = getCal(lastUpdate);
            return isDateSame(dbCal, curCal);
        }

        return false;
    }

    public static boolean isDateToday(long timestamp) {
        Date date = new Date(timestamp);
        Calendar timestampCal = Calendar.getInstance();
        timestampCal.setTime(date);
        return isDateSame(timestampCal, Calendar.getInstance());
    }

    public static boolean isDateSame(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);

    }

    public static int dateDiff(Calendar someCal, Calendar curCal) {
        int days = 0;
        Calendar clonedCal = (Calendar)someCal.clone();
        while(clonedCal.before(curCal)) {
            clonedCal.add(Calendar.DAY_OF_MONTH, 1);
            days++;
        }

        return days;
    }

    public static boolean isDateLess(Calendar isLowerCal, Calendar otherCal) {
        if(isLowerCal.before(otherCal)) {
            return true;
        }

        return false;
    }

    // Provides feedback in a small pop up black window.
    public static void showErrorToast(Context context, String error) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
    }

    public static ConnectionStatus isOnline(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo != null && wifiNetworkInfo.isConnected())
            return ConnectionStatus.ONLINE_WIFI;

        NetworkInfo onlineNetworkInfo = connMgr.getActiveNetworkInfo();
        if(onlineNetworkInfo != null && onlineNetworkInfo.isConnected())
            return ConnectionStatus.ONLINE_NON_WIFI;

        return ConnectionStatus.OFFLINE;
    }

    public static void showChange(TextView textView, double change, double original, TextView labelView) {
        if(change < 0) {
            textView.setTextColor(Color.RED);
            if(labelView != null) labelView.setText("Loss:");
        } else if(change > 0) {
            textView.setTextColor(Color.GREEN);
            if(labelView != null) labelView.setText("Gain:");
        }

        textView.setText(Double.toString(Util.roundTwoDecimals(change)));

        double changePercent = Util.roundTwoDecimals((Math.abs(change) * 100.0) / original);
        textView.append(" (");
        textView.append(Double.toString(changePercent));
        textView.append("%)");
    }

    public static boolean setPositiveColor(Double value,
                                           Double rangeStart,
                                           boolean rangeStartCanBeEqual,
                                           Double rangeEnd,
                                           boolean rangeEndCanBeEqual,
                                           TextView view) {
        if(value != null && rangeStart != null && rangeEnd != null) {
            boolean withinUpperBound = rangeEndCanBeEqual ? value <= rangeEnd : value < rangeEnd;
            boolean withinLowerBound = rangeStartCanBeEqual ? value >= rangeStart : value > rangeStart;
            if(withinLowerBound && withinUpperBound) {
                view.setTextColor(Color.GREEN);
                return true;
            }
        }
        return false;
    }

    public static boolean setNegativeColor(Double value,
                                           double rangeStart,
                                           boolean rangeStartCanBeEqual,
                                           double rangeEnd,
                                           boolean rangeEndCanBeEqual,
                                           TextView view) {
        if(value != null) {
            boolean withinUpperBound = rangeEndCanBeEqual ? value <= rangeEnd : value < rangeEnd;
            boolean withinLowerBound = rangeStartCanBeEqual ? value >= rangeStart : value > rangeStart;
            if(withinLowerBound && withinUpperBound) {
                view.setTextColor(Color.RED);
                return true;
            }
        }
        return false;
    }

    public static void setChange(TextView changeView, double change, double original) {
        double changePercent = Util.roundTwoDecimals((Math.abs(change)*100.0)/original);
        changeView.setText(" (");

        if(change < 0) {
            changeView.append("-");
        }

        changeView.append(Double.toString(changePercent));
        changeView.append("%)");
    }

    public static double findMax(double... vals) {
        double max = Double.MIN_VALUE;
        for(double d : vals) {
            if(d > max) {
                max = d;
            }
        }

        return max;
    }

    public static double findMin(double... vals) {
        double min = Double.MAX_VALUE;
        for(double d : vals) {
            if(d < min) {
                min = d;
            }
        }

        return min;
    }

}
