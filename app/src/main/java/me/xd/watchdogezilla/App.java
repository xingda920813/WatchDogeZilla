package me.xd.watchdogezilla;

import android.app.Application;
import android.graphics.drawable.Icon;

public class App extends Application {

    public static App sApp;
    public static Icon sIcon;

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        sIcon = Icon.createWithData(Images.ETH, 0, Images.ETH.length);
    }
}
