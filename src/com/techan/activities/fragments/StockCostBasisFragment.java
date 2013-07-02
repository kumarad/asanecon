package com.techan.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techan.R;
import com.techan.custom.Util;

public class StockCostBasisFragment extends Fragment {
    public static final String COST_VAL = "COST_VAL";
    public static final String COUNT_VAL = "COUNT_VAL";
    public static final String CUR_PRICE = "CUR_PRICE";

    TextView warningView;
    TextView costBasisView;
    LinearLayout costBasisChangeLayout;
    TextView costBasisChangeView;
    TextView costBasisChangeValView;
    LinearLayout costRow;
    TextView costValView;
    TextView countValView;

    private double curPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //todo check to see if auto refresh has been disabled - impacts stop loss!!
        View rootView = inflater.inflate(R.layout.stock_cost_basis, container, false);
        Bundle args = getArguments();

        curPrice = args.getDouble(CUR_PRICE);

        warningView = (TextView)rootView.findViewById(R.id.costWarning);
        costBasisView = (TextView)rootView.findViewById(R.id.costBasisVal);
        costBasisChangeLayout = (LinearLayout)rootView.findViewById(R.id.costBasisChangeRow);
        costBasisChangeView = (TextView)rootView.findViewById(R.id.costBasisChange);
        costBasisChangeValView = (TextView)rootView.findViewById(R.id.costBasisChangeVal);

        costRow = (LinearLayout)rootView.findViewById(R.id.costRow);
        costValView = (TextView) rootView.findViewById(R.id.detailCostVal);
        countValView = (TextView) rootView.findViewById(R.id.detailCountVal);

        warningView.setText("Set cost basis.");

        Double costVal = args.getDouble(COST_VAL);
        if(costVal == 0) {
            setWarning();
            return rootView;
        } else {
            clearWarning();
        }

        costValView.setText(Double.toString(costVal));

        Integer countVal = args.getInt(COUNT_VAL);
        if(countVal != 0) {
            countValView.setText(Integer.toString(countVal));
            showCostBasis(costVal, countVal);
        } else {
            countValView.setText("Not set");
            hideCostBasisViews();
        }

        return rootView;
    }

    private void hideCostBasisViews() {
        costBasisView.setVisibility(View.GONE);
        costBasisChangeLayout.setVisibility(View.GONE);
    }

    private void showCostBasisViews() {
        costBasisView.setVisibility(View.VISIBLE);
        costBasisChangeLayout.setVisibility(View.VISIBLE);
    }

    private void showCostBasis(double cost, int count) {
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
        costValView.setVisibility(View.GONE);
        countValView.setVisibility(View.GONE);
    }

    private void clearWarning() {
        warningView.setVisibility(View.GONE);

        costRow.setVisibility(View.VISIBLE);
        costValView.setVisibility(View.VISIBLE);
        countValView.setVisibility(View.VISIBLE);
    }

    public void update(final Double buyPrice, final Integer stockCount) {
        if(buyPrice != null) {
            costValView.setText(Double.toString(buyPrice));
            clearWarning();
        } else {
            setWarning();
            return;
        }

        if(stockCount != null) {
            countValView.setText(Integer.toString(stockCount));
            showCostBasisViews();
            showCostBasis(buyPrice, stockCount);
        } else {
            countValView.setText("Not set");
            hideCostBasisViews();
        }
    }
}
