package me.xd.watchdogezilla;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

public class MainService extends Service {

    private static boolean sStarted;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!sStarted) {
            Utils.createNotificationChannel();
            final Notification.Builder builder = new Notification
                    .Builder(App.app, "Low")
                    .setContentTitle("正在启动服务...")
                    .setSmallIcon(App.icon);
            startForeground(16, builder.build());
        }
        new Thread(() -> {
            final double price = Utils.fetchPrice();
            Utils.notifyOnce(price);
            Utils.lastPrice = price;
            final long delay;
            if (intent.getBooleanExtra("fromAlarmManager", false)) {
                delay = 15 * 60 * 1000;
            } else if (!sStarted) {
                sStarted = true;
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
            } else {
                return;
            }
            final AlarmManager am = App.app.getSystemService(AlarmManager.class);
            final PendingIntent pi = createAlarmManagerPendingIntent();
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi);
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static PendingIntent createAlarmManagerPendingIntent() {
        final Intent intent = new Intent(App.app, MainService.class);
        intent.putExtra("fromAlarmManager", true);
        return PendingIntent.getService(App.app, 64, intent, PendingIntent.FLAG_IMMUTABLE);
    }
}
