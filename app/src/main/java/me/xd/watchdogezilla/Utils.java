package me.xd.watchdogezilla;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;
import java.util.stream.Collectors;

import me.xd.task.PeriodicTaskService;
import me.xd.task.PeriodicTaskUtils;

class Utils {

    public static double lastPrice = Double.NaN;

    public static double fetchPrice() {
        try {
            return fetchPriceCore();
        } catch (IOException ignored) {}
        try {
            return fetchPriceCore();
        } catch (IOException ignored) {}
        return Double.NaN;
    }

    private static double fetchPriceCore() throws IOException {
        final URL url = new URL("https://capi.bitgetapi.com/api/swap/v3/market/mark_price?symbol=cmt_ethusdt");
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            final String body = reader.lines().collect(Collectors.joining("\n"));
            final String last = extractPrice(body);
            return Double.parseDouble(last);
        }
    }

    private static String extractPrice(String res) {
        final String key = "\"mark_price\":\"";
        final int start = res.indexOf(key) + key.length();
        final int end = res.indexOf('"', start);
        return res.substring(start, end);
    }

    public static void notifyOnce(double price) {
        final String title = formatPrice(price);
        Level level = Level.INFO;
        final String desc;
        if (!Double.isNaN(lastPrice)) {
            final double changeInPercentage = (price - lastPrice) / lastPrice;
            if (changeInPercentage >= 0.008 || changeInPercentage <= -0.008) {
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

    private static void notifyOnce(String title, String desc, Level level) {
        PeriodicTaskUtils.createNotificationChannel(App.app);
        final Notification.Builder builder = new Notification
                .Builder(App.app, level == Level.WARNING ? "High" : "Low")
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(PeriodicTaskService.icon);
        if (level == Level.WARNING) {
            builder.setColorized(true)
                    .setColor(Color.YELLOW);
        } else {
            final PendingIntent pi = PeriodicTaskUtils.createPendingIntent(App.app);
            final Notification.Action.Builder actionBuilder = new Notification.Action.Builder(PeriodicTaskService.icon, "立即更新", pi);
            builder.addAction(actionBuilder.build());
        }
        App.app.getSystemService(NotificationManager.class).notify(level == Level.WARNING ? 32 : 16, builder.build());
    }
}
