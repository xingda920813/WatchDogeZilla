package me.xd.watchdogezilla;

import android.app.Activity;
import android.os.Bundle;

import me.xd.task.PeriodicTaskUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PeriodicTaskUtils.startAsForeground(this);
        PeriodicTaskUtils.requestIgnoreBatteryOptimizations(this);
        finish();
    }
}
