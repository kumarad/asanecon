package com.techan.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.techan.R;
import com.techan.contentProvider.StockContentProvider;
import com.techan.custom.Util;
import com.techan.profile.JSONManager;
import com.techan.database.StocksTable;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.DownloadNewSymbolTask;

import java.util.List;

public class StockAddActivity extends Activity {

    private EditText addText;
    private String CURRENT_TEXT = "CURRENT_TEXT";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stock_add);

        addText = (EditText) findViewById(R.id.stock_add);
        String currentText = (bundle == null) ? null : bundle.getString(CURRENT_TEXT);
        if(currentText != null)
            addText.setText(currentText);

        Button confirmButton = (Button) findViewById(R.id.stock_add_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(addText.getText().toString())) {
                    //todo confirm that it is a valid symbol here ?
                    Util.showErrorToast(StockAddActivity.this, "Please insert stock symbol to be added.");
                } else {
                    addSymbol();

                    // RESULT_OK is negative. Returning a negative value is the same as calling
                    // startActivity on the calling activity.
                    setResult(RESULT_OK);

                    // will call onDestroy which will end up saving information
                    // in database.
                    finish();
                }
            }
        });
    }


    // Called when the activity is no longer the primary activity. Android can
    // kill this activity if running low on memory. So should persist any data
    // that shouldn't be lost at this point.
//    @Override
//    protected void onPause() {
//        super.onPause();
//        addSymbol();
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }


    // Called before the activity is put in a background state. Save stuff in the bundle.
    // When the activity comes back to the foreground it is passed to onCreate to help recreate
    // the state of the activity.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_TEXT, addText.getText().toString());
    }

    private void addSymbol() {
        String symbol = addText.getText().toString().toUpperCase();
        if(symbol.length() == 0) {
            Util.showErrorToast(this, "Nothing input.");
            return;
        }

        if(!symbol.matches("[\\.a-zA-Z0-9]+")) {
            Util.showErrorToast(this, "Stock symbol can only contain letters and periods.");
            return;
        }

        // Check to see if the symbol is already in the database. Can't have duplicates.
        String[] projection = {StocksTable.COLUMN_ID};
        Cursor cursor = getContentResolver().query(StockContentProvider.CONTENT_URI, projection, StocksTable.COLUMN_SYMBOL + "='" + symbol + "'", null, null);
        if(cursor.getCount() != 0) {
            // Already in cursor.
            Util.showErrorToast(this, "Stock symbol already added.");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SYMBOL, symbol);
        Uri addedUri = getContentResolver().insert(StockContentProvider.CONTENT_URI, values);
        Uri uri = Uri.parse(StockContentProvider.BASE_URI_STR + addedUri);
        (new DownloadNewSymbolTask(this.getContentResolver(), uri, symbol)).execute();

        if(!JSONManager.addSymbol(this.getApplicationContext(), symbol)) {
            // Failure adding symbol to persistent file. Let user know.
            Util.showErrorToast(this, "Oops. Something on your device prevented profile from being updated.");
        }
    }

    private void testJSONManager(String symbol) {
        SymbolProfile symProfile = new SymbolProfile(symbol);
        if(symbol.equals("IBM")) {
            symProfile.stopLossPercent = 25;
        } else if(symbol.equals("MSFT")) {
            symProfile.stopLossPercent = 10;
            symProfile.stopLossPivot = 400.12;
        }

        JSONManager.addSymbolData(this.getApplicationContext(), symProfile);

        List<SymbolProfile> profiles = JSONManager.getSymbols(this.getApplicationContext());

        profiles.clear();
    }
}
