package com.example.ezmathmobile.adaptors;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.Notification;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * This is the NotificationAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class NotificationAdaptor extends RecyclerView.Adapter<NotificationAdaptor.NotificationViewHolder> {
    // These are the private variables
    private List<Notification> notifications;


    /**
     * This is the constructor for the Adaptor
     * @param notifications This is a List of Notifications
     */
    public NotificationAdaptor(List<Notification> notifications) {
        this.notifications = notifications;
    }

    /**
     * This is an override of the onCreateViewHolder method
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new NotificationViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false));
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        if (position < notifications.size()) holder.bindNotification(notifications.get(position));
    }

    /**
     * This is an override of the getItemCount method
     * @return the size of the notifications list
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * This is the NotificationViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        ConstraintLayout layoutNotification;
        //View viewBackground;
        TextView notificationName, notificationTime, notificationDate;

        /**
         * This is the NotificationViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public NotificationViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);
            // Bind the objects to the view IDs
            layoutNotification = itemView.findViewById(R.id.layoutNotification);
            notificationName = itemView.findViewById(R.id.examName);
            notificationTime = itemView.findViewById(R.id.examTime);
            notificationDate = itemView.findViewById(R.id.examDate);
        }

        /**
         * This is the bind Notification method that will bind actions
         * and listeners to the notification
         * @param notification this is the notification to bind actions to
         */
        void bindNotification(final Notification notification) {
            Log.d("Notif Data",notification.toString());
            // Update the view with the poster information
            notificationName.setText(notification.examName);
//            notificationTime.setText(notification.examTime.toString());
            if (notification.examDate != null) {
                Date date = notification.examDate.toDate();
                // Get the time
                // Convert Date to LocalTime
                //LocalTime localTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                // Format LocalTime to string
                //String timeString = localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                //notificationTime.setText(localDateTime.toString());
                // Convert Date to LocalDate using Instant
                LocalDate localDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
                notificationDate.setText(localDate.toString());
            }
        }
    }
}