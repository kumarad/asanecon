package com.techan.activities.fragments;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.xy.XYPlot;
import com.techan.R;
import com.techan.custom.TrendPlot;
import com.techan.progressbar.LimitProgressBar;
import com.techan.progressbar.SaundProgressBar;

public class StockTrendFragment extends Fragment {
    public static final String CUR_PRICE = "CUR_PRICE";
    public static final String MOV_50_VAL = "MOV_50_VAL";
    public static final String MOV_200_VAL = "MOV_200_VAL";
    public static final String DAY_COUNT = "DAY_COUNT";
    public static final String HIGH_60_DAY = "HIGH_60_DAY";
    public static final String LOW_90_DAY = "LOW_90_DAY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_trend, container, false);
        Bundle args = getArguments();

        double curPrice = args.getDouble(CUR_PRICE);
        double high60Day = args.getDouble(HIGH_60_DAY);
        double low90Day = args.getDouble(LOW_90_DAY);
        double mov50Avg = args.getDouble(MOV_50_VAL);
        double mov200Avg = args.getDouble(MOV_200_VAL);

        // High/Low
        XYPlot plot = (XYPlot) rootView.findViewById(R.id.highLowPlot);
        TrendPlot highLowPlot = new TrendPlot(plot, curPrice, high60Day, TrendPlot.GREEN_LINE_COLOR, low90Day, TrendPlot.RED_LINE_COLOR);
        highLowPlot.plot();

        TextView high60DayKeyView = (TextView) rootView.findViewById(R.id.high60DayKey);
        high60DayKeyView.setTextColor(highLowPlot.getLine1Color());
        TextView high60DayView = (TextView) rootView.findViewById(R.id.high60Day);
        high60DayView.setText("60 Day High = " + Double.toString(high60Day));

        TextView low90DayKeyView = (TextView) rootView.findViewById(R.id.low90DayKey);
        low90DayKeyView.setTextColor(highLowPlot.getLine2Color());
        TextView low90DayView = (TextView) rootView.findViewById(R.id.low90Day);
        low90DayView.setText("90 Day Low = " + Double.toString(low90Day));

        TextView highLowCurKey = (TextView) rootView.findViewById(R.id.highLowCurKey);
        highLowCurKey.setBackgroundColor(highLowPlot.getBarColor());

        // Averages.
        plot = (XYPlot) rootView.findViewById(R.id.avgPlot);
        TrendPlot avgPlot = new TrendPlot(plot, curPrice, mov200Avg, TrendPlot.GREEN_LINE_COLOR, mov50Avg, TrendPlot.BLUE_LINE_COLOR);
        avgPlot.plot();

        TextView mov50AvgKeyView = (TextView) rootView.findViewById(R.id.movAvg50Key);
        mov50AvgKeyView.setTextColor(avgPlot.getLine2Color());
        TextView mov50AvgView = (TextView) rootView.findViewById(R.id.movAvg50);
        mov50AvgView.setText("50 Day Moving Avg. = " + Double.toString(mov50Avg));

        TextView mov200AvgKeyView = (TextView) rootView.findViewById(R.id.movAvg200Key);
        mov200AvgKeyView.setTextColor(avgPlot.getLine1Color());
        TextView mov200AvgView = (TextView) rootView.findViewById(R.id.movAvg200);
        mov200AvgView.setText("200 Day Moving Avg. = " + Double.toString(mov200Avg));

        TextView avgCurKey = (TextView) rootView.findViewById(R.id.avgCurKey);
        avgCurKey.setBackgroundColor(avgPlot.getBarColor());


//        LimitProgressBar avgProgressBar = (LimitProgressBar) rootView.findViewById(R.id.avgBar);
//        avgProgressBar.setValue(curPrice, mov200Avg, "200d", mov50Avg, "50d");

        TextView upTrendCountView = (TextView) rootView.findViewById(R.id.upTrendCount);
        upTrendCountView.setText("Up Trend: ");

        int dayCount = args.getInt(DAY_COUNT);
        if(dayCount > 10)
            dayCount = 10;
        SaundProgressBar regularProgressBar = (SaundProgressBar) rootView.findViewById(R.id.upTrendBar);
        Drawable indicator = getResources().getDrawable(R.drawable.progress_indicator_b2);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5, indicator.getIntrinsicHeight());
        indicator.setBounds(bounds);
        regularProgressBar.setProgressIndicator(indicator);
        regularProgressBar.setTextFormatter(new SaundProgressBar.Formatter() {
            @Override
            public String getText(int progress) {
                return Integer.toString(progress/10) + " days";
            }
        });
        regularProgressBar.setProgress(dayCount*10);

        return rootView;
    }

    public void update() {
        //plot.clear() will allow you to redraw the whole thing!
    }
}
