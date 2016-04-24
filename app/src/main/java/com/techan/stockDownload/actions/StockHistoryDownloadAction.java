package com.techan.stockDownload.actions;

import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.techan.custom.ChartBuilder;
import com.techan.custom.ChartMarkerView;
import com.techan.memrepo.HistoryRepo;

import java.util.Map;

public class StockHistoryDownloadAction implements PostRefreshAction {

    private final String symbol;
    private final LineChart chart;
    private final View progressView;
    private final View contentView;
    private final int color;

    public StockHistoryDownloadAction(String symbol, LineChart lineChart, View progressView, View contentView, int color) {
        this.symbol = symbol;
        this.chart = lineChart;
        this.progressView = progressView;
        this.contentView = contentView;
        this.color = color;
    }

    @Override
    public void execute() {
        Map<String, Double> stockPriceMap = HistoryRepo.getStockRepo(symbol).getPrices();

        ChartBuilder.build(color,
                chart,
                stockPriceMap,
                symbol,
                new ChartMarkerView(progressView.getContext()));

        progressView.setVisibility(View.INVISIBLE);
        contentView.setVisibility(View.VISIBLE);
    }
}
