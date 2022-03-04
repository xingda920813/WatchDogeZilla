package me.xd.task;

import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;

import java.util.Calendar;
import java.util.function.ToLongFunction;

public class PeriodicTaskService extends Service {

    public static Icon icon;
    private static Runnable sTask;
    private static ToLongFunction<Boolean> sDelayProvider;
    private static boolean sInited;
    private static boolean sStarted;

    public static void init(Icon icon, Runnable task, ToLongFunction<Boolean> delayProvider) {
        PeriodicTaskService.icon = icon;
        sTask = task;
        sDelayProvider = delayProvider;
        sInited = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Application app = getApplication();
        if (!sInited || !sStarted) {
            PeriodicTaskUtils.createNotificationChannel(app);
            final Notification.Builder builder = new Notification
                    .Builder(app, PeriodicTaskUtils.CHANNEL_ID_LOW)
                    .setContentTitle("正在启动服务...")
                    .setSmallIcon(icon != null ? icon : Icon.createWithResource(app, app.getApplicationInfo().icon));
            startForeground(PeriodicTaskUtils.NOTIFICATION_ID_LOW, builder.build());
            if (!sInited) return START_NOT_STICKY;
        }
        new Thread(() -> {
            sTask.run();
            final boolean fromAlarmManager = intent.getBooleanExtra("fromAlarmManager", false);
            long delay = sDelayProvider.applyAsLong(fromAlarmManager);
            if (fromAlarmManager) {
                if (delay == -1) delay = 15 * 60 * 1000;
            } else if (!sStarted) {
                sStarted = true;
                if (delay == -1) {
                    final int minute = Calendar.getInstance().get(Calendar.MINUTE);
                    final int nextMinute;
                    if (minute < 15) {
                        nextMinute = 15;
                    } else if (minute < 30) {
                        nextMinute = 30;
                    } else if (minute < 45) {
                        nextMinute = 45;
                    } else {
                        nextMinute = 60;
                    }
                    delay = (nextMinute - minute) * 60 * 1000;
                }
            } else {
                return;
            }
            final AlarmManager am = app.getSystemService(AlarmManager.class);
            final PendingIntent pi = createAlarmManagerPendingIntent();
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi);
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private PendingIntent createAlarmManagerPendingIntent() {
        final Application app = getApplication();
        final Intent intent = new Intent(app, PeriodicTaskService.class);
        intent.putExtra("fromAlarmManager", true);
        return PendingIntent.getService(app, PeriodicTaskUtils.REQUEST_CODE_ALARM_MANAGER, intent, PendingIntent.FLAG_IMMUTABLE);
    }
}
