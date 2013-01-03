package com.techan.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.techan.R;
import com.techan.contentProvider.StockContentProvider;
import com.techan.database.StocksTable;

public class StockDetailActivity extends Activity {

    private Uri stockUri;
    private Cursor stockCursor;
    private String symbol;

    // Bundle passed into onCreate represents saved state
    // for situations where the activity is being restored
    // from being paused for example.
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stock_detail);

        Bundle extras = getIntent().getExtras();
        stockUri = (Uri)extras.get(StockContentProvider.CONTENT_ITEM_TYPE);

        String[] projection = {StocksTable.COLUMN_ID, StocksTable.COLUMN_SYMBOL, StocksTable.COLUMN_PRICE, StocksTable.COLUMN_PE};
        stockCursor = getContentResolver().query(stockUri, projection, null, null, null);

        if(stockCursor.getCount() != 1) {
            throw new RuntimeException("Was not able to find details for stock.");
        }

        stockCursor.moveToFirst();
        TextView symbolView = (TextView) this.findViewById(R.id.detailSymbol);
        symbol = stockCursor.getString(StocksTable.COLUMN_SYMBOL_INDEX);
        symbolView.setText(symbol);

        TextView priceView = (TextView) this.findViewById(R.id.detailPrice);
        priceView.setText("Price: ");
        priceView.append(stockCursor.getString(StocksTable.COLUMN_PRICE_INDEX));

        TextView peView = (TextView) this.findViewById(R.id.detailPe);
        peView.setText("PE: ");
        peView.append(stockCursor.getString(StocksTable.COLUMN_PE_INDEX));


        ProgressBar stopLoss = (ProgressBar) this.findViewById(R.id.stopLoss);

        // Define a shape with rounded corners
        final float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(roundedCorners, null, null));

        // Sets the progressBar color
        pgDrawable.getPaint().setColor(Color.parseColor("#93d500"));

        // Adds the drawable to your progressBar
        ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        stopLoss.setProgressDrawable(progress);
        stopLoss.setBackground(getResources().getDrawable(android.R.drawable.progress_horizontal));
        stopLoss.setProgress(10);



        ProgressBar stopLossR = (ProgressBar) this.findViewById(R.id.stopLossR);
        // Define a shape with rounded corners
        final float[] roundedCornersR = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
        ShapeDrawable pgDrawableR = new ShapeDrawable(new RoundRectShape(roundedCornersR, null, null));

        // Sets the progressBar color
        pgDrawableR.getPaint().setColor(Color.parseColor("#E52B50"));

        // Adds the drawable to your progressBar
        ClipDrawable progressR = new ClipDrawable(pgDrawableR, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        stopLossR.setProgressDrawable(progressR);
        stopLossR.setBackground(getResources().getDrawable(android.R.drawable.progress_horizontal));
        stopLossR.setProgress(10);


    }

    /////////////////////////////////////////////////////////////////////
    // Menu on top right that allows deletion of item.
    /////////////////////////////////////////////////////////////////////
    private static final int ACTIVITY_CREATE = 0;

    // Create the menu based on the XML defintion
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailmenu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteStock();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteStock() {
        getContentResolver().delete(stockUri, null, null);

        Intent i = new Intent(this, StockHomeActivity.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

}
