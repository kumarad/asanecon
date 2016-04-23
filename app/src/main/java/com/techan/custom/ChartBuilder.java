package com.techan.custom;

import android.graphics.Color;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartBuilder {
    public static void build(int color,
                             LineChart chart,
                             final TextView selectionView,
                             Map<String, Double> valueMap,
                             String label,
                             final String valueLabel) {
        // Y Axis setup
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setStartAtZero(false);
        leftAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setStartAtZero(false);
        rightAxis.setDrawLabels(false);

        // X Axis setup
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setAxisLineColor(color);
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);

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
        yDataSet.setDrawFilled(true);
        yDataSet.setFillAlpha(255);
        yDataSet.setFillColor(color);
        yDataSet.setCircleSize(0);
        yDataSet.setColor(color);

        LineData data = new LineData(new ArrayList<>(valueMap.keySet()), yDataSet);
        data.setDrawValues(false);
        data.setHighlightEnabled(true);

        // General chart settings.
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getLegend().setEnabled(false);

        chart.setData(data);
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
