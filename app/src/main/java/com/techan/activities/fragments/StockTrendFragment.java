package com.techan.activities.fragments;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techan.R;
import com.techan.activities.dialogs.InfoDialog;
import com.techan.custom.Util;
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
        curPrice = Util.roundTwoDecimals(curPrice);

        handleHighLowSection(rootView, inflater, curPrice, args.getDouble(HIGH_60_DAY), args.getDouble(LOW_90_DAY));
        handleMovingAverages(rootView, inflater, curPrice, args.getDouble(MOV_50_VAL), args.getDouble(MOV_200_VAL));
        handleTrending(rootView, inflater, args.getInt(DAY_COUNT));
        handleVolumes(rootView, inflater, args.getDouble(VOLUME), args.getDouble(AVG_VOLUME), args.getDouble(CHANGE));
        handlePeg(rootView, inflater, args.getDouble(PEG));

        return rootView;
    }

    protected void handleHighLowSection(View rootView, final LayoutInflater inflater, double curPrice, double high60Day, double low90Day) {
        TextView high60DayView = (TextView) rootView.findViewById(R.id.high60Day);
        high60Day = Util.roundTwoDecimals(high60Day);
        high60DayView.setText("60 day high: " + Double.toString(high60Day));

        TextView low90DayView = (TextView) rootView.findViewById(R.id.low90Day);
        low90Day = Util.roundTwoDecimals(low90Day);
        low90DayView.setText("90 day low: " + Double.toString(low90Day));

        LimitProgressBar highLowBar = (LimitProgressBar) rootView.findViewById(R.id.highLowBar);
        highLowBar.setValue(curPrice, high60Day, "High", low90Day, "Low");

        TextView highLowAlertView = (TextView) rootView.findViewById(R.id.highLowAlert);
        if(curPrice > high60Day) {
            highLowAlertView.setText("Breakout");
            InfoDialog.setOnClickInfoDialog(highLowAlertView, inflater, rootView.getResources().getString(R.string.breakout));
        } else if(curPrice < low90Day) {
            highLowAlertView.setText("Downside");
            InfoDialog.setOnClickInfoDialog(highLowAlertView, inflater, rootView.getResources().getString(R.string.downside));
        } else {
            highLowAlertView.setVisibility(View.INVISIBLE);
        }
    }

    protected void handleMovingAverages(View rootView, final LayoutInflater inflater, double curPrice, double mov50Avg, double mov200Avg) {
        TextView mov50AvgView = (TextView) rootView.findViewById(R.id.movAvg50);
        mov50Avg = Util.roundTwoDecimals(mov50Avg);
        mov50AvgView.setText("50 day moving average: " + Double.toString(mov50Avg));

        TextView mov200AvgView = (TextView) rootView.findViewById(R.id.movAvg200);
        mov200Avg = Util.roundTwoDecimals(mov200Avg);
        mov200AvgView.setText("200 day moving average: " + Double.toString(mov200Avg));

        LimitProgressBar movAvgBar = (LimitProgressBar) rootView.findViewById(R.id.movAvgBar);
        movAvgBar.setValue(curPrice, mov200Avg, "200 day", mov50Avg, "50 day");

        TextView mov50AvgAlertView = (TextView) rootView.findViewById(R.id.movAvg50Alert);
        if(curPrice < mov50Avg) {
            mov50AvgAlertView.setText("Down Price Action");
            InfoDialog.setOnClickInfoDialog(mov50AvgAlertView, inflater, rootView.getResources().getString(R.string.down_price_action));
        } else {
            mov50AvgAlertView.setVisibility(View.INVISIBLE);
        }

        TextView mov200AvgAlertView = (TextView) rootView.findViewById(R.id.mov200AvgAlert);
        if(curPrice < mov200Avg) {
            mov200AvgAlertView.setText("Bear Trend");
            InfoDialog.setOnClickInfoDialog(mov200AvgAlertView, inflater, rootView.getResources().getString(R.string.bear_trend));
        } else if(curPrice > mov200Avg) {
            mov200AvgAlertView.setText("Bull Trend");
            InfoDialog.setOnClickInfoDialog(mov200AvgAlertView, inflater, rootView.getResources().getString(R.string.bull_trend));
        } else {
            mov200AvgAlertView.setVisibility(View.INVISIBLE);
        }
    }

    protected void handleTrending(View rootView, final LayoutInflater inflater, int dayCount) {
        TextView upTrendCountView = (TextView) rootView.findViewById(R.id.upTrendCount);
        upTrendCountView.setText("Up Trend: ");

        if(dayCount > 10)
            dayCount = 10;

        TextView upTrendAlertView = (TextView) rootView.findViewById(R.id.upTrendAlert);
        if(dayCount > 5) {
            upTrendAlertView.setText("Breakout");
            InfoDialog.setOnClickInfoDialog(upTrendAlertView, inflater, rootView.getResources().getString(R.string.breakout));
        } else {
            upTrendAlertView.setVisibility(View.INVISIBLE);
        }

        SaundProgressBar regularProgressBar = (SaundProgressBar) rootView.findViewById(R.id.upTrendBar);
        Drawable indicator = getResources().getDrawable(R.drawable.progress_indicator_b2);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + SaundProgressBar.INDICATOR_PADDING, indicator.getIntrinsicHeight());
        indicator.setBounds(bounds);
        regularProgressBar.setProgressIndicator(indicator);
        regularProgressBar.setTextFormatter(new SaundProgressBar.Formatter() {
            @Override
            public String getText(int progress) {
                return Integer.toString(progress / 10) + " days";
            }
        });
        regularProgressBar.setProgress(dayCount*10);
    }

    protected void handleVolumes(View rootView, final LayoutInflater inflater, double vol, double avgVol, double change) {
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
            if((vol > avgVol && change < 0)) {
                InfoDialog.setOnClickInfoDialog(volAlertView, inflater, rootView.getResources().getString(R.string.distribution_heavy));
            } else {
                InfoDialog.setOnClickInfoDialog(volAlertView, inflater, rootView.getResources().getString(R.string.distribution_light));
            }
        } else if((vol > avgVol && change > 0) || (vol < avgVol && change < 0)) {
            volAlertView.setText("Accumulation");
            if((vol > avgVol && change > 0)) {
                InfoDialog.setOnClickInfoDialog(volAlertView, inflater, rootView.getResources().getString(R.string.accumulation_heavy));
            } else {
                InfoDialog.setOnClickInfoDialog(volAlertView, inflater, rootView.getResources().getString(R.string.accumulation_light));
            }
        } else {
            volAlertView.setVisibility(View.INVISIBLE);
        }
    }

    protected void handlePeg(View rootView, final LayoutInflater inflater, double peg) {
        TextView pegView = (TextView) rootView.findViewById(R.id.pegDetail);
        if(peg > 0)
            pegView.setText("PEG: " + Double.toString(peg));
        else
            pegView.setText("PEG: -");

        TextView pegAlertView = (TextView) rootView.findViewById(R.id.pegAlert);
        if(peg > 2) {
            pegAlertView.setText("High valuation");
            InfoDialog.setOnClickInfoDialog(pegAlertView, inflater, rootView.getResources().getString(R.string.high_valuation));
        } else {
            pegAlertView.setVisibility(View.INVISIBLE);
        }
    }
}
