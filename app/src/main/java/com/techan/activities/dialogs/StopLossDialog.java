package com.techan.activities.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;

import com.techan.R;
import com.techan.activities.SettingsActivity;
import com.techan.activities.StockPagerAdapter;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.ContentValuesFactory;
import com.techan.stockDownload.RefreshTask;
import com.techan.stockDownload.actions.CostBasisPostRefreshAction;

import java.util.Calendar;

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

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
        if(profile.buyPrice == null) {
            createError(alertDialog);
            return;
        }

        // Handle number picker.
        final NumberPicker np = ((NumberPicker)view.findViewById(R.id.stop_loss_np));
        np.setMaxValue(100);
        np.setMinValue(1);

        if(profile.stopLossPercent != null) {
            np.setValue(profile.stopLossPercent);
        } else {
            np.setValue(SettingsActivity.STOP_LOSS_DEFAULT);
        }

        // Handle date picker
        final DatePicker datePicker = (DatePicker) view.findViewById(R.id.slDatePicker);
        datePicker.setCalendarViewShown(false);
        setSlTrackingStartDateOnView(datePicker, profile.slTrackingStartDate);

        //Pass null as parent view because its a dialog.
        alertDialog.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doAdd(parentActivity, stockUri, profile, np, datePicker, stockPagerAdapter);
                    }
                })
                .setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        doClear(profile, stockPagerAdapter);
                    }
                });


        alertDialog.create().show();
    }

    private static void setSlTrackingStartDateOnView(DatePicker dp, String buyDate) {
        Calendar c;
        if(buyDate == null) {
            c = Calendar.getInstance();
        } else {
            c = Util.getCal(buyDate);
        }

        // set current date into datepicker
        dp.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), null);
    }


    private static void doAdd(Activity parentActivity, Uri stockUri, SymbolProfile profile, NumberPicker np, DatePicker datePicker, StockPagerAdapter stockPagerAdapter) {
        Integer stopLossPercent = np.getValue();
        // Either way set highestPrice to buyPrice. So that highestPrices is tracked from when stop loss notifications are activated.
        // TODO let user specify trailing vs non trailing
        String curPickerDate = Util.getCalStr(datePicker.getYear(), datePicker.getMonth() + 1, datePicker.getDayOfMonth());
        profile.setStopLossInfo(stopLossPercent, true, curPickerDate);

        // Update profile manager.
        ProfileManager.addSymbolData(profile);

        // Update db with stop loss information.
        ContentResolver cr = parentActivity.getContentResolver();
        Cursor cursor = cr.query(stockUri, null, null, null, null);
        cursor.moveToFirst();
        Double curPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE));

        if(Util.isDateLess(Util.getCal(profile.slTrackingStartDate), Util.getCurCalWithZeroTime())) {
            // Date picked is before current date. Add it to the db and have refresh task figure out current stop loss state.
            ContentValues values = ContentValuesFactory.createSlAddValuesDiffDate(profile.buyPrice, profile.slTrackingStartDate);
            cr.update(stockUri, values, null, null);

            // Need to refresh historical data for this stock.
            RefreshTask rt = new RefreshTask(parentActivity, parentActivity.getContentResolver(), stockUri, profile.symbol, false);
            rt.addAction(new CostBasisPostRefreshAction(stockPagerAdapter, profile));
            rt.download();
        } else {
            // Start tracking stop loss from todays date.
            ContentValues values = ContentValuesFactory.createSlAddValuesSameDate(curPrice, profile.buyPrice);
            cr.update(stockUri, values, null, null);

            stockPagerAdapter.updateCostBasisFragment(profile);
        }
    }

    private static void doClear(SymbolProfile profile, StockPagerAdapter stockPagerAdapter) {
        profile.clearStopLossInfo();
        ProfileManager.addSymbolData(profile);
        stockPagerAdapter.updateCostBasisFragment(profile);
    }

}
