package com.example.mobilesecurity_finalproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {

    public static final String CHANNEL_ID = "NotificationListenerChannel";
    public static final String CHANNEL_NAME = "Notification Listener Service Channel";
    public static final String CHANNEL_DESCRIPTION = "Channel for Notification Listener Service";

    public static void createNotificationChannel(Context context) {
        Log.d(CHANNEL_ID, "Create notification channel!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            // Register the channel with the system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static Notification buildNotification(Context context, String message) {

        Log.d(CHANNEL_ID, "build notification!");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle("Notification Collector")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true); // Notification cannot be dismissed by the user

        return builder.build();
    }
}
