package com.techan.custom;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.techan.R;

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

        Double price = c.getDouble(2);
        TextView priceView = (TextView) convertView.findViewById(R.id.listPrice);
        priceView.setText(Double.toString(price));

        return row;
    }
}
