package com.example.ezmathmobile.adaptors;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.Notification;
import com.example.ezmathmobile.utilities.TimeConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * This is the NotificationAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class NotificationMonthAdaptor extends RecyclerView.Adapter<NotificationMonthAdaptor.NotificationMonthViewHolder> {
    // These are the private variables
    private LinkedHashMap<String,List<Notification>> notifications;


    /**
     * This is the constructor for the Adaptor
     * @param notifications This is a List of Notifications
     */
    public NotificationMonthAdaptor(LinkedHashMap<String,List<Notification>> notifications) {
        Log.d("Month Notif",notifications.toString());
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
    public NotificationMonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationMonthViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_month, parent, false));
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationMonthViewHolder holder, int position) {
        Object[] keys = notifications.keySet().toArray();
        String month = (String) keys[position];
        if (position < notifications.size()) holder.bindNotification(notifications.get(month),month);
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
    public static class NotificationMonthViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        ConstraintLayout layoutNotification;
        //View viewBackground;
        TextView notificationMonthName, notificationTime, notificationDate;
        RecyclerView notificationsView;

        /**
         * This is the NotificationViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public NotificationMonthViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);
            // Bind the objects to the view IDs
            layoutNotification = itemView.findViewById(R.id.layoutMonthNotification);
            notificationMonthName = itemView.findViewById(R.id.monthName);
            notificationsView = itemView.findViewById(R.id.notificationsView);

        }

        /**
         * This is the bind Notification method that will bind actions
         * and listeners to the notification
         * @param List<Notification> this is a List of notifications to bind actions to
         */
        void bindNotification(final List<Notification> notifications, String month) {
            Log.d("Month Notif",String.valueOf(month));
            if (notifications != null) {
                Log.d("Month Notif",String.valueOf(month));
                Log.d("Month Notif", notifications.toString());
                // Set the name
                notificationMonthName.setText(String.valueOf(month));

                // Set the adaptor with the current month notifications
                final NotificationAdaptor notificationAdaptor = new NotificationAdaptor(notifications);
                notificationsView.setAdapter(notificationAdaptor);
            }
        }
    }
}