package me.xd.watchdogezilla;

import android.app.Application;
import android.graphics.drawable.Icon;

public class App extends Application {

    public static App app;
    public static Icon icon;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        icon = Icon.createWithData(Images.ETH, 0, Images.ETH.length);
    }
}
