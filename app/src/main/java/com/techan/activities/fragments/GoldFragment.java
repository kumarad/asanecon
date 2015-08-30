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
        buildChart(color, borderWidth, goldChart, goldSelectionView, goldPriceMap, "Gold", "Price");


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
        buildChart(color, borderWidth, goldSPChart, goldSPSelectionView, ratioMap, "Gold/S&P Ratio", "Gold/S&P Ratio");
    }

    public static void buildChart(int color,
                            float borderWidth,
                            LineChart chart,
                            final TextView selectionView,
                            Map<String, Double> valueMap,
                            String label,
                            final String valueLabel) {
        // Y Axis setup
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineWidth(borderWidth);
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisLineColor(color);
        leftAxis.setTextColor(color);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisLineWidth(borderWidth);
        rightAxis.setStartAtZero(false);
        rightAxis.setDrawLabels(false);
        rightAxis.setAxisLineColor(color);

        // X Axis setup
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineColor(color);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisLineWidth(borderWidth);

        // Set data.
        final List<String> dates = new ArrayList<>();
        List<Entry> yEntryList = new ArrayList<>();
        int i = 0;
        for(Map.Entry<String, Double> curEntry : valueMap.entrySet()) {
            Double value = curEntry.getValue();
            yEntryList.add(new Entry(value.floatValue(), i++));
            dates.add(curEntry.getKey());
        }

        LineDataSet yDataSet = new LineDataSet(yEntryList, label);
        yDataSet.setCircleColorHole(color);
        yDataSet.setCircleColor(color);
        yDataSet.setCircleSize(0);
        yDataSet.setColor(color);

        LineData data = new LineData(new ArrayList<>(valueMap.keySet()), yDataSet);
        data.setDrawValues(false);
        data.setHighlightEnabled(true);

        // General chart settings.
        chart.getLegend().setTextColor(color);
        chart.getLegend().setEnabled(false);

        chart.setData(data);
        chart.setBackgroundColor(Color.TRANSPARENT);
        chart.setGridBackgroundColor(Color.TRANSPARENT);
        chart.setBorderColor(color);
        chart.setDescription("");
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                double doubleVal = Util.roundTwoDecimals(e.getVal());
                selectionView.setText(String.format("Date:  %s     %s:  %s", dates.get(e.getXIndex()), valueLabel, Double.toString(doubleVal)));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }



}
