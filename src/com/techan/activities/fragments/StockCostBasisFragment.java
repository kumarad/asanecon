package com.techan.activities.fragments;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.techan.R;
import com.techan.custom.Util;
import com.techan.progressbar.SaundProgressBar;

public class StockCostBasisFragment extends Fragment {
    public static final String COST_VAL = "COST_VAL";
    public static final String BUY_DATE = "BUY_DATE";
    public static final String COUNT_VAL = "COUNT_VAL";
    public static final String CUR_PRICE = "CUR_PRICE";
    public static final String HIGH_PRICE = "HIGH_PRICE";
    public static final String SL_PERCENT = "SL_PERCENT";

    TextView warningView;

    TextView costBasisView;

    LinearLayout costBasisChangeRow;
    TextView costBasisChangeView;
    TextView costBasisChangeValView;

    LinearLayout costRow;
    TextView costValView;
    TextView countValView;

    LinearLayout buyDateRow;
    TextView buyDateValView;

    RelativeLayout stopLossView;

    SaundProgressBar regularProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_cost_basis, container, false);
        Bundle args = getArguments();

        warningView = (TextView)rootView.findViewById(R.id.costWarning);
        costBasisView = (TextView)rootView.findViewById(R.id.costBasisVal);
        costBasisChangeRow = (LinearLayout)rootView.findViewById(R.id.costBasisChangeRow);
        costBasisChangeView = (TextView)rootView.findViewById(R.id.costBasisChange);
        costBasisChangeValView = (TextView)rootView.findViewById(R.id.costBasisChangeVal);

        costRow = (LinearLayout)rootView.findViewById(R.id.costRow);
        costValView = (TextView) rootView.findViewById(R.id.detailCostVal);
        countValView = (TextView) rootView.findViewById(R.id.detailCountVal);

        buyDateRow = (LinearLayout)rootView.findViewById(R.id.buyDateRow);
        buyDateValView = (TextView)rootView.findViewById(R.id.buyDateVal);

        stopLossView = (RelativeLayout)rootView.findViewById(R.id.stopLossView);

        warningView.setText("Set cost basis.");

        regularProgressBar = (SaundProgressBar) rootView.findViewById(R.id.slprogressbar);
        Drawable indicator = getResources().getDrawable(R.drawable.progress_indicator_b2);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5, indicator.getIntrinsicHeight());
        indicator.setBounds(bounds);
        regularProgressBar.setProgressIndicator(indicator);

        update(args.getDouble(CUR_PRICE), args.getDouble(COST_VAL), args.getString(BUY_DATE), args.getInt(COUNT_VAL), args.getInt(SL_PERCENT), args.getDouble(HIGH_PRICE));

        return rootView;
    }

    public void update(final Double curPrice, final Double buyPrice, final String buyDate, final Integer stockCount, final Integer slPercent, final Double highPrice) {
        if(buyPrice != null && buyPrice != 0) {
            costValView.setText(Double.toString(buyPrice));
            buyDateValView.setText(Util.getDateFromDateTimeStr(buyDate));
            clearWarning();
        } else {
            setWarning();
            return;
        }

        if(stockCount != null && stockCount != 0) {
            countValView.setText(Integer.toString(stockCount));
            showCostBasisViews();
            updateCostBasis(curPrice, buyPrice, stockCount);
        } else {
            countValView.setText("Not set");
            hideCostBasisViews();
        }

        if(slPercent != null && slPercent != 0) {
            stopLossView.setVisibility(View.VISIBLE);
            int progress = 0;
            double low = highPrice - ((((double)slPercent)*highPrice)/100.00);
            if(curPrice > low && curPrice <= highPrice) {
                progress = (int)(((curPrice - low)*100.00)/(highPrice - low));
            } // else lower. Can't be higher!
            regularProgressBar.setValue("$" + Double.toString(curPrice), progress);
        } else {
            stopLossView.setVisibility(View.GONE);
        }
    }


    private void hideCostBasisViews() {
        costBasisView.setVisibility(View.GONE);
        costBasisChangeRow.setVisibility(View.GONE);
    }

    private void showCostBasisViews() {
        costBasisView.setVisibility(View.VISIBLE);
        costBasisChangeRow.setVisibility(View.VISIBLE);
    }

    private void updateCostBasis(double curPrice, double cost, int count) {
        double oldBasis = cost * count;
        double curBasis = curPrice * count;
        double change = curBasis - oldBasis;
        costBasisView.setText(Double.toString(Util.roundTwoDecimals(curBasis)));
        Util.showChange(costBasisChangeValView, change, oldBasis, costBasisChangeView);
    }

    private void setWarning() {
        warningView.setVisibility(View.VISIBLE);

        hideCostBasisViews();

        costRow.setVisibility(View.GONE);
//        costValView.setVisibility(View.GONE);
//        countValView.setVisibility(View.GONE);
        buyDateRow.setVisibility(View.GONE);
        stopLossView.setVisibility(View.GONE);
    }

    private void clearWarning() {
        warningView.setVisibility(View.GONE);

        costRow.setVisibility(View.VISIBLE);
//        costValView.setVisibility(View.VISIBLE);
//        countValView.setVisibility(View.VISIBLE);
        buyDateRow.setVisibility(View.VISIBLE);
        stopLossView.setVisibility(View.VISIBLE);
    }
}
