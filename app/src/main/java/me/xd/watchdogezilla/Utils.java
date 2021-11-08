package me.xd.watchdogezilla;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Utils {

    public static Double lastPrice;

    private static boolean sNotificationChannelCreated;

    public static double fetchPrice() {
        try {
            final URL url = new URL("https://www.okex.com/api/index/v3/ETH-USDT/constituents");
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                final String body = reader.lines().collect(Collectors.joining("\n"));
                final String last = extractPrice(body);
                return Double.parseDouble(last);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return Double.NaN;
        }
    }

    private static String extractPrice(String res) {
        final String key = "\"last\":\"";
        final int start = res.indexOf(key) + key.length();
        final int end = res.indexOf('"', start);
        return res.substring(start, end);
    }

    public static void notifyOnce(double price) {
        final String title = formatPrice(price);
        Level level = Level.INFO;
        final String desc;
        if (lastPrice != null) {
            final double changeInPercentage = (price - lastPrice) / lastPrice;
            if (changeInPercentage >= 0.01 || changeInPercentage <= -0.01) {
                level = Level.WARNING;
            }
            final String changeInDesc = (changeInPercentage >= 0 ? "+" : "") + formatPrice(changeInPercentage * 100) + '%';
            desc = changeInDesc + ", 现报 " + title + ", 前值 " + formatPrice(lastPrice)
                    + (level == Level.WARNING ? ". 请注意控制风险." : "");
        } else {
            desc = "ETH/USDT 现报 " + title;
        }
        notifyOnce(title, desc, level);
    }

    private static String formatPrice(double price) {
        return String.format(Locale.US, "%.2f", price);
    }

    public static void createNotificationChannel() {
        final NotificationManager nm = App.app.getSystemService(NotificationManager.class);
        if (!sNotificationChannelCreated) {
            nm.createNotificationChannel(new NotificationChannel("High", "High", NotificationManager.IMPORTANCE_HIGH));
            nm.createNotificationChannel(new NotificationChannel("Low", "Low", NotificationManager.IMPORTANCE_LOW));
            sNotificationChannelCreated = true;
        }
    }

    private static void notifyOnce(String title, String desc, Level level) {
        createNotificationChannel();
        final Notification.Builder builder = new Notification
                .Builder(App.app, level == Level.WARNING ? "High" : "Low")
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(App.icon);
        if (level == Level.WARNING) {
            builder.setColorized(true)
                    .setColor(Color.YELLOW);
        } else {
            final Intent intent = new Intent(App.app, MainService.class);
            final PendingIntent pendingIntent = PendingIntent.getService(App.app, 8, intent, PendingIntent.FLAG_IMMUTABLE);
            final Notification.Action.Builder actionBuilder = new Notification.Action.Builder(App.icon, "立即更新", pendingIntent);
            builder.addAction(actionBuilder.build());
        }
        App.app.getSystemService(NotificationManager.class).notify(level == Level.WARNING ? 32 : 16, builder.build());
    }
}
