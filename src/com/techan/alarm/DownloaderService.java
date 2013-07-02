package com.techan.alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.techan.profile.Constants;
import com.techan.stockDownload.RefreshAllTask;

public class DownloaderService extends Service {

    private static long last = 0;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new RefreshAllTask(getApplicationContext(), this.getContentResolver(), true).download();

        long cur = System.currentTimeMillis()/1000;
        long delta = (cur - last);
        Log.d(Constants.LOG_TAG, "Delta " + Long.toString(delta));
        last = cur;

        // Will restart this service if it is killed by the system
        // for some reason.
        return START_FLAG_REDELIVERY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
