package com.example.mobilesecurity_finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        holder.packageName.setText("*Package*: " + model.getPackageName() + "\n");
        holder.title.setText("*Title*: " + model.getTitle() + "\n");
        holder.text.setText("*Text*: " +model.getText() + "\n");
        String formattedDate = SimpleDateFormat.getDateTimeInstance().format(new Date(model.getTimestamp()));
        holder.timestamp.setText("*Posted*: " + formattedDate + "\n");

        if(model.getPackageName().contains("whatsapp")){
            holder.packageIcon.setImageResource(R.drawable.whatsapp);
        }
        else if(model.getPackageName().contains("facebook")){
            holder.packageIcon.setImageResource(R.drawable.facebook);
        }
        else if(model.getPackageName().contains("linkedin")){
            holder.packageIcon.setImageResource(R.drawable.linkedin);
        }
        else if(model.getPackageName().contains("instagram")){
            holder.packageIcon.setImageResource(R.drawable.instagram);
        }
        else if(model.getPackageName().contains("spotify")){
            holder.packageIcon.setImageResource(R.drawable.spotify);
        }
        else if(model.getPackageName().contains("chrome")){
            holder.packageIcon.setImageResource(R.drawable.chrome);
        }
        else if(model.getPackageName().contains("outlook")){
            holder.packageIcon.setImageResource(R.drawable.outlook);
        }
        else {
            holder.packageIcon.setImageResource(R.drawable.android);
        }

    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView packageName, title, text, timestamp;
        ImageView packageIcon;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            packageName = itemView.findViewById(R.id.packageName);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            timestamp = itemView.findViewById(R.id.timestamp);
            packageIcon = itemView.findViewById(R.id.packageIcon);
        }
    }


}
