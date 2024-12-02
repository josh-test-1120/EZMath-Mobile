package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivityRemindersBinding;
import com.example.ezmathmobile.models.Reminder;
import com.example.ezmathmobile.models.ReminderDateBlock;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
     * @return A new ReminderPageViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public ReminderPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReminderPageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_reminders, parent, false),parent);
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
        // Dependency Objects
        private PreferenceManager preferenceManager;
        private FirebaseFirestore database;
        private ActivityRemindersBinding binding;
        private Context mainPageLayout;
        private RecyclerView contentView;

        /**
         * This is the ReminderPageViewHolder constructor
         * @param itemView the view that is to be inflated
         * @param parent this is the parent View for references
         */
        public ReminderPageViewHolder(@NonNull View itemView, ViewGroup parent) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Bind the objects to the view IDs
            layoutReminder = itemView.findViewById(R.id.reminders);
            // Bind the content view ID
            contentView = parent.findViewById(R.id.contentView);
            // Attach the preferences
            preferenceManager = new PreferenceManager(layoutReminder.getContext().getApplicationContext());
            // Attach the database
            database = FirebaseFirestore.getInstance();
            // Attach the binding
            this.binding = ActivityRemindersBinding.bind(itemView);

            // Connecting recycler view to GUI
            RecyclerView reminderWithDateRecyclerView = itemView.findViewById(R.id.remindersWithDateRecyclerView);
            loading(true);

            // Creating the outer ReminderDateblock Arraylist that holds the categories
            List<ReminderDateBlock> remindersWithDateList = new ArrayList<>();

            //Creating Hashmap
            Map<String, List<Reminder>> remindersByDays = new LinkedHashMap<>();

            // Populating with data from the database
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            // User ID
            String userid = preferenceManager.getString(Constants.User.KEY_USERID).trim();
            // Executing the query
            database.collection(Constants.Reminders.KEY_COLLECTION_REMINDERS)
                    .orderBy(Constants.Reminders.KEY_REMINDER_DATETIME, Query.Direction.DESCENDING)
                    .whereEqualTo(Constants.User.KEY_USERID, userid)
                    // Getting the query results
                    .get()
                    // When the query is successful
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null
                                && task.getResult().getDocuments().size() > 0) {
                            // Iterating over reminder query results
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Creating individual reminder object
                                Reminder reminder = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    reminder = new Reminder(document.getString(Constants.Reminders.KEY_REMINDER_TEXT),
                                            document.getString(Constants.Reminders.KEY_REMINDER_TYPE), document.getTimestamp(Constants.Reminders.KEY_REMINDER_DATETIME));
                                    remindersByDays.computeIfAbsent(
                                            new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(reminder.date.toDate()),
                                            k -> new ArrayList<>()).add(reminder);
                                }
                            }
                            // Traversing HashMap to compute the remindersWithDateList
                            for (Map.Entry<String, List<Reminder>> entry : remindersByDays.entrySet()) {
                                ReminderDateBlock reminderDateBlock = new ReminderDateBlock(entry.getValue(), entry.getKey());
                                remindersWithDateList.add(reminderDateBlock);
                            }
                        }
                        //Declaring the Reminder adapter
                        final ReminderGroupAdapter reminderWithDateAdapter = new ReminderGroupAdapter(remindersWithDateList);
                        reminderWithDateRecyclerView.setAdapter(reminderWithDateAdapter);
                        loading(false);
                    });

        }

        /**
         * A helper function that controls the ProgressBar visibility depending
         * on the loading state.
         * @param isLoading Boolean type value that indicates loading or not.
         */
        private void loading(Boolean isLoading) {
            // If loading, display progress bar, else make it invisible.
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}