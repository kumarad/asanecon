package com.techan.custom;

import android.graphics.Color;

import com.androidplot.series.XYSeries;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.util.Arrays;

public class TrendPlot {
    public final static String GREEN_BAR_COLOR_STR = "#69a064";
    public final static int GREEN_BAR_COLOR = Color.parseColor(GREEN_BAR_COLOR_STR);

    public final static String RED_BAR_COLOR_STR = "#db6972";
    public final static int RED_BAR_COLOR = Color.parseColor(RED_BAR_COLOR_STR);

    public final static String GREEN_LINE_COLOR_STR = "#00ceaa";
    public final static int GREEN_LINE_COLOR = Color.parseColor(GREEN_LINE_COLOR_STR);

    public final static String RED_LINE_COLOR_STR = "#e30283";
    public final static int RED_LINE_COLOR = Color.parseColor(RED_LINE_COLOR_STR);

    public final static String DARK_BLUE_LINE_COLOR_STR = "#003b6f";
    public final static int DARK_BLUE_LINE_COLOR = Color.parseColor(DARK_BLUE_LINE_COLOR_STR);

    public final static String BLUE_LINE_COLOR_STR = "#194e7d";
    public final static int BLUE_LINE_COLOR = Color.parseColor(BLUE_LINE_COLOR_STR);


    final XYPlot plot;
    final double min;
    final double max;

    final double barVal;
    final int barColor;
    final double line1Val;
    final int line1Color;
    final double line2Val;
    final int line2Color;

    public TrendPlot(XYPlot plot, double barVal, double line1Val, int line1Color, double line2Val, int line2Color) {
        this.plot = plot;

        min = Util.findMin(barVal, line1Val, line2Val);
        max = Util.findMax(barVal, line1Val, line2Val);

        this.barVal = barVal;
        if(barVal == min) {
            barColor = RED_BAR_COLOR;
        } else {
            barColor = GREEN_BAR_COLOR;
        }


        this.line1Val = line1Val;
        this.line1Color = line1Color;

        this.line2Val = line2Val;
        this.line2Color = line2Color;

        setupYAxis();
        setupXAxis();

        setupLayout();
    }

    public void plot() {
        constructBar();
        constructLine(line1Val, line1Color);
        constructLine(line2Val, line2Color);
    }

    public int getLine1Color() { return line1Color; }
    public int getLine2Color() { return line2Color; }
    public int getBarColor() { return barColor; }

    private void setupYAxis() {
        int range = (int)(max - min);
        int interval = range/10;
        if(interval == 0) {
            interval = range;
        }

        plot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, interval);

        int baseYValue = (int)min - interval;
        int topYValue = (int)max + interval;
        plot.setRangeBoundaries(baseYValue, topYValue, BoundaryMode.FIXED);

        // Make the 0 for Y axis disappear.
        plot.getGraphWidget().setRangeOriginLabelPaint(null);
        // Make the y axis numbers disappear
        plot.getGraphWidget().setRangeLabelPaint(null);
        // Make the y axis disappear
        //plot.getGraphWidget().setDomainOriginLinePaint(null);

        plot.getLayoutManager().remove(plot.getRangeLabelWidget());
    }

    private void setupXAxis() {
        plot.setDomainBoundaries(0,2, BoundaryMode.FIXED);
        plot.setDomainStepValue(1);

        // Make the 0 for the X axis disappear.
        plot.getGraphWidget().setDomainOriginLabelPaint(null);
        // Make X axis numbers disappear
        plot.getGraphWidget().setDomainLabelPaint(null);
        // Make the x axis disappear
        //plot.getGraphWidget().setRangeOriginLinePaint(null);

        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
    }

    private void setupLayout() {
        //Makes stuff outside grid transparent.
        plot.getBackgroundPaint().setColor(Color.TRANSPARENT);

        plot.setBorderStyle(XYPlot.BorderStyle.NONE, null, null);
        //plot.setBorderStyle(XYPlot.BorderStyle.ROUNDED, new Float(5), new Float(5));

        // Make stuff for graph transparent.
        // Makes the grid background transparent.
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getGridLinePaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);

        plot.getLayoutManager().remove(plot.getLegendWidget());
        plot.getLayoutManager().remove(plot.getTitleWidget());

        //Margins
        plot.setPlotMargins(0, 0, 0, 0);
        plot.setPlotPadding(0, 0, 0, 0);
        plot.setGridPadding(0, 10, 5, 0);
    }

    private void constructBar() {
        Number[] seriesNumbers = {0,barVal};
        XYSeries series = new SimpleXYSeries(Arrays.asList(seriesNumbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Bar Val");

        BarFormatter series1Format = new BarFormatter(
                barColor,               // line color
                barColor);              // fill color
        plot.addSeries(series, series1Format);

        // Bar width.
        BarRenderer barRenderer = (BarRenderer)plot.getRenderer(BarRenderer.class);
        barRenderer.setBarWidth(50);
    }

    private void constructLine(double val, int color) {
        Number[] numbers = {val,val,val};
        XYSeries series = new SimpleXYSeries(Arrays.asList(numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Line Val");
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(
                color,              // line color
                null,               // point color
                null, new PointLabelFormatter(0));
        plot.addSeries(series, seriesFormat);
    }

}
