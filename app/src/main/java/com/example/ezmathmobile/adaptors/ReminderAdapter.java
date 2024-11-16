/**
 * Notifications Adapter class that binds the notification information to the notifications page.
 * The information will be fetched from the Firebase database.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.Reminder;

import java.util.List;
import java.util.Objects;

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


    /**
     * An overridden method that listens to CreateViewHolder action and inflates the
     * item_container_reminder layout file.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return Retunrs ReminderViewHolder for the Reminders page.
     */
    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReminderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_reminder, parent, false));
    }

    /**
     * An overridden method that listens to the BindViewHolder action and binds the
     * individual reminders to the page.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        holder.bindReminders(reminderList.get(position));
    }

    /**
     * An overridden method that returns the size of the reminderList ArrayList.
     * @return Integer type size of reminderList ArrayList.
     */
    @Override
    public int getItemCount() {
        return reminderList.size();
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

        /**
         * Helper method that binds the content information to the GUI elements.
         * @param reminder Reminder element.
         */
        void bindReminders(final Reminder reminder) {
            // Setting text
            noticeText.setText(reminder.text);

            // Setting background
            // If the type is green
            if (reminder.type.equals("green")) {
                viewBackground.setBackgroundResource(R.drawable.background_reminder_container_green);
            // If the type is red
            } else if (reminder.type.equals("red")) {
                viewBackground.setBackgroundResource(R.drawable.background_reminder_container_red);
            // If the type is blue
            } else if (reminder.type.equals("blue")) {
                viewBackground.setBackgroundResource(R.drawable.background_reminder_container_blue);
            }
        }
    }
}
