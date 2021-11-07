package me.xd.watchdogezilla;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)) {
            App.sApp.startForegroundService(new Intent(App.sApp, MainService.class));
        }
    }
}
