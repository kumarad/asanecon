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
    public static final String PEG = "PEG";
    public static final String VOLUME = "VOLUME";
    public static final String AVG_VOLUME = "AVG_VOLUME";
    public static final String CHANGE = "CHANGE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_trend, container, false);
        Bundle args = getArguments();

        double curPrice = args.getDouble(CUR_PRICE);
        double high60Day = args.getDouble(HIGH_60_DAY);
        double low90Day = args.getDouble(LOW_90_DAY);
        double mov50Avg = args.getDouble(MOV_50_VAL);
        double mov200Avg = args.getDouble(MOV_200_VAL);
        double peg = args.getDouble(PEG);
        double vol = args.getDouble(VOLUME);
        double avgVol = args.getDouble(AVG_VOLUME);
        double change = args.getDouble(CHANGE);

        // High/Low
//        XYPlot plot = (XYPlot) rootView.findViewById(R.id.highLowPlot);
//        TrendPlot highLowPlot = new TrendPlot(plot, curPrice, high60Day, TrendPlot.GREEN_LINE_COLOR, low90Day, TrendPlot.RED_LINE_COLOR);
//        highLowPlot.plot();

        TextView high60DayView = (TextView) rootView.findViewById(R.id.high60Day);
        high60DayView.setText("60 day high: " + Double.toString(high60Day));

        TextView low90DayView = (TextView) rootView.findViewById(R.id.low90Day);
        low90DayView.setText("90 day low: " + Double.toString(low90Day));

        LimitProgressBar highLowBar = (LimitProgressBar) rootView.findViewById(R.id.highLowBar);
        highLowBar.setValue(curPrice, high60Day, "High", low90Day, "Low");

        TextView highLowAlertView = (TextView) rootView.findViewById(R.id.highLowAlert);
        if(curPrice > high60Day) {
            highLowAlertView.setText("Breakout");
        } else if(curPrice < low90Day) {
            highLowAlertView.setText("Downside");
        } else {
            highLowAlertView.setVisibility(View.INVISIBLE);
        }


        TextView mov50AvgView = (TextView) rootView.findViewById(R.id.movAvg50);
        mov50AvgView.setText("50 day moving average: " + Double.toString(mov50Avg));

        TextView mov200AvgView = (TextView) rootView.findViewById(R.id.movAvg200);
        mov200AvgView.setText("200 day moving average: " + Double.toString(mov200Avg));

        LimitProgressBar movAvgBar = (LimitProgressBar) rootView.findViewById(R.id.movAvgBar);
        movAvgBar.setValue(curPrice, mov200Avg, "200 day", mov50Avg, "50 day");

        TextView mov50AvgAlertView = (TextView) rootView.findViewById(R.id.movAvg50Alert);
        if(curPrice < mov50Avg) {
            mov50AvgAlertView.setText("Down Price Action");
        } else {
            mov50AvgAlertView.setVisibility(View.INVISIBLE);
        }

        TextView mov200AvgAlertView = (TextView) rootView.findViewById(R.id.mov200AvgAlert);
        if(curPrice < mov200Avg) {
            mov200AvgAlertView.setText("Bear Trend");
        } else if(curPrice > mov200Avg) {
            mov200AvgAlertView.setText("Bull Trend");
        } else {
            mov200AvgAlertView.setVisibility(View.INVISIBLE);
        }

        TextView upTrendCountView = (TextView) rootView.findViewById(R.id.upTrendCount);
        upTrendCountView.setText("Up Trend: ");

        int dayCount = args.getInt(DAY_COUNT);
        if(dayCount > 10)
            dayCount = 10;

        TextView upTrendAlertView = (TextView) rootView.findViewById(R.id.upTrendAlert);
        if(dayCount > 5) {
            upTrendAlertView.setText("Breakout");
        } else {
            upTrendAlertView.setVisibility(View.INVISIBLE);
        }

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

        TextView pegView = (TextView) rootView.findViewById(R.id.pegDetail);
        if(peg > 0)
            pegView.setText("PEG: " + Double.toString(peg));
        else
            pegView.setText("PEG: -");

        TextView pegAlertView = (TextView) rootView.findViewById(R.id.pegAlert);
        if(peg > 2) {
            pegAlertView.setText("High valuation");
        } else {
            pegAlertView.setVisibility(View.INVISIBLE);
        }

        TextView volView = (TextView) rootView.findViewById(R.id.volDetail);
        if(vol > 0) {
            volView.setText("Volume : " + Long.toString((long)vol));
        } else {
            volView.setText("Volume : -");
        }

        TextView avgVolView = (TextView) rootView.findViewById(R.id.avgVolDetail);
        if(avgVol > 0) {
            avgVolView.setText("Average: " + Long.toString((long)avgVol));
        } else {
            avgVolView.setText("Average: -");
        }

        LimitProgressBar volBar = (LimitProgressBar) rootView.findViewById(R.id.avgBar);
        volBar.setValue(vol, "Vol", avgVol, "Avg");

        TextView volAlertView = (TextView) rootView.findViewById(R.id.volAlert);
        if((vol > avgVol && change < 0) || (vol < avgVol && change > 0)) {
            volAlertView.setText("Distribution");
        } else if((vol >avgVol && change > 0) || (vol < avgVol && change < 0)) {
            volAlertView.setText("Accumulation");
        } else {
            volAlertView.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    public void update() {
        //plot.clear() will allow you to redraw the whole thing!
    }
}
