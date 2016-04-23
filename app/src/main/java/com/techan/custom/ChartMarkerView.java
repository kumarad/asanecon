package com.techan.custom;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.techan.R;

import java.util.List;

public class ChartMarkerView extends MarkerView {

    private final TextView textView1;
    private final TextView textView2;
    private List<String> xData;

    public ChartMarkerView(Context context) {
        super(context, R.layout.chart_pop_view);
        textView1 = (TextView) findViewById(R.id.chartPopupText1);
        textView2 = (TextView) findViewById(R.id.chartPopupText2);
    }

    public void setXData(List<String> xData) {
        this.xData = xData;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        textView1.setText(String.format("%s", e.getVal()));
        textView2.setText(xData.get(e.getXIndex()));
    }

    @Override
    public int getXOffset() {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset() {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}