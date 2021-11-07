package me.xd.watchdogezilla;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.sApp.startForegroundService(new Intent(App.sApp, MainService.class));
        finish();
    }
}
