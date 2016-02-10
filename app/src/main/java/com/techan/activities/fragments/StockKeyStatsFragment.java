package com.techan.activities.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.techan.R;
import com.techan.activities.BusService;
import com.techan.activities.dialogs.InfoDialog;
import com.techan.custom.Util;
import com.techan.memrepo.KeyStatsRepo;
import com.techan.stockDownload.KeyStatsDownloader;
import com.techan.stockDownload.StockKeyStats;

public class StockKeyStatsFragment extends Fragment {
    private String symbol;
    private double stockPrice;
    private View progressView;
    private View statsView;
    private View rootView;
    private LayoutInflater inflater;

    public void setSymbolAndPrice(String symbol, double stockPrice) {
        this.symbol = symbol;
        this.stockPrice = stockPrice;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.stock_key_stats, container, false);
        this.inflater = inflater;

        progressView = rootView.findViewById(R.id.stockKeyStatsProgress);
        statsView = rootView.findViewById(R.id.stockKeyStatsScrollView);

        StockKeyStats stats = KeyStatsRepo.getRepo().get(symbol);
        if(stats == null || !Util.isDateToday(stats.getTimestamp())) {
            // Lets get the updated value from our source
            progressView.setVisibility(View.VISIBLE);
            statsView.setVisibility(View.INVISIBLE);
            KeyStatsDownloader.download(symbol);
        } else {
            // else we already have what we need.
            showStats();
        }

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
    public void doneLoading(KeyStatsDownloader.KeyStatsDownloaderComplete event) {
        showStats();
    }

    private void showStats() {
        progressView.setVisibility(View.INVISIBLE);
        statsView.setVisibility(View.VISIBLE);

        StockKeyStats stats = KeyStatsRepo.getRepo().get(symbol);
        View undervaluedAlertView = rootView.findViewById(R.id.valuationAlert);
        View profitabilityAlertView = rootView.findViewById(R.id.profitabilityAlert);
        View mgmtAlertView = rootView.findViewById(R.id.mgmtEffectivenessAlert);
        View volatilityAlertView = rootView.findViewById(R.id.volatilityAlert);

        if(stats != null) {
            final TextView evView = (TextView) rootView.findViewById(R.id.keyStatsEnterpriseValue);
            evView.setText(Util.doubleToString(stats.getEnterpriseValueMultiple()));
            boolean evUnderValued = Util.setPositiveColor(stats.getEnterpriseValueMultiple(), 0.0, true, 10.0, true, evView);

            final TextView pegView = (TextView) rootView.findViewById(R.id.keyStatsPeg);
            pegView.setText(Util.doubleToString(stats.getPeg()));
            boolean pegUnderValued = Util.setPositiveColor(stats.getPeg(), 0.0, true, 1.0, false, pegView);

            final TextView bookValueView = (TextView) rootView.findViewById(R.id.keyStatsBookValue);
            bookValueView.setText(Util.doubleToString(stats.getBookValue()));
            boolean bookValueUnderValued = Util.setPositiveColor(stockPrice, 0.0, true, stats.getBookValue(), false, bookValueView);

            if(evUnderValued || pegUnderValued || bookValueUnderValued) {
                undervaluedAlertView.setVisibility(View.VISIBLE);
                InfoDialog.setOnClickInfoDialog(undervaluedAlertView, inflater, rootView.getResources().getString(R.string.undervalued), true);
            } else {
                undervaluedAlertView.setVisibility(View.INVISIBLE);
            }

            final TextView currentRatioView = (TextView) rootView.findViewById(R.id.keyStatsCurrentRatio);
            currentRatioView.setText(Util.doubleToString(stats.getCurrentRatio()));
            boolean currentRatioBad = Util.setNegativeColor(stats.getCurrentRatio(), 0.0, true, 1.0, false, currentRatioView);

            final TextView operatingMarginView = (TextView) rootView.findViewById(R.id.keyStatsOperatingMargin);
            operatingMarginView.setText(Util.doubleToString(stats.getOperatingMargin()));
            boolean operatingMarginBad = Util.setNegativeColor(stats.getOperatingMargin(), 0.0, true, 15.0, false, operatingMarginView);

            final TextView totalDebtToEquityView = (TextView) rootView.findViewById(R.id.keyStatsTotalDebtToEquity);
            totalDebtToEquityView.setText(Util.doubleToString(stats.getDebtToEquityRatio()));

            if(currentRatioBad || operatingMarginBad) {
                profitabilityAlertView.setVisibility(View.VISIBLE);
                InfoDialog.setOnClickInfoDialog(profitabilityAlertView, inflater, rootView.getResources().getString(R.string.profitability), true);
            } else {
                profitabilityAlertView.setVisibility(View.INVISIBLE);
            }

            final TextView roaView = (TextView) rootView.findViewById(R.id.keyStatsRoaValue);
            roaView.setText(Util.doubleToString(stats.getRoa()));
            boolean roaBad = Util.setNegativeColor(stats.getRoa(), 0.0, true, 5.0 , false, roaView);

            final TextView roeView = (TextView) rootView.findViewById(R.id.keyStatsRoeValue);
            roeView.setText(Util.doubleToString(stats.getRoe()));
            boolean roeBad = Util.setNegativeColor(stats.getRoe(), 0.0, true, 15, false, roeView);

            if(roaBad || roeBad) {
                mgmtAlertView.setVisibility(View.VISIBLE);
                InfoDialog.setOnClickInfoDialog(mgmtAlertView, inflater, rootView.getResources().getString(R.string.management_effectiveness), true);
            } else {
                mgmtAlertView.setVisibility(View.INVISIBLE);
            }

            final TextView betaView = (TextView) rootView.findViewById(R.id.keyStatsBeta);
            betaView.setText(Util.doubleToString(stats.getBeta()));
            if(Util.setPositiveColor(stats.getBeta(), 0.0, true, 1.0 , false, betaView)) {
                volatilityAlertView.setVisibility(View.VISIBLE);
                InfoDialog.setOnClickInfoDialog(volatilityAlertView, inflater, String.format(rootView.getResources().getString(R.string.volatility), Integer.toString((int) ((1.0 - stats.getBeta()) * 100))));
            } else {
                volatilityAlertView.setVisibility(View.INVISIBLE);
            }
        } else {
            undervaluedAlertView.setVisibility(View.INVISIBLE);
            profitabilityAlertView.setVisibility(View.INVISIBLE);
            mgmtAlertView.setVisibility(View.INVISIBLE);
            volatilityAlertView.setVisibility(View.INVISIBLE);
        }

    }
}
