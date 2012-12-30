package com.techan;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.techan.database.StocksTable;
import com.techan.stockDownload.QuoteDownloadTask;

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

        String symbolStr = c.getString(StocksTable.COLUMN_SYMBOL_INDEX);
        TextView symbolView = (TextView) convertView.findViewById(R.id.symbol);
        symbolView.setText(symbolStr);

        Double price = c.getDouble(StocksTable.COLUMN_PRICE_INDEX);
        TextView priceView = (TextView) convertView.findViewById(R.id.price);
        priceView.setText(Double.toString(price));

        return row;
    }
}
