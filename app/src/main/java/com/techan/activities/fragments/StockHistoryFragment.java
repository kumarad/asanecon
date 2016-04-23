package com.techan.activities.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.squareup.otto.Subscribe;
import com.techan.R;
import com.techan.activities.BusService;
import com.techan.custom.ChartBuilder;
import com.techan.memrepo.HistoryRepo;
import com.techan.stockDownload.retro.StockHistoryDownloader;

import java.util.Map;

public class StockHistoryFragment extends Fragment {
    private String symbol;
    private View rootView;
    private View progressView;
    private View chartView;

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.stock_history, container, false);

        progressView = rootView.findViewById(R.id.stockHistoryProgress);
        chartView = rootView.findViewById(R.id.stockHistoryChartLayout);

        progressView.setVisibility(View.VISIBLE);
        chartView.setVisibility(View.INVISIBLE);

        HistoryRepo repo = HistoryRepo.getStockRepo(symbol);
        String lastUpdateDateStr = null;
        if(repo != null) {
            if(repo.alreadyUpdatedToday()) {
                doneLoading(null);
            } else if(repo.getPrices().size() > 0) {
                lastUpdateDateStr = repo.getLatestPriceDate();
            }
        }

        StockHistoryDownloader downloader = StockHistoryDownloader.getInstance();
        downloader.get(symbol, lastUpdateDateStr);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusService.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusService.getInstance().unregister(this);
    }

    @Subscribe
    public void doneLoading(StockHistoryDownloader.StockHistoryDownloaderComplete event) {
        int color = getActivity().getResources().getColor(R.color.asaneconTheme);
        Map<String, Double> stockPriceMap = HistoryRepo.getStockRepo(symbol).getPrices();

        final TextView selectionView = (TextView) rootView.findViewById(R.id.stockHistoryChartSelection);
        final LineChart chart = (LineChart) rootView.findViewById(R.id.stockHistoryChart);
        ChartBuilder.build(color, chart, selectionView, stockPriceMap, symbol, "Price");

        progressView.setVisibility(View.INVISIBLE);
        chartView.setVisibility(View.VISIBLE);
    }

}
