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
import com.techan.custom.TextProgressBar;
import com.techan.custom.Util;
import com.techan.progressbar.SaundProgressBar;

public class StockTrendFragment extends Fragment {
    public static final String MOV_50_VAL = "MOV_50_VAL";
    public static final String MOV_200_VAL = "MOV_200_VAL";
    public static final String DAY_COUNT = "DAY_COUNT";
    public static final String HIGH_60_DAY = "HIGH_60_DAY";
    public static final String LOW_90_DAY = "LOW_90_DAY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_trend, container, false);
        Bundle args = getArguments();

        TextView mov50View = (TextView) rootView.findViewById(R.id.detailMovAvg50);
        mov50View.setText("50d movAvg: ");
        TextView mov50ValView = (TextView) rootView.findViewById(R.id.detailMovAvg50Val);
        mov50ValView.setText(args.getString(MOV_50_VAL));

        TextView mov200View = (TextView) rootView.findViewById(R.id.detailMovAvg200);
        mov200View.setText("200d movAvg: ");
        TextView mov200ValView = (TextView) rootView.findViewById(R.id.detailMovAvg200Val);
        mov200ValView.append(args.getString(MOV_200_VAL));

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

//
//        TextProgressBar upTrendBar = (TextProgressBar) rootView.findViewById(R.id.upTrendBar);
//        Util.createBar(this, upTrendBar, "#93d500", dayCount*10);
//        upTrendBar.setText(Integer.toString(dayCount) + "/10 Days");


        TextView high60DayView = (TextView) rootView.findViewById(R.id.high60Day);
        high60DayView.setText("High(60): ");
        TextView high60DayValView = (TextView) rootView.findViewById(R.id.high60DayVal);
        high60DayValView.setText(args.getString(HIGH_60_DAY));

        TextView low90DayView = (TextView) rootView.findViewById(R.id.low90Day);
        low90DayView.setText("Low(90): ");
        TextView low90DayValView = (TextView) rootView.findViewById(R.id.low90DayVal);
        low90DayValView.setText(args.getString(LOW_90_DAY));

        return rootView;
    }
}
