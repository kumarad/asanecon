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
import com.techan.custom.EventedAlertDialog;
import com.techan.custom.Util;
import com.techan.database.StocksTable;
import com.techan.profile.ProfileManager;
import com.techan.profile.SymbolProfile;
import com.techan.stockDownload.ContentValuesFactory;
import com.techan.stockDownload.DownloadTrendAndStopLossInfo;
import com.techan.stockDownload.actions.CostBasisPostRefreshAction;

import java.util.Calendar;

public class StopLossDialog {
    public static void createError(Activity parentActivity) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(parentActivity);
        alertDialogBuilder.setTitle("Trailing stop loss");

        // Need buyPrice to set stop loss.
        alertDialogBuilder.setTitle("Set buy price first.");
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Nada.
            }
        });

        alertDialogBuilder.create().show();
    }

    public static void create(final Activity parentActivity, String symbol, final Uri stockUri, final StockPagerAdapter stockPagerAdapter) {
        // Get layout inflater
        LayoutInflater inflater = parentActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.set_stop_loss, null);
        final View progressView = view.findViewById(R.id.stopLossDialogProgressView);
        final View contentView = view.findViewById(R.id.stopLossDialogContentView);

        progressView.setVisibility(View.INVISIBLE);

        final SymbolProfile profile = ProfileManager.getSymbolData(parentActivity.getApplicationContext(), symbol);
        if(profile.buyPrice == null) {
            createError(parentActivity);
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

        final EventedAlertDialog dialog = new EventedAlertDialog(parentActivity);

        //Pass null as parent view because its a dialog.
        dialog.setView(view);
        dialog.setTitle("Trailing stop loss");

        view.findViewById(R.id.stopLossDialogStartTracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contentView.animate().translationY(contentView.getHeight());
                progressView.setVisibility(View.VISIBLE);
                doAdd(parentActivity, stockUri, profile, np, datePicker, stockPagerAdapter, dialog);
            }
        });

        view.findViewById(R.id.stopLossDialogStopTracking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                doClear(profile, stockPagerAdapter);
            }
        });

        dialog.show();
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


    private static void doAdd(Activity parentActivity,
                              Uri stockUri,
                              SymbolProfile profile,
                              NumberPicker np,
                              DatePicker datePicker,
                              StockPagerAdapter stockPagerAdapter,
                              EventedAlertDialog dialog) {
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
        Double curPrice = null;
        if(cursor != null) {
            try {
                cursor.moveToFirst();
                curPrice = cursor.getDouble(StocksTable.stockColumns.get(StocksTable.COLUMN_PRICE));
            } finally {
                cursor.close();
            }
        }

        if(curPrice == null) {
            return;
        }

        if(Util.isDateLess(Util.getCal(profile.slTrackingStartDate), Util.getCurCalWithZeroTime())) {
            // Date picked is before current date. Add it to the db and have refresh task figure out current stop loss state.
            ContentValues values = ContentValuesFactory.createSlAddValuesDiffDate(profile.buyPrice, profile.slTrackingStartDate);
            cr.update(stockUri, values, null, null);

            dialog.addAction(new CostBasisPostRefreshAction(stockPagerAdapter, profile));
            new DownloadTrendAndStopLossInfo(profile.symbol, parentActivity, parentActivity.getContentResolver(), stockUri);
        } else {
            // Start tracking stop loss from todays date.
            ContentValues values = ContentValuesFactory.createSlAddValuesSameDate(curPrice, profile.buyPrice);
            cr.update(stockUri, values, null, null);
            stockPagerAdapter.updateCostBasisFragment(profile);
            dialog.dismiss();
        }
    }

    private static void doClear(SymbolProfile profile, StockPagerAdapter stockPagerAdapter) {
        profile.clearStopLossInfo();
        ProfileManager.addSymbolData(profile);
        stockPagerAdapter.updateCostBasisFragment(profile);
    }

}
