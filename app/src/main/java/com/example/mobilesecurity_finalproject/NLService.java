package com.example.mobilesecurity_finalproject;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NLService extends NotificationListenerService {

    private String TAG = "NLService";
    private NLServiceReceiver nlservicereciver;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        registerReceiver(nlservicereciver, filter);

        // Firebase initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "**********  onNotificationPosted");

        Notification newNot = sbn.getNotification();

        // Extract package name
        String packageName = sbn.getPackageName();

        // Extract title and text
        String title = "";
        String text = "";
        if (newNot.extras != null) {
            title = newNot.extras.getString(Notification.EXTRA_TITLE);
            CharSequence csText = newNot.extras.getCharSequence(Notification.EXTRA_TEXT);
            text = csText != null ? csText.toString() : "";
        }

        // Extract timestamp
        long timestamp = newNot.when;

        // Log the details
        Log.i(TAG, "Package Name: " + packageName);
        Log.i(TAG, "Title: " + title);
        Log.i(TAG, "Text: " + text);
        Log.i(TAG, "Timestamp: " + timestamp);

        // Send broadcast with the notification details
        Intent i = new Intent("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event", "onNotificationPosted :\nPackage Name: " + packageName + "\nTitle: " + title + "\nText: " + text + "\nTimestamp: " + timestamp + "\n");
        sendBroadcast(i);

        // Upload to Firebase
        NotificationData notificationData = new NotificationData(packageName, title, text, timestamp);
        databaseReference.push().setValue(notificationData);
    }

    class NLServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getStringExtra("command").equals("clearall")) {
                NLService.this.cancelAllNotifications();
            } else if (intent.getStringExtra("command").equals("list")) {
                Intent i1 = new Intent("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_EXAMPLE");
                i1.putExtra("notification_event", "=====================");
                sendBroadcast(i1);
                int i = 1;
                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
                    Intent i2 = new Intent("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_EXAMPLE");
                    i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "\n" + sbn.getNotification().tickerText);
                    sendBroadcast(i2);
                    i++;
                }
                Intent i3 = new Intent("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_EXAMPLE");
                i3.putExtra("notification_event", "===== Notification List ====");
                sendBroadcast(i3);
            }
        }
    }
}