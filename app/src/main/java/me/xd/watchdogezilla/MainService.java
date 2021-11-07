package me.xd.watchdogezilla;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MainService extends Service {

    private static boolean sStarted;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!sStarted) {
            sStarted = true;
            Utils.createNotificationChannel();
            final Notification.Builder builder = new Notification
                    .Builder(App.sApp, "Low")
                    .setContentTitle("正在启动服务...")
                    .setSmallIcon(App.sIcon);
            startForeground(16, builder.build());
            new Thread(new Main()).start();
        } else {
            new Thread(() -> {
                final double price = Utils.fetchPrice();
                Utils.notifyOnce(price);
                Utils.lastPrice = price;
            }).start();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
