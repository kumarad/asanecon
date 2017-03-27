package com.techan.custom;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.techan.R;
import com.techan.activities.fragments.StockCostBasisFragment;
import com.techan.contentProvider.StockContentProvider;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class StockCursorAdapter extends SimpleCursorAdapter {

    private Activity activity;

    private boolean showCostBasis = false;
    private boolean showStopLoss = false;


    public StockCursorAdapter(Activity activity,
                              int layout,
                              Cursor c,
                              String[] from,
                              int[] to,
                              int flags) {
        super(activity, layout, c, from, to, flags);

        this.activity = activity;
    }

    public void update(boolean showCostBasis, boolean showStopLoss) {
        this.showCostBasis = showCostBasis;
        this.showStopLoss = showStopLoss;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(activity, R.layout.stock_row, null);
        }

        Cursor c = getCursor();
        View row = convertView;
        c.moveToPosition(position);

        String symbolStr = c.getString(1);
        TextView symbolView = (TextView) convertView.findViewById(R.id.listSymbol);
        symbolView.setText(symbolStr);

        TextView priceView = (TextView) convertView.findViewById(R.id.listPrice);
        TextView changeView = (TextView) convertView.findViewById(R.id.listChange);

        View priceLayout = convertView.findViewById(R.id.stockRowPricePlusChangeView);
        View stopLossLayout = convertView.findViewById(R.id.stockRowStopLossChangeView);

        TextView stopLossVal = (TextView) convertView.findViewById(R.id.stockRowStopLossPercent);

        Double price = c.getDouble(2);
        price = Util.roundTwoDecimals(price);
        Double change = c.getDouble(3);
        if(!showCostBasis && !showStopLoss) {
            priceView.setText(Double.toString(price));
            Util.setChange(changeView, change, price);
        } else {
            final SymbolProfile profile = ProfileManager.getSymbolData(activity, symbolStr);
            if (showCostBasis) {
                if (profile != null && profile.buyPrice != null && profile.stockCount != null) {
                    change = price - profile.buyPrice;
                    priceView.setText(Double.toString(Util.roundTwoDecimals(price * profile.stockCount)));
                    Util.setChange(changeView, change, profile.buyPrice);
                } else {
                    change = null;
                    priceView.setText("-");
                    changeView.setText(null);
                }
            } else {
                if (profile.stopLossPercent != null) {
                    Cursor cursor = activity.getContentResolver().query(StockContentProvider.CONTENT_URI, null, StocksTable.COLUMN_SYMBOL + "='" + symbolStr + "'", null, null);
                    try {
                        if (cursor == null || cursor.getCount() != 1) {
                            // Not good. Just set showStopLoss to false so we show normal stuff
                            stopLossVal.setText("-");
                            stopLossVal.setTextColor(Color.GRAY);
                        } else {
                            cursor.moveToFirst();
                            double highPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_SL_HIGEST_PRICE));
                            double curPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE));
                            double lowPrice = StockCostBasisFragment.getLowPrice(highPrice, profile.stopLossPercent);

                            if(curPrice < lowPrice) {
                                // We have hit the stop loss
                                stopLossVal.setText("Sell");
                                stopLossVal.setTypeface(Typeface.DEFAULT_BOLD);
                                stopLossVal.setTextColor(convertView.getResources().getColor(R.color.asaneconRed));
                            } else {
                                double slVal = Util.roundTwoDecimals(((highPrice - curPrice) * 100)/highPrice);
                                stopLossVal.setText(Double.toString(slVal * -1));
                                if (slVal < profile.stopLossPercent/2) {
                                    stopLossVal.setTextColor(convertView.getResources().getColor(R.color.asaneconGreen));
                                } else {
                                    stopLossVal.setTextColor(convertView.getResources().getColor(R.color.asaneconRed));
                                }
                            }
                        }
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                } else {
                    stopLossVal.setText("-");
                    stopLossVal.setTextColor(Color.GRAY);
                }
            }
        }

        if (showStopLoss) {
            priceLayout.setVisibility(View.INVISIBLE);
            stopLossLayout.setVisibility(View.VISIBLE);
        } else {
            priceLayout.setVisibility(View.VISIBLE);
            stopLossLayout.setVisibility(View.INVISIBLE);

            if (change == null) {
                priceView.setTextColor(Color.GRAY);
                changeView.setTextColor(Color.GRAY);
            } else if (change < 0) {
                int red = convertView.getResources().getColor(R.color.asaneconRed);
                priceView.setTextColor(red);
                changeView.setTextColor(red);
            } else {
                int green = convertView.getResources().getColor(R.color.asaneconGreen);
                priceView.setTextColor(green);
                changeView.setTextColor(green);
            }
        }

        return row;
    }
}
