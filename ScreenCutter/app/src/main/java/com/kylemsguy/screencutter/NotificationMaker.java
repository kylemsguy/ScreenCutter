package com.kylemsguy.screencutter;

import android.app.*;
import android.content.*;
import android.support.v4.app.NotificationCompat;

public class NotificationMaker {
    private static final int NOTIFICATION_ID = 1234;
    public static Notification buildNotification(Context context) {
        Intent intent = new Intent(context, ScreenshotActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return new NotificationCompat.Builder(context).
                setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle("Tap to select text").
                setContentIntent(PendingIntent.getActivity(context, 2016, intent, 0)).
                setCategory(NotificationCompat.CATEGORY_SERVICE).
                setOngoing(true).
                setLocalOnly(true).
                setVisibility(NotificationCompat.VISIBILITY_SECRET).
                build();
    }

    public static void showNotification(Context context) {
        Notification notification = buildNotification(context);
        NotificationManager mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.notify(NOTIFICATION_ID, notification);
    }

    public static void hideNotification(Context context) {
        NotificationManager mgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mgr.cancel(NOTIFICATION_ID);
    }

    public static void setNotificationEnabled(Context context, boolean enabled) {
        SharedPreferences prefs = context.getSharedPreferences("screencutter", 0);
        prefs.edit().putBoolean("notification_enabled", enabled).apply();
        if (enabled) {
            showNotification(context);
        } else {
            hideNotification(context);
        }
    }
    public static void showNotificationIfNeeded(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("screencutter", 0);
        if (prefs.getBoolean("notification_enabled", true)) {
            showNotification(context);
        }
    }
}