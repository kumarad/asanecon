package com.techan.custom;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.techan.R;
import com.techan.activities.SettingsActivity;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;

public class StockCursorAdapter extends SimpleCursorAdapter {

    Context context;
    Activity activity;

    public StockCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        this.context = context;
        this.activity = (Activity)context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(context, R.layout.stock_row, null);
        }

        Cursor c = getCursor();
        View row = convertView;
        c.moveToPosition(position);

        String symbolStr = c.getString(1);
        TextView symbolView = (TextView) convertView.findViewById(R.id.listSymbol);
        symbolView.setText(symbolStr);

        TextView priceView = (TextView) convertView.findViewById(R.id.listPrice);
        TextView changeView = (TextView) convertView.findViewById(R.id.listChange);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean showCostBasis = prefs.getBoolean(SettingsActivity.SHOW_COST_BASIS, false);

        Double price = c.getDouble(2);
        price = Util.roundTwoDecimals(price);
        Double change = c.getDouble(3);
        if(!showCostBasis) {
            priceView.setText(Double.toString(price));
            Util.setChange(changeView, change, price);
        } else {
            final SymbolProfile profile = ProfileManager.getSymbolData(context, symbolStr);
            if(profile != null && profile.buyPrice != null && profile.stockCount != null) {
                change = price - profile.buyPrice;
                priceView.setText(Double.toString(Util.roundTwoDecimals(price * profile.stockCount)));
                Util.setChange(changeView, change, profile.buyPrice);
            } else {
                change = null;
                priceView.setText("-");
                changeView.setText(null);
            }
        }

        if(change == null) {
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

        return row;
    }
}
