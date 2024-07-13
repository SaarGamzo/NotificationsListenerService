package com.example.mobilesecurity_finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.StackedValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Statistics extends AppCompatActivity {

    private BarChart barChart;
    private Button btnBack;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Initialize views
        barChart = findViewById(R.id.barChart);
        btnBack = findViewById(R.id.btnBack);

        // Set headline
        setTitle("Statistics");

        // Set click listener for back button
        btnBack.setOnClickListener(v -> finish());

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("notifications");

        // Display bar chart based on notification packages
        displayBarChart();
    }

    /**
     * Method to fetch notifications from Firebase and display them in a bar chart.
     */
    private void displayBarChart() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Map to store notification counts by package
                Map<String, Integer> notificationData = new HashMap<>();

                // Initialize with the specified categories, including those with count 0
                notificationData.put("Others", 0);
                notificationData.put("WhatsApp", 0);
                notificationData.put("Outlook", 0);
                notificationData.put("Spotify", 0);
                notificationData.put("Instagram", 0);

                // Iterate through notifications
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NotificationData notification = snapshot.getValue(NotificationData.class);
                    if (notification != null && notification.getPackageName() != null) {
                        // Get package name from notification
                        String packageName = notification.getPackageName();

                        // Check if the package name contains known strings
                        if (packageName.contains("whatsapp")) {
                            addToNotificationData(notificationData, "WhatsApp");
                        } else if (packageName.contains("outlook")) {
                            addToNotificationData(notificationData, "Outlook");
                        } else if (packageName.contains("spotify")) {
                            addToNotificationData(notificationData, "Spotify");
                        } else if (packageName.contains("instagram")) {
                            addToNotificationData(notificationData, "Instagram");
                        } else {
                            // Other packages
                            addToNotificationData(notificationData, "Others");
                        }
                    }
                }

                // Prepare entries and labels for the bar chart
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                int index = 0;
                for (Map.Entry<String, Integer> entry : notificationData.entrySet()) {
                    labels.add(entry.getKey()); // Add label (group name)
                    entries.add(new BarEntry(index++, entry.getValue())); // Add entry with count
                }

                // Create dataset
                BarDataSet dataSet = new BarDataSet(entries, "");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                dataSet.setValueTextSize(12f);

                // Create bar data object
                BarData data = new BarData(dataSet);
                data.setValueFormatter(new StackedValueFormatter(false, "", 0)); // Show counts instead of percentages

                // Configure bar chart
                barChart.setData(data);
                barChart.getDescription().setEnabled(false); // Disable description
                barChart.animateY(1000);
                barChart.invalidate(); // Refresh chart

                // Configure X-axis labels
                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                barChart.getXAxis().setGranularity(1f);
                barChart.getXAxis().setGranularityEnabled(true);

                // Configure legend
                Legend legend = barChart.getLegend();
                legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                legend.setDrawInside(false); // Don't draw legend inside the chart
                legend.setTextSize(12f); // Set legend text size
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    /**
     * Helper method to add notification count to the map.
     *
     * @param map The map to add to.
     * @param key The key for the count.
     */
    private void addToNotificationData(Map<String, Integer> map, String key) {
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
        }
    }
}
