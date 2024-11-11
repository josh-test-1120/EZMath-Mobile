/**
 * Notifications Adapter class that binds the notification information to the notifications page.
 * The information will be fetched from the Firebase database.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    /**
     * NotificationAdapter fields
     */
    private List<Notification>  notificationList;

    /**
     * Constructor for the NotificationAdapter that connects the field objects.
     * @param notificationList List class type list of notifications.
     */
    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class NotificationViewHolder extends RecyclerView.ViewHolder {

        /**
         * Fields to be connected to the GUI elements.
         */
        ConstraintLayout layoutNotifications;
        View viewBackground;
        TextView noticeText;


        /**
         * NotificationViewHolder constructor that creates the class object and connects the
         * GUI references to the fields.
         * @param itemView The view screen of the app.
         */
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            //Connecting GUI elements
            layoutNotifications = itemView.findViewById(R.id.layoutNotifications);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            noticeText = itemView.findViewById(R.id.notificationText);
        }
    }
}
