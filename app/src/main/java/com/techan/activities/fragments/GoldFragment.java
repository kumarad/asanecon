package com.techan.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.squareup.otto.Subscribe;
import com.techan.R;
import com.techan.activities.BusService;
import com.techan.custom.ChartBuilder;
import com.techan.custom.ChartMarkerView;
import com.techan.memrepo.GoldRepo;
import com.techan.memrepo.HistoryRepo;
import com.techan.stockDownload.retro.GoldDownloader;
import com.techan.stockDownload.retro.SPDownloader;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GoldFragment extends Fragment {

    private AtomicInteger downloadsDone = new AtomicInteger(0);
    private View progressView;
    private View contentView;
    private volatile int expectedDownloads = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.gold_fragment, container, false);
        progressView = root.findViewById(R.id.goldProgressBar);
        contentView = root.findViewById(R.id.goldContentView);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusService.getInstance().register(this);

        progressView.setVisibility(View.VISIBLE);
        contentView.setVisibility(View.INVISIBLE);

        HistoryRepo goldRepo = GoldRepo.getRepo();
        if(!goldRepo.alreadyUpdatedToday()) {
            GoldDownloader.getInstance().get(goldRepo.getLatestPriceDate());
            expectedDownloads++;
        }

        HistoryRepo spRepo = HistoryRepo.getSPRepo();
        if(!spRepo.alreadyUpdatedToday()) {
            SPDownloader.getInstance().get(spRepo.getLatestPriceDate());
            expectedDownloads++;
        }

        done();
    }

    @Override
    public void onPause() {
        super.onPause();
        BusService.getInstance().register(this);
    }

    @Subscribe
    public void goldDownloadComplete(GoldDownloader.GoldDownloaderComplete event) {
        downloadsDone.incrementAndGet();
        done();
    }

    @Subscribe
    public void spDownloadComplete(SPDownloader.SPDownloaderComplete event) {
        downloadsDone.incrementAndGet();
        done();
    }

    private void done() {
        if(downloadsDone.get() == expectedDownloads) {
            int color = getActivity().getResources().getColor(R.color.blue);
            Map<String, Double> goldPriceMap = GoldRepo.getRepo().getPrices();

           final LineChart goldChart = (LineChart) getActivity().findViewById(R.id.goldChart);
            ChartBuilder.build(color, goldChart, goldPriceMap, "Gold", new ChartMarkerView(getActivity()));


            Map<String, Double> spPriceMap = HistoryRepo.getSPRepo().getPrices();
            Map<String, Double> ratioMap = new TreeMap<>();
            for (Map.Entry<String, Double> curGoldEntry : goldPriceMap.entrySet()) {
                Double spValue = spPriceMap.get(curGoldEntry.getKey());
                if (spValue != null) {
                    ratioMap.put(curGoldEntry.getKey(), curGoldEntry.getValue() / spValue);
                }
            }

            final LineChart goldSPChart = (LineChart) getActivity().findViewById(R.id.goldSPChart);
            ChartBuilder.build(color, goldSPChart, ratioMap, "Gold/S&P Ratio", new ChartMarkerView(getActivity()));

            progressView.setVisibility(View.INVISIBLE);
            contentView.setVisibility(View.VISIBLE);
        }
    }
}
