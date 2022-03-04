package me.xd.watchdogezilla;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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

    static double sLastPrice = Double.NaN;

    static double fetchPrice() {
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

    static void notifyOnce(Context ctx, double price) {
        final String title = formatPrice(price);
        Level level = Level.INFO;
        final String desc;
        if (!Double.isNaN(sLastPrice)) {
            final double changeInPercentage = (price - sLastPrice) / sLastPrice;
            if (changeInPercentage >= 0.008 || changeInPercentage <= -0.008) {
                level = Level.WARNING;
            }
            final String changeInDesc = (changeInPercentage >= 0 ? "+" : "") + formatPrice(changeInPercentage * 100) + '%';
            desc = changeInDesc + ", 现报 " + title + ", 前值 " + formatPrice(sLastPrice)
                    + (level == Level.WARNING ? ". 请注意控制风险." : "");
        } else {
            desc = "ETH/USDT 现报 " + title;
        }
        notifyOnce(ctx, title, desc, level);
    }

    private static String formatPrice(double price) {
        return String.format(Locale.US, "%.2f", price);
    }

    private static void notifyOnce(Context ctx, String title, String desc, Level level) {
        PeriodicTaskUtils.createNotificationChannel(ctx);
        final Notification.Builder builder = new Notification
                .Builder(ctx, level == Level.WARNING ? PeriodicTaskUtils.CHANNEL_ID_HIGH : PeriodicTaskUtils.CHANNEL_ID_LOW)
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(PeriodicTaskService.icon);
        if (level == Level.WARNING) {
            builder.setColorized(true)
                    .setColor(Color.YELLOW);
        } else {
            final PendingIntent pi = PeriodicTaskUtils.createPendingIntent(ctx);
            final Notification.Action.Builder actionBuilder = new Notification.Action.Builder(PeriodicTaskService.icon, "立即更新", pi);
            builder.addAction(actionBuilder.build());
        }
        final int notificationId = level == Level.WARNING ? PeriodicTaskUtils.NOTIFICATION_ID_HIGH : PeriodicTaskUtils.NOTIFICATION_ID_LOW;
        ctx.getSystemService(NotificationManager.class).notify(notificationId, builder.build());
    }
}
