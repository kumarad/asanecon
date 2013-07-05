package com.techan.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.techan.activities.SettingsActivity;
import com.techan.profile.Constants;

//:~/Installs/android-studio/sdk/platform-tools$ ./adb shell am broadcast -a android.intent.action.BOOT_COMPLETED
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d(Constants.LOG_TAG, "Boot Completed.");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            // Ensure auto refresh is enabled for the case this is a reboot.
            if(!sharedPreferences.getBoolean(SettingsActivity.AUTO_REFRESH_KEY, false)) {
                return;
            }
        }
        Intent downloader = new Intent(context, DownloaderService.class);
        context.startService(downloader);
    }

    public static void setAutoRefresh(Context context, int refreshValueHrs) {
        // Intent describes an action to be performed.
        Intent downloader = new Intent(context, AlarmReceiver.class);
        // A PendingIntent describes an intent and the target action to perform with it.
        // 0 is a requestCode that isnt really used by the current API.
        // downloader represents the intent to create.
        // FLAG_CANCEL_CURRENT will cancel a pending intent if one exists.
        // This intent knows to invoke onReceive on the intent!
        PendingIntent recurringDownloadIntent = PendingIntent.getBroadcast(context, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);

        // Get android alarm manager.
        AlarmManager alarms = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        // Create an alarm. RTC_WAKUP tells the alarm to go off the defined interval from current time. Currently set to every 5 minutes.
        //todo use refreshValueHrs * 60 * 60 * 1000 eventually.
        // refreshValueHrs * 60 (minutes) * 60 (seconds) * 1000 (ms)
        int interval = refreshValueHrs * 60 * 60 * 1000;
//        int interval;
//        interval = 1000 * 30 * 1;
//        if(refreshValueHrs == 1) {
//            interval = 1000 * 30 * 1;
//        } else {
//            interval = 1000 * 30 * 2;
//        }

        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, recurringDownloadIntent);

    }

    public static void cancelAutoRefresh(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

}
