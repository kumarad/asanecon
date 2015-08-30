package com.techan.activities.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.techan.R;
import com.techan.custom.ChartBuilder;
import com.techan.custom.Util;
import com.techan.memrepo.HistoryRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GoldFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gold_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int color = getActivity().getResources().getColor(R.color.blue);
        float borderWidth = 2;

        Map<String, Double> goldPriceMap = HistoryRepo.getGoldRepo().getPrices();

        final TextView goldSelectionView = (TextView) getActivity().findViewById(R.id.goldChartSelection);
        final LineChart goldChart = (LineChart) getActivity().findViewById(R.id.goldChart);
        ChartBuilder.build(color, borderWidth, goldChart, goldSelectionView, goldPriceMap, "Gold", "Price");


        Map<String, Double> spPriceMap = HistoryRepo.getSPRepo().getPrices();
        Map<String, Double> ratioMap = new TreeMap<>();
        for(Map.Entry<String, Double> curGoldEntry : goldPriceMap.entrySet()) {
            Double spValue = spPriceMap.get(curGoldEntry.getKey());
            if(spValue != null) {
                ratioMap.put(curGoldEntry.getKey(), curGoldEntry.getValue() / spValue);
            }
        }
        final TextView goldSPSelectionView = (TextView) getActivity().findViewById(R.id.goldSPChartSelection);
        final LineChart goldSPChart = (LineChart) getActivity().findViewById(R.id.goldSPChart);
        ChartBuilder.build(color, borderWidth, goldSPChart, goldSPSelectionView, ratioMap, "Gold/S&P Ratio", "Gold/S&P Ratio");
    }
}
