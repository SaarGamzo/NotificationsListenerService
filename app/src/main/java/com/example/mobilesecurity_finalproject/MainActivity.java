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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

/**
 * MainActivity displays and manages notifications and permissions for the application.
 * It interacts with Firebase to store and display notifications.
 */
public class MainActivity extends AppCompatActivity {

    private TextView txtPermissionsCreate;

    private TextView txtCouningFrom;
    private TextView txtPermissionsRead;
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private DatabaseReference databaseReference;

    private DatabaseReference runningTimeDataReference;
    private NotificationManager notificationManager;

    private Button navigateCreatePermissionsBTN;
    private Button navigateReadPermissionsBTN;
    private Button clearBTN;

    private Button btnStatistics;

    private static final int NOTIFICATION_ID = 1;
    private boolean isNotificationVisible = false;

    private String RUNNINFROM = "RUNNING_FROM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        findViews();
        setOnClickListeners();

        // Initialize notification manager
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationUtils.createNotificationChannel(this);

        // Check and request notification permissions
        checkNotificationPermissions();

        // Initialize Firebase
        firebaseInitialize();

        // Configure FirebaseRecyclerOptions
        FirebaseRecyclerOptions<NotificationData> options =
                new FirebaseRecyclerOptions.Builder<NotificationData>()
                        .setQuery(databaseReference, NotificationData.class)
                        .build();

        // Initialize RecyclerView and adapter
        adapter = new NotificationsAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    /**
     * Initializes Firebase Database reference.
     */
    private void firebaseInitialize() {
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");
        runningTimeDataReference = FirebaseDatabase.getInstance().getReference(RUNNINFROM);

        // Check if 'RUNNINGFROM' exists in Firebase
        runningTimeDataReference.child(RUNNINFROM).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // 'RUNNINGFROM' does not exist, upload current time
                    long currentTimestamp = System.currentTimeMillis();
                    runningTimeDataReference.child(RUNNINFROM).setValue(currentTimestamp).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateRunningSinceUI(currentTimestamp);
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to set initial running time", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // 'RUNNINGFROM' exists, update UI with existing timestamp
                    long runningSince = dataSnapshot.getValue(Long.class);
                    updateRunningSinceUI(runningSince);
                }
            }
                        @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Finds and initializes views from XML layout.
     */
    private void findViews() {
        navigateCreatePermissionsBTN = findViewById(R.id.btnNotificationAccess);
        navigateReadPermissionsBTN = findViewById(R.id.btnPermissionsScreen);
        clearBTN = findViewById(R.id.btnClearNotify);
        txtPermissionsCreate = findViewById(R.id.txtPermissionsCreate);
        txtPermissionsRead = findViewById(R.id.txtPermissionsRead);
        recyclerView = findViewById(R.id.recyclerView);
        txtCouningFrom = findViewById(R.id.txtCouningFrom);
        btnStatistics = findViewById(R.id.btnStatistics);
    }

    /**
     * Sets click listeners for navigation and clear buttons.
     */
    private void setOnClickListeners() {
        navigateCreatePermissionsBTN.setOnClickListener(v -> navigateToCreatePermissions());
        navigateReadPermissionsBTN.setOnClickListener(v -> navigateToReadPermissions());
        clearBTN.setOnClickListener(v -> clearAllNotifications());
        btnStatistics.setOnClickListener(v -> navigateToStatistics());
    }

    private void navigateToStatistics() {
        Intent intent = new Intent(MainActivity.this, Statistics.class);
        startActivity(intent);
    }

    /**
     * Sets the 'Running since' timestamp in the UI.
     */
    private void updateRunningSinceUI(long timestamp) {
        String formattedDate = SimpleDateFormat.getDateTimeInstance().format(new Date(timestamp));
        txtCouningFrom.setText("Counting from: " + formattedDate);
    }

    /**
     * Clears all notifications stored in Firebase.
     */
    private void clearAllNotifications() {
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "All notifications cleared", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to clear notifications", Toast.LENGTH_SHORT).show();
            }
        });
        long currentTimestamp = System.currentTimeMillis();
        runningTimeDataReference.child(RUNNINFROM).setValue(currentTimestamp).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateRunningSinceUI(currentTimestamp);
            } else {
            }
        });

    }

    /**
     * Navigates to system settings for app notification access.
     */
    private void navigateToCreatePermissions() {
        startActivity(new Intent(Settings.ACTION_ALL_APPS_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName()));
    }

    /**
     * Navigates to system settings for notification listener access.
     */
    private void navigateToReadPermissions() {
        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    /**
     * Checks and updates UI based on notification permissions granted.
     */
    private void checkNotificationPermissions() {
        boolean createPermissionGranted = isNotificationCreatePermissionGranted(this);
        boolean readPermissionGranted = isNotificationReadPermissionGranted(this);

        // Update UI based on create notification permission
        if (createPermissionGranted) {
            txtPermissionsCreate.setText("Permissions to create notifications:\nGranted");
        } else {
            txtPermissionsCreate.setText("Permissions to create notifications:\nNot granted");
        }

        // Update UI based on read notification permission
        if (readPermissionGranted) {
            txtPermissionsRead.setText("Permissions to read notifications:\nGranted");
            NLService.setRunning(true);
            if (!isNotificationVisible) {
                showNotification();
            }
        } else {
            txtPermissionsRead.setText("Permissions to read notifications:\nNot granted");
            NLService.setRunning(false);
            if (isNotificationVisible) {
                removeNotification();
            }
        }
    }

    /**
     * Shows a persistent notification indicating background notification collection.
     */
    private void showNotification() {
        Notification notification = NotificationUtils.buildNotification(this, "Collecting notifications in background...");
        notificationManager.notify(NOTIFICATION_ID, notification);
        isNotificationVisible = true;
    }

    /**
     * Removes the persistent notification.
     */
    private void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
        isNotificationVisible = false;
    }

    /**
     * Checks if the app has permission to create notifications.
     *
     * @param context The context of the calling component.
     * @return True if the app has notification creation permission, false otherwise.
     */
    private boolean isNotificationCreatePermissionGranted(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = notificationManager.areNotificationsEnabled();
        return areNotificationsEnabled;
    }

    /**
     * Checks if the app has permission to read notifications.
     *
     * @param context The context of the calling component.
     * @return True if the app has notification listener permission, false otherwise.
     */
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
    }
}