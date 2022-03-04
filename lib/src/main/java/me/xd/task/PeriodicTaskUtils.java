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

    public static final String CHANNEL_ID_HIGH = "High";
    public static final String CHANNEL_ID_LOW = "Low";

    public static final int NOTIFICATION_ID_HIGH = 64;
    public static final int NOTIFICATION_ID_LOW = 32;

    static final int REQUEST_CODE_ALARM_MANAGER = 16;
    private static final int REQUEST_CODE_PENDING_INTENT = 8;

    private static boolean sNotificationChannelCreated;

    public static void createNotificationChannel(Context ctx) {
        if (!sNotificationChannelCreated) {
            final NotificationManager nm = ctx.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID_HIGH, CHANNEL_ID_HIGH, NotificationManager.IMPORTANCE_HIGH));
            nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID_LOW, CHANNEL_ID_LOW, NotificationManager.IMPORTANCE_LOW));
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
        return PendingIntent.getService(ctx, REQUEST_CODE_PENDING_INTENT, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private static boolean isIgnoringBatteryOptimizations(Context ctx) {
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
