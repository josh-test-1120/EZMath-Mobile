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

/**
 * This is the ReminderPageAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class ReminderPageAdaptor extends RecyclerView.Adapter<ReminderPageAdaptor.ReminderPageViewHolder> {

    /**
     * This is the constructor for the Adaptor
     */
    public ReminderPageAdaptor() {
    }

    /**
     * This is an override of the onCreateViewHolder method
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ExamViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public ReminderPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReminderPageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_test_manager, parent, false));
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ReminderPageViewHolder holder, int position) {}

    /**
     * This is an override of the getItemCount method
     * @return the size of the exams list
     */
    @Override
    public int getItemCount() {
        return 1;
    }

    /**
     * This is the ReminderPageViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class ReminderPageViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        ConstraintLayout layoutReminder;

        /**
         * This is the ReminderPageViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public ReminderPageViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);
            // Bind the objects to the view IDs
            layoutReminder = itemView.findViewById(R.id.reminders);
        }
    }
}