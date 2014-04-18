package com.techan.stockDownload.actions;

import com.techan.activities.StockPagerAdapter;
import com.techan.profile.SymbolProfile;

public class CostBasisPostRefreshAction implements PostRefreshAction {
    private final StockPagerAdapter stockPagerAdapter;
    private final SymbolProfile profile;

    public CostBasisPostRefreshAction(StockPagerAdapter stockPagerAdapter, SymbolProfile profile) {
        this.stockPagerAdapter = stockPagerAdapter;
        this.profile = profile;
    }
    @Override
    public void execute() {
        stockPagerAdapter.updateCostBasisFragment(profile.buyPrice,
                                                  profile.slTrackingStartDate,
                                                  profile.stockCount,
                                                  profile.stopLossPercent,
                                                  profile.targetPrice,
                                                  profile.lessThanEqual);
    }
}
