/**
 * Notifications Adapter class that binds the notification information to the notifications page.
 * The information will be fetched from the Firebase database.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile.adaptors;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.Reminder;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    /**
     * ReminderAdapter fields
     */
    private List<Reminder> reminderList;

    /**
     * Constructor for the ReminderAdapter that connects the field objects.
     * @param reminderList List class type list of notifications.
     */
    public ReminderAdapter(List<Reminder> reminderList) {
        this.reminderList = reminderList;
    }


    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    class ReminderViewHolder extends RecyclerView.ViewHolder {

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
        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);

            //Connecting GUI elements
            layoutNotifications = itemView.findViewById(R.id.layoutReminders);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            noticeText = itemView.findViewById(R.id.reminderText);
        }
    }
}
