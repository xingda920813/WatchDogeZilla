package me.xd.watchdogezilla;

import android.app.AlarmManager;
import android.app.PendingIntent;

import java.util.Calendar;

public class Main implements Runnable {

    @Override
    public void run() {
        final double price = Utils.fetchPrice();
        Utils.notifyOnce(price);
        Utils.lastPrice = price;
        final int minute = Calendar.getInstance().get(Calendar.MINUTE);
        final int nextMinute;
        if (minute < 15) {
            nextMinute = 15;
        } else if (minute < 30) {
            nextMinute = 30;
        } else if (minute < 45) {
            nextMinute = 45;
        } else {
            nextMinute = 60;
        }
        final AlarmManager am = App.sApp.getSystemService(AlarmManager.class);
        final long initialDelay = (nextMinute - minute) * 60 * 1000;
        final PendingIntent pi = MainService.createAlarmManagerPendingIntent();
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + initialDelay, pi);
    }
}
