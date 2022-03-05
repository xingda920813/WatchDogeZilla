package me.xd.watchdogezilla;

import android.app.Application;
import android.graphics.drawable.Icon;

import me.xd.task.PeriodicTaskService;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PeriodicTaskService.init(Icon.createWithData(Images.ETH, 0, Images.ETH.length), () -> {
            final double price = Utils.fetchPrice();
            Utils.notifyOnce(this, price);
            Utils.lastPrice = price;
        }, fromAlarmManager -> -1);
    }
}
