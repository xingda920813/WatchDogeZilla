package me.xd.watchdogezilla;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

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
        new Timer(false).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final double price = Utils.fetchPrice();
                Utils.notifyOnce(price);
                Utils.lastPrice = price;
            }
        }, (nextMinute - minute) * 60 * 1000, 15 * 60 * 1000);
    }
}
