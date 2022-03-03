package me.xd.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;

public class PeriodicTaskUtils {

    private static boolean sNotificationChannelCreated;

    public static void createNotificationChannel(Context ctx) {
        final NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (!sNotificationChannelCreated) {
            nm.createNotificationChannel(new NotificationChannel("High", "High", NotificationManager.IMPORTANCE_HIGH));
            nm.createNotificationChannel(new NotificationChannel("Low", "Low", NotificationManager.IMPORTANCE_LOW));
            sNotificationChannelCreated = true;
        }
    }

    public static void startAsForeground(Context ctx) {
        ctx = ctx.getApplicationContext();
        ctx.startForegroundService(new Intent(ctx, PeriodicTaskService.class));
    }

    public static PendingIntent createPendingIntent(Context ctx) {
        ctx = ctx.getApplicationContext();
        final Intent intent = new Intent(ctx, PeriodicTaskService.class);
        return PendingIntent.getService(ctx, 8, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public static boolean isIgnoringBatteryOptimizations(Context ctx) {
        final PowerManager pm = ctx.getSystemService(PowerManager.class);
        return pm.isIgnoringBatteryOptimizations(ctx.getPackageName());
    }

    @SuppressLint("BatteryLife")
    public static void requestIgnoreBatteryOptimizations(Context ctx) {
        if (isIgnoringBatteryOptimizations(ctx)) return;
        final Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + ctx.getPackageName()));
        if (!(ctx instanceof Activity)) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }
}
