package com.techan.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techan.R;

public class StockCostBasisFragment extends Fragment {
    public static final String COST_VAL = "COST_VAL";
    public static final String COUNT_VAL = "COUNT_VAL";

    TextView warningView;
    TextView costView;
    TextView costValView;
    TextView countView;
    TextView countValView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_cost_basis, container, false);
        Bundle args = getArguments();

        warningView = (TextView)rootView.findViewById(R.id.costWarning);
        costView = (TextView) rootView.findViewById(R.id.detailCost);
        costValView = (TextView) rootView.findViewById(R.id.detailCostVal);
        countView = (TextView) rootView.findViewById(R.id.detailCount);
        countValView = (TextView) rootView.findViewById(R.id.detailCountVal);

        warningView.setText("Set cost basis.");
        costView.setText("Cost: ");
        countView.setText("Count: ");

        Double costVal = args.getDouble(COST_VAL);
        if(costVal == 0) {
            setWarning();
            return rootView;
        } else {
            clearWarning();
        }

        costValView.setText(Double.toString(costVal));

        Integer countVal = args.getInt(COUNT_VAL);
        if(countVal != 0)
            countValView.setText(Integer.toString(countVal));
        else
            countValView.setText("Not set");

        return rootView;
    }

    private void setWarning() {
        warningView.setVisibility(View.VISIBLE);
        costView.setVisibility(View.GONE);
        costValView.setVisibility(View.GONE);
        countView.setVisibility(View.GONE);
        countValView.setVisibility(View.GONE);
    }

    private void clearWarning() {
        warningView.setVisibility(View.GONE);
        costView.setVisibility(View.VISIBLE);
        costValView.setVisibility(View.VISIBLE);
        countView.setVisibility(View.VISIBLE);
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
        } else {
            countValView.setText("Not set");
        }
    }
}
