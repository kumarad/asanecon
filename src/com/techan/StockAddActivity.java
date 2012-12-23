package com.techan;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.techan.contentProvider.StockContentProvider;
import com.techan.database.StocksTable;

public class StockAddActivity extends Activity {

    private EditText addText;
    private String CURRENT_TEXT = "CURRENT_TEXT";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stock_add);

        addText = (EditText) findViewById(R.id.stock_add);
        String currentText = (bundle == null) ? null : bundle.getString(CURRENT_TEXT);
        addText.setText(currentText);

        Button confirmButton = (Button) findViewById(R.id.stock_add_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(addText.getText().toString())) {
                    showErrorToast();
                } else {
                    // RESULT_OK is negative. Returning a negative value is the same as calling
                    // startActivity on the calling activity.
                    setResult(RESULT_OK);

                    // will call onPause or onSaveInstanceState which will end up saving information
                    // in database.
                    finish();
                }
            }
        });
    }

    // Provides feedback in a small pop up black window.
    private void showErrorToast() {
        Toast.makeText(StockAddActivity.this, "Please insert stock symbol to be added.", Toast.LENGTH_LONG).show();
    }

    // Called when the activity is no longer the primary activity. Android can
    // kill this activity if running low on memory. So should persist any data
    // that shouldn't be lost at this point.
    @Override
    protected void onPause() {
        super.onPause();
        addSymbol();
    }

    // Called before the activity is put in a background state. Save stuff in the bundle.
    // When the activity comes back to the foreground it is passed to onCreate to help recreate
    // the state of the activity.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_TEXT, addText.getText().toString());
    }

    private Uri addSymbol() {
        String symbol = addText.getText().toString();
        if(symbol.length() == 0) {
            // Should never happen.
            throw new RuntimeException("Should never have a null symbol string.");
        }

        ContentValues values = new ContentValues();
        values.put(StocksTable.COLUMN_SYMBOL, symbol);
        return getContentResolver().insert(StockContentProvider.CONTENT_URI, values);
    }
}
