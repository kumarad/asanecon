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
        } catch(NumberFormatException e) {
            return 0.0;
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

    public static SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static boolean isDateSame(String lastUpdate, Calendar curCal) {
        if(lastUpdate != null) {
            // Stock has been updated before.
            try {
                Date dbDate = formater.parse(lastUpdate);
                Calendar dbCal = new GregorianCalendar();
                dbCal.setTime(dbDate);
                if(dbCal.get(Calendar.DAY_OF_MONTH) == curCal.get(Calendar.DAY_OF_MONTH) &&
                        dbCal.get(Calendar.MONTH) == curCal.get(Calendar.MONTH) &&
                        dbCal.get(Calendar.YEAR) == curCal.get(Calendar.YEAR)) {
                    // Stock already updated today once.
                    return true;
                }
            } catch(ParseException e) {
                throw new RuntimeException("Exception parsing date from db.", e);
            }
        }

        return false;
    }

    // Provides feedback in a small pop up black window.
    public static void showErrorToast(Activity activity, String error) {
        Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
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

        double changePercent = Util.roundTwoDecimals((Math.abs(change)*100.0)/original);
        textView.append(" (");
        textView.append(Double.toString(changePercent));
        textView.append("%)");
    }
}
