package me.xd.watchdogezilla;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());
        App.app.startForegroundService(new Intent(App.app, MainService.class));
    }
}
