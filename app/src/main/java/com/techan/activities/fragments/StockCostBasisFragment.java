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
import com.techan.profile.SymbolProfile;
import com.techan.progressbar.SaundProgressBar;

public class StockCostBasisFragment extends Fragment {
    public static final String COST_VAL = "COST_VAL";
    public static final String SL_TRACKING_START_DATE = "SL_TRACKING_START_DATE";
    public static final String COUNT_VAL = "COUNT_VAL";
    public static final String CUR_PRICE = "CUR_PRICE";
    public static final String HIGH_PRICE = "HIGH_PRICE";
    public static final String SL_PERCENT = "SL_PERCENT";
    public static final String TARGET_PRICE = "TARGET_PRICE";
    public static final String TARGET_LESS_THAN_EQUAL = "TARGET_LESS_THAN_EQUAL";

    TextView warningView;

    TextView costBasisView;

    LinearLayout costBasisChangeRow;
    TextView costBasisChangeView;
    TextView costBasisChangeValView;

    LinearLayout costRow;
    TextView costValView;
    TextView countValView;

    View divView1;

    RelativeLayout stopLossView;
    TextView slDateValView;
    SaundProgressBar slProgressBar;

    View divView2;

    LinearLayout targetPriceView;
    TextView targetLowTextView;
    SaundProgressBar targetProgressBar;
    TextView targetHighTextView;

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

        divView1 = rootView.findViewById(R.id.cbDiv1);

        stopLossView = (RelativeLayout)rootView.findViewById(R.id.stopLossView);
        slDateValView = (TextView)rootView.findViewById(R.id.slDateVal);

        divView2 = rootView.findViewById(R.id.cbDiv2);

        targetPriceView = (LinearLayout) rootView.findViewById(R.id.targetPriceView);
        targetLowTextView = (TextView) rootView.findViewById(R.id.targetLowVal);
        targetProgressBar = (SaundProgressBar) rootView.findViewById(R.id.targetBar);
        targetHighTextView = (TextView) rootView.findViewById(R.id.targetHighVal);

        warningView.setText("Set cost basis.");

        slProgressBar = (SaundProgressBar) rootView.findViewById(R.id.slprogressbar);
        Drawable indicator = getResources().getDrawable(R.drawable.progress_indicator_b2);
        Rect bounds = new Rect(0, 0, indicator.getIntrinsicWidth() + 5, indicator.getIntrinsicHeight());
        indicator.setBounds(bounds);
        slProgressBar.setProgressIndicator(indicator);

        update(args.getDouble(CUR_PRICE),
                args.getDouble(COST_VAL),
                args.getString(SL_TRACKING_START_DATE),
                args.getInt(COUNT_VAL),
                args.getInt(SL_PERCENT),
                args.getDouble(HIGH_PRICE),
                args.getDouble(TARGET_PRICE),
                args.getBoolean(TARGET_LESS_THAN_EQUAL));

        return rootView;
    }

    public void update(final Double curPrice, Double highPrice, final SymbolProfile profile) {
        update(curPrice,
               profile.buyPrice,
               profile.slTrackingStartDate,
               profile.stockCount,
               profile.stopLossPercent,
               highPrice,
               profile.targetPrice,
               profile.lessThanEqual);
    }

    protected void update(final Double curPrice,
                       final Double buyPrice,
                       final String slTrackingStartDate,
                       final Integer stockCount,
                       final Integer slPercent,
                       final Double highPrice,
                       final Double targetPrice,
                       final Boolean lessThanEqual) {
        boolean warningSet = false;
        if((buyPrice != null && buyPrice != 0)) {
            costValView.setText(Double.toString(buyPrice));
            clearWarning();
        } else {
            setWarning();
            warningSet = true;
        }

        if(stockCount != null && stockCount != 0) {
            countValView.setText(Integer.toString(stockCount));
            showCostBasisViews();
            updateCostBasis(curPrice, buyPrice, stockCount);
        } else {
            countValView.setText("Not set");
            hideCostBasisViews();
        }

        handleStopLossView(slTrackingStartDate, curPrice, slPercent, highPrice);
        handleTargetPricing(warningSet, curPrice, targetPrice, lessThanEqual);
    }

    protected void handleStopLossView(String slTrackingStartDate, Double curPrice, Integer slPercent, Double highPrice) {
        if(slPercent != null && slPercent != 0) {
            stopLossView.setVisibility(View.VISIBLE);
            divView1.setVisibility(View.VISIBLE);
            int progress = 0;
            double low = highPrice - ((((double)slPercent)*highPrice)/100.00);
            if(curPrice > low && curPrice <= highPrice) {
                progress = (int)(((curPrice - low)*100.00)/(highPrice - low));
            } // else lower. Can't be higher!
            slProgressBar.setValue("$" + Double.toString(curPrice), progress);

            slDateValView.setText(Util.getDateFromDateTimeStr(slTrackingStartDate));
        } else {
            stopLossView.setVisibility(View.GONE);
            divView1.setVisibility(View.GONE);
        }
    }

    protected void handleTargetPricing(boolean warningSet, Double curPrice, Double targetPrice, Boolean lessThanEqual) {
        if(targetPrice != null && targetPrice != 0) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) divView2.getLayoutParams();
            if(warningSet) {
                params.addRule(RelativeLayout.BELOW, R.id.costWarning);
            } else {
                params.addRule(RelativeLayout.BELOW, R.id.stopLossView);
            }
            divView2.setLayoutParams(params);

            divView2.setVisibility(View.VISIBLE);
            targetPriceView.setVisibility(View.VISIBLE);
            if(lessThanEqual) {
                targetLowTextView.setVisibility(View.VISIBLE);
                targetLowTextView.setText(Double.toString(targetPrice));
                targetHighTextView.setVisibility(View.GONE);
                if(curPrice <= targetPrice) {
                    targetProgressBar.setProgress(100);
                    targetProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progressbar));
                } else {
                    double factor = 100 - (targetPrice/curPrice) * 100;
                    targetProgressBar.setProgress((int)factor);
                    targetProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.invertedblue_progressbar));
                }
            } else {
                targetLowTextView.setVisibility(View.GONE);
                targetHighTextView.setVisibility(View.VISIBLE);
                targetHighTextView.setText(Double.toString(targetPrice));
                if(curPrice >= targetPrice) {
                    targetProgressBar.setProgress(100);
                    targetProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progressbar));
                } else {
                    double factor = (curPrice/targetPrice) * 100;
                    targetProgressBar.setProgress((int)factor);
                    targetProgressBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progressbar));
                }

            }
        } else {
            divView2.setVisibility(View.GONE);
            targetPriceView.setVisibility(View.GONE);
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
        stopLossView.setVisibility(View.GONE);

        divView1.setVisibility(View.GONE);
    }

    private void clearWarning() {
        warningView.setVisibility(View.GONE);

        costRow.setVisibility(View.VISIBLE);
        stopLossView.setVisibility(View.VISIBLE);

        divView1.setVisibility(View.VISIBLE);
    }
}
