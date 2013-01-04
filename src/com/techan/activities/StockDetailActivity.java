package com.techan.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.techan.R;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;

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
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        stockUri = (Uri)extras.get(StockContentProvider.CONTENT_ITEM_TYPE);

        stockCursor = getContentResolver().query(stockUri, null, null, null, null);

        if(stockCursor.getCount() != 1) {
            throw new RuntimeException("Was not able to find details for stock.");
        }

        stockCursor.moveToFirst();
        TextView symbolView = (TextView) this.findViewById(R.id.detailNameSymbol);
        symbol = stockCursor.getString(1);
        String name = stockCursor.getString(12);
        symbolView.setText(name);
        symbolView.append(" ("+symbol+")");

        TextView priceView = (TextView) this.findViewById(R.id.detailPrice);
        priceView.setText("Price: ");
        priceView.append(Util.parseDouble(stockCursor, 2));

        TextView changeView = (TextView) this.findViewById(R.id.detailChange);
        changeView.setText("Change: ");
        changeView.append(Util.parseDouble(stockCursor, 11));

        TextView lowView = (TextView) this.findViewById(R.id.detailLow);
        lowView.setText("Low: ");
        lowView.append(Util.parseDouble(stockCursor, 3));

        TextView highView = (TextView) this.findViewById(R.id.detailHigh);
        highView.setText("High: ");
        highView.append(Util.parseDouble(stockCursor, 4));

        TextView peView = (TextView) this.findViewById(R.id.detailPe);
        peView.setText("PE: ");
        peView.append(Util.parseDouble(stockCursor, 5));

        TextView pegView = (TextView) this.findViewById(R.id.detailPeg);
        pegView.setText("PEG: ");
        pegView.append(Util.parseDouble(stockCursor, 6));

        TextView mov50View = (TextView) this.findViewById(R.id.detailMovAvg50);
        mov50View.setText("50d movAvg: ");
        mov50View.append(Util.parseDouble(stockCursor, 7));

        TextView mov200View = (TextView) this.findViewById(R.id.detailMovAvg200);
        mov200View.setText("200d movAvg: ");
        mov200View.append(Util.parseDouble(stockCursor, 8));

        TextView volView = (TextView) this.findViewById(R.id.detailTradingVol);
        volView.setText("Volume: ");
        double volDouble = stockCursor.getDouble(9);
        if(volDouble != 0) {
            volView.append(Long.toString((long) volDouble));
        } else {
            volView.append("N/A");
        }

        TextView avgVolView = (TextView) this.findViewById(R.id.detailAvgTradingVol);
        avgVolView.setText("Avg Volume: ");
        double avgVolDouble = stockCursor.getDouble(10);
        if(avgVolDouble != 0) {
            avgVolView.append(Long.toString((long) avgVolDouble));
        } else {
            avgVolView.append("N/A");
        }

        ProgressBar stopLoss = (ProgressBar) this.findViewById(R.id.stopLoss);
        Util.createBar(this, stopLoss, "#93d500", 35);

        ProgressBar stopLossR = (ProgressBar) this.findViewById(R.id.stopLossR);
        Util.createBar(this, stopLossR, "#E52B50", 10);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Are you sure you want to delete stock?");
                alertDialog.setMessage("Click yes to confirm deletion.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteStock();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                alertDialog.create().show();
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