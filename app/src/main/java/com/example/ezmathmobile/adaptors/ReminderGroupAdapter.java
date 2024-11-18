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

    private List<ReminderDateBlock> reminderGroups;

    public ReminderGroupAdapter(List<ReminderDateBlock> reminderGroups) {
        this.reminderGroups = reminderGroups;
    }

    @NonNull
    @Override
    public ReminderGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_reminders_withdate, parent, false);
        return new ReminderGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderGroupViewHolder holder, int position) {
        // Get the current ReminderGroup
        ReminderDateBlock group = reminderGroups.get(position);

        String dateText = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateText = group.date.getMonth().toString() + " " + group.date.getDayOfMonth();
        }
        // Bind the date to the TextView
        holder.date.setText(dateText);

        // Set up the nested RecyclerView for this ReminderGroup
        ReminderAdapter reminderAdapter = new ReminderAdapter(group.reminderList);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext()));
        holder.recyclerView.setAdapter(reminderAdapter);
    }

    @Override
    public int getItemCount() {
        return reminderGroups.size();
    }


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


