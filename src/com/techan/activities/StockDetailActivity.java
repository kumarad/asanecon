package com.techan.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
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
        populateGeneralView();
        populatePEView();
        populateVolumeView();
        populateMovingAvgView();
        populateTrends();
    }

    void populateGeneralView() {
        TextView symbolView = (TextView) this.findViewById(R.id.detailNameSymbol);
        symbol = stockCursor.getString(1);
        String name = stockCursor.getString(15);
        symbolView.setText(name);
        symbolView.append(" ("+symbol+")");

        TextView priceValView = (TextView) this.findViewById(R.id.detailPriceVal);
        priceValView.setText(Util.parseDouble(stockCursor, 2));

        TextView changeView = (TextView) this.findViewById(R.id.detailPriceChange);
        double change = stockCursor.getDouble(11);
        if(change < 0) {
            changeView.setTextColor(Color.RED);
        } else if(change > 0) {
            changeView.setTextColor(Color.GREEN);
        }
        changeView.setText("(");
        changeView.append(Double.toString(change));
        changeView.append(")");

        TextView lowView = (TextView) this.findViewById(R.id.detailLow);
        lowView.setText("Low: ");
        TextView lowValView = (TextView) this.findViewById(R.id.detailLowVal);
        lowValView.setText(Util.parseDouble(stockCursor, 3));

        TextView highView = (TextView) this.findViewById(R.id.detailHigh);
        highView.setText("High: ");
        TextView highValView = (TextView) this.findViewById(R.id.detailHighVal);
        highValView.setText(Util.parseDouble(stockCursor, 4));
    }

    void populatePEView() {
        TextView peView = (TextView) this.findViewById(R.id.detailPe);
        peView.setText("PE: ");
        TextView peValView = (TextView) this.findViewById(R.id.detailPeVal);
        peValView.setText(Util.parseDouble(stockCursor, 5));

//        ProgressBar stopLoss = (ProgressBar) this.findViewById(R.id.stopLoss);
//        Util.createBar(this, stopLoss, "#93d500", 35);
//
        ProgressBar stopLossR = (ProgressBar) this.findViewById(R.id.stopLoss);
        Util.createBar(this, stopLossR, "#E52B50", 10);

        TextView pegView = (TextView) this.findViewById(R.id.detailPeg);
        pegView.setText("PEG: ");
        TextView pegValView = (TextView) this.findViewById(R.id.detailPegVal);
        pegValView.setText(Util.parseDouble(stockCursor, 6));

    }

    void populateVolumeView() {
        TextView volView = (TextView) this.findViewById(R.id.detailVol);
        volView.setText("Volume: ");
        TextView volValView = (TextView) this.findViewById(R.id.detailVolVal);
        double volDouble = stockCursor.getDouble(9);
        if(volDouble != 0) {
            volValView.setText(Long.toString((long) volDouble));
        } else {
            volValView.setText("N/A");
        }

        TextView avgVolView = (TextView) this.findViewById(R.id.detailAvgVol);
        avgVolView.setText("Avg Volume: ");
        TextView avgVolValView = (TextView) this.findViewById(R.id.detailAvgVolVal);
        double avgVolDouble = stockCursor.getDouble(10);
        if(avgVolDouble != 0) {
            avgVolValView.setText(Long.toString((long) avgVolDouble));
        } else {
            avgVolValView.setText("N/A");
        }
    }

    void populateMovingAvgView() {
        TextView mov50View = (TextView) this.findViewById(R.id.detailMovAvg50);
        mov50View.setText("50d movAvg: ");
        TextView mov50ValView = (TextView) this.findViewById(R.id.detailMovAvg50Val);
        mov50ValView.setText(Util.parseDouble(stockCursor, 7));

        TextView mov200View = (TextView) this.findViewById(R.id.detailMovAvg200);
        mov200View.setText("200d movAvg: ");
        TextView mov200ValView = (TextView) this.findViewById(R.id.detailMovAvg200Val);
        mov200ValView.append(Util.parseDouble(stockCursor, 8));
    }

    void populateTrends() {
        TextView upTrendCountView = (TextView) this.findViewById(R.id.upTrendCount);
        upTrendCountView.setText("UpTrendDayCount: ");
        TextView upTrendCountValView = (TextView) this.findViewById(R.id.upTrendCountVal);
        upTrendCountValView.setText(Util.parseDouble(stockCursor,12));

        TextView high60DayView = (TextView) this.findViewById(R.id.high60Day);
        high60DayView.setText("High(60): ");
        TextView high60DayValView = (TextView) this.findViewById(R.id.high60DayVal);
        high60DayValView.setText(Util.parseDouble(stockCursor,13));

        TextView low90DayView = (TextView) this.findViewById(R.id.low90Day);
        low90DayView.setText("Low(90): ");
        TextView low90DayValView = (TextView) this.findViewById(R.id.low90DayVal);
        low90DayValView.setText(Util.parseDouble(stockCursor,14));
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
