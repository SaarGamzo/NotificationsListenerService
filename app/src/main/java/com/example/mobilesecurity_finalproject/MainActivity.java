package com.example.mobilesecurity_finalproject;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {

    private TextView txtPermissionsStatus;
    private NotificationReceiver nReceiver;
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private DatabaseReference databaseReference;

    private Button clearBTN, navigateSettingsBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setOnClickListeners();

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver, filter);

        checkNotificationPermission();

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

    private void firebaseInitialize(){
        // Firebase initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");
    }

    private void findViews(){
        clearBTN = findViewById(R.id.btnClearNotify);
        navigateSettingsBTN = findViewById(R.id.btnPermissionsScreen);
        txtPermissionsStatus = findViewById(R.id.txtPermissionsStatus);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setOnClickListeners(){
        navigateSettingsBTN.setOnClickListener(v -> navigateToSettings());
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

    private void navigateToSettings() {
        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private void checkNotificationPermission() {
        if (isNotificationServiceEnabled(this)) {
            txtPermissionsStatus.setText("Permissions granted");
        } else {
            txtPermissionsStatus.setText("Permissions not granted");
        }
    }

    private boolean isNotificationServiceEnabled(Context c) {
        String pkgName = c.getPackageName();
        final String flat = Settings.Secure.getString(c.getContentResolver(),
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

//    public void buttonClicked(View v) {
//        if (v.getId() == R.id.btnClearNotify) {
//            Intent i = new Intent("com.example.mobilesecurity_finalproject.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
//            i.putExtra("command", "clearall");
//            sendBroadcast(i);
//        } else if (v.getId() == R.id.btnPermissionsScreen) {
//            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
//        }
//        checkNotificationPermission();
//    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening(); // Start listening to Firebase updates
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationPermission(); // Check and update permissions status when the activity resumes
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
//            txtView.setText(temp);
        }
    }
}
