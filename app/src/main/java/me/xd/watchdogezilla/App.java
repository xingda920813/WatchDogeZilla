package me.xd.watchdogezilla;

import android.app.Application;
import android.graphics.drawable.Icon;

import me.xd.task.PeriodicTaskService;

public class App extends Application {

    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        PeriodicTaskService.init(Icon.createWithData(Images.ETH, 0, Images.ETH.length), () -> {
            final double price = Utils.fetchPrice();
            Utils.notifyOnce(price);
            Utils.lastPrice = price;
        }, fromAlarmManager -> -1);
    }
}
