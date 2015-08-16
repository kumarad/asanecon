package com.techan.activities.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidplot.ui.TableModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.techan.R;
import com.techan.memrepo.GoldRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        final TextView selectionView = (TextView) getActivity().findViewById(R.id.goldChartSelection);

        final LineChart chart = (LineChart) getActivity().findViewById(R.id.goldChart);

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
        Map<String, Double> priceMap = GoldRepo.get().getPrices();
        final List<String> dates = new ArrayList<>();
        List<Entry> yEntryList = new ArrayList<>();
        int i = 0;
        for(Map.Entry<String, Double> curEntry : priceMap.entrySet()) {
            Double value = curEntry.getValue();
            yEntryList.add(new Entry(value.floatValue(), i++));
            dates.add(curEntry.getKey());
        }

        LineDataSet yDataSet = new LineDataSet(yEntryList, "Gold");
        yDataSet.setCircleColorHole(color);
        yDataSet.setCircleColor(color);
        yDataSet.setColor(color);

        LineData data = new LineData(new ArrayList<>(priceMap.keySet()), yDataSet);
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
                selectionView.setText(String.format("Date:%s     Price:%s", dates.get(e.getXIndex()), Float.toString(e.getVal())));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

}
