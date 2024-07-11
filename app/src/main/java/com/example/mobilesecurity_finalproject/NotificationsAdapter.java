package com.example.mobilesecurity_finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class NotificationsAdapter extends FirebaseRecyclerAdapter<NotificationData, NotificationsAdapter.NotificationViewHolder> {

    public NotificationsAdapter(@NonNull FirebaseRecyclerOptions<NotificationData> options) {
        super(options);
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull NotificationData model) {
        holder.packageName.setText(model.getPackageName());
        holder.title.setText(model.getTitle());
        holder.text.setText(model.getText());
        holder.timestamp.setText(String.valueOf(model.getTimestamp()));
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView packageName, title, text, timestamp;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.packageName);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            timestamp = itemView.findViewById(R.id.timestamp);
        }
    }


}
