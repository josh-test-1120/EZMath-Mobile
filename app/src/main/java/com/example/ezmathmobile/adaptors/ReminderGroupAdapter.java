/**
 * This is the Adapter that uses the reminders layout that groups the reminders by their days.
 * It also uses the individual ReminderAdapter to display and bind each reminder block inside the
 * categorized day blocks.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile.adaptors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.ReminderDateBlock;

import java.util.List;

public class ReminderGroupAdapter extends RecyclerView.Adapter<ReminderGroupAdapter.ReminderGroupViewHolder> {

    /**
     * A arraylist class field that holds grouped reminder with date objects.
     */
    private List<ReminderDateBlock> reminderGroups;

    /**
     * A constructor to connect the field for this class.
     * @param reminderGroups An ArrayList of grouped reminders with date objects.
     */
    public ReminderGroupAdapter(List<ReminderDateBlock> reminderGroups) {
        this.reminderGroups = reminderGroups;
    }

    /**
     * An overridden action listener method that inflates the reminder_withdate layout to the
     * application view screen.
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return Returns a newly declared ReminderGroupViewHolder object with the inflated layout.
     */
    @NonNull
    @Override
    public ReminderGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_reminders_withdate, parent, false);
        return new ReminderGroupViewHolder(view);
    }

    /**
     * An overridden action listener that connects the reminder data to the RecyclerView
     * elements and populates it.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReminderGroupViewHolder holder, int position) {
        // Get the current ReminderGroup
        ReminderDateBlock group = reminderGroups.get(position);

        String dateText = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateText = group.date;
        }
        // Bind the date to the TextView
        holder.date.setText(dateText);

        // Set up the nested RecyclerView for this ReminderGroup
        ReminderAdapter reminderAdapter = new ReminderAdapter(group.reminderList);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext()));
        holder.recyclerView.setAdapter(reminderAdapter);
    }

    /**
     * An overridden method that returns the size of the reminderGroups ArrayList.
     * @return Size of reminderGroups ArrayList.
     */
    @Override
    public int getItemCount() {
        return reminderGroups.size();
    }


    /**
     * A class of ViewHolder that gets the references of the GUI elements and connects them
     * to local variables.
     */
    class ReminderGroupViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        RecyclerView recyclerView;

        public ReminderGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.reminderDateHeader);
            recyclerView = itemView.findViewById(R.id.remindersRecyclerView);
        }
    }
}


