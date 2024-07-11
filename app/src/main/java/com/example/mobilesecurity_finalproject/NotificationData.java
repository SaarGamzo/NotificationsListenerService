package com.example.mobilesecurity_finalproject;

public class NotificationData {
    private String packageName;
    private String title;
    private String text;
    private long timestamp;

    public NotificationData() {
        // Default constructor required for calls to DataSnapshot.getValue(NotificationData.class)
    }

    public NotificationData(String packageName, String title, String text, long timestamp) {
        this.packageName = packageName;
        this.title = title;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
