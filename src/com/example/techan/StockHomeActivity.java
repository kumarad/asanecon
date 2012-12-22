package com.example.techan;

import android.app.ListActivity;
import android.os.Bundle;

public class StockHomeActivity extends ListActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.stock_list);
    }
}
