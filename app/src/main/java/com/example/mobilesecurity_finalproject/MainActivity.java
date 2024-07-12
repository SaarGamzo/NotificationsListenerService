package com.example.mobilesecurity_finalproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView txtPermissionsCreate;
    private TextView txtPermissionsRead;
    private TextView txtPermissionsStatus;
    private NotificationReceiver nReceiver;
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private DatabaseReference databaseReference;
    private NotificationManager notificationManager;

    private Button clearBTN;
    private Button navigateCreatePermissionsBTN;
    private Button navigateReadPermissionsBTN;

    private static final int NOTIFICATION_ID = 1;
    private boolean isNotificationVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setOnClickListeners();

        // Initialize notification manager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationUtils.createNotificationChannel(this);

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);

        checkNotificationPermissions();

        firebaseInitialize();

        // Configure FirebaseRecyclerOptions
        FirebaseRecyclerOptions<NotificationData> options =
                new FirebaseRecyclerOptions.Builder<NotificationData>()
                        .setQuery(databaseReference, NotificationData.class)
                        .build();

        // Initialize your adapter with the options
        adapter = new NotificationsAdapter(options);

        // Set up your RecyclerView with the adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void firebaseInitialize() {
        // Firebase initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");
    }

    private void findViews() {
        clearBTN = findViewById(R.id.btnClearNotify);
        navigateCreatePermissionsBTN = findViewById(R.id.btnNotificationAccess);
        navigateReadPermissionsBTN = findViewById(R.id.btnPermissionsScreen);
        txtPermissionsCreate = findViewById(R.id.txtPermissionsCreate);
        txtPermissionsRead = findViewById(R.id.txtPermissionsRead);
//        txtPermissionsStatus = findViewById(R.id.txtPermissionsCreate);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setOnClickListeners() {
        navigateCreatePermissionsBTN.setOnClickListener(v -> navigateToCreatePermissions());
        navigateReadPermissionsBTN.setOnClickListener(v -> navigateToReadPermissions());
        clearBTN.setOnClickListener(v -> clearAllNotifications());
    }

    private void clearAllNotifications() {
        // Remove all notifications from Firebase
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Notify the user that notifications have been cleared
                Toast.makeText(MainActivity.this, "All notifications cleared", Toast.LENGTH_SHORT).show();
            } else {
                // Handle any errors
                Toast.makeText(MainActivity.this, "Failed to clear notifications", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToCreatePermissions() {
        startActivity(new Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName()));
    }

    private void navigateToReadPermissions() {
        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private void checkNotificationPermissions() {
        boolean createPermissionGranted = isNotificationCreatePermissionGranted(this);
        boolean readPermissionGranted = isNotificationReadPermissionGranted(this);

        if (createPermissionGranted) {
            txtPermissionsCreate.setText("Permissions granted");
            navigateReadPermissionsBTN.setEnabled(true);
        } else {
            txtPermissionsCreate.setText("Permissions not granted");
            navigateReadPermissionsBTN.setEnabled(false);
        }

        if (readPermissionGranted) {
            txtPermissionsRead.setText("Permissions granted");
            // Check if the notification is already visible
            if (!isNotificationVisible) {
                showNotification();
            }
        } else {
            txtPermissionsRead.setText("Permissions not granted");
            // Remove the notification if it's currently visible
            if (isNotificationVisible) {
                removeNotification();
            }
        }
    }

    private void showNotification() {
        Notification notification = NotificationUtils.buildNotification(this, "Collecting notifications in background...");
        notificationManager.notify(NOTIFICATION_ID, notification);
        isNotificationVisible = true;
    }

    private void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
        isNotificationVisible = false;
    }

    private boolean isNotificationCreatePermissionGranted(Context context) {
        // Use NotificationManagerCompat to check if the app has permission to create notifications
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();
        return areNotificationsEnabled;
    }

    private boolean isNotificationReadPermissionGranted(Context context) {
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening(); // Start listening to Firebase updates
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationPermissions(); // Check and update permissions status when the activity resumes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle broadcast if needed
        }
    }
}
