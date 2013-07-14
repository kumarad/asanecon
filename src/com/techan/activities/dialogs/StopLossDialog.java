package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.techan.R;
import com.techan.activities.SettingsActivity;
import com.techan.activities.StockPagerAdapter;
import com.techan.custom.SwitchCheckRefreshListener;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.ContentValuesFactory;

public class StopLossDialog {
    public static void createError(AlertDialog.Builder alertDialog) {
        // Need buyPrice to set stop loss.
        alertDialog.setTitle("Set buy price first.");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Nada.
            }
        });

        alertDialog.create().show();
    }

    public static void create(final Activity parentActivity, String symbol, final Uri stockUri, final StockPagerAdapter stockPagerAdapter) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(parentActivity);
        alertDialog.setTitle("Trailing stop loss");

        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_stop_loss, null);

        // Get global notification preference
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(parentActivity);
        boolean globalNotifications = sharedPref.getBoolean(SettingsActivity.ALL_NOTIFICATIONS_KEY, false);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
        if(profile.buyPrice == null) {
            createError(alertDialog);
            return;
        }

        // Handle number picker.
        final NumberPicker np = ((NumberPicker)view.findViewById(R.id.stop_loss_np));
        np.setMaxValue(100);
        np.setMinValue(0);

        if(profile.stopLossPercent != null) {
            np.setValue(profile.stopLossPercent);
        } else {
            np.setValue(SettingsActivity.STOP_LOSS_DEFAULT);
        }

        // Handle price text.
        TextView buyPriceTextView = (TextView)view.findViewById(R.id.show_buy_price);
        buyPriceTextView.setText(Double.toString(profile.buyPrice));

        final Switch s = (Switch) view.findViewById(R.id.switch_sl_notify);
        if(globalNotifications && profile.stopLossPercent != null) {
            s.setChecked(true);
        } else {
            s.setChecked(false);
            np.setEnabled(false);
        }

        // Handle warning.
        final TextView warningText = (TextView)view.findViewById(R.id.sl_warning);
        warningText.setText("Enable auto refresh for timely notifications");
        warningText.setVisibility(View.GONE);

        final SwitchCheckRefreshListener listener = new SwitchCheckRefreshListener(np, globalNotifications, sharedPref.edit(), warningText, parentActivity, sharedPref, false);
        s.setOnCheckedChangeListener(listener);

        //Pass null as parent view because its a dialog.
        alertDialog.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doAdd(parentActivity, stockUri, profile, np, s, listener, stockPagerAdapter);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Ignore
                    }
                });


        alertDialog.create().show();
    }

    private static void doAdd(Activity parentActivity, Uri stockUri, SymbolProfile profile, NumberPicker np, Switch s, SwitchCheckRefreshListener listener, StockPagerAdapter stockPagerAdapter) {
        if(s.isChecked()) {
            Integer stopLossPercent = np.getValue();
            // Either way set highestPrice to buyPrice. So that highestPrices is tracked from when stop loss notifications are activated.
            // TODO let the user change the buy price for stop loss??
            // TODO let user specify trailing vs non trailing
            profile.setStopLossInfo(stopLossPercent, true);
        } else {
            profile.clearStopLossInfo();
        }

        // Update profile manager.
        ProfileManager.addSymbolData(profile);

        // Update db with stop loss information.
        ContentResolver cr = parentActivity.getContentResolver();
        Cursor cursor = cr.query(stockUri, null, null, null, null);
        cursor.moveToFirst();
        Double curPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE));
        ContentValues values = ContentValuesFactory.createSlAddValues(curPrice, profile.buyPrice);
        cr.update(stockUri, values, null, null);

        stockPagerAdapter.updateCostBasisFragment(profile.buyPrice, profile.stockCount, profile.stopLossPercent);

        listener.commit();
    }
}
