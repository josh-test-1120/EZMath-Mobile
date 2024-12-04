package com.example.ezmathmobile.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.adaptors.ReminderGroupAdapter;
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

public class ReminderPageFragment extends Fragment {
    // These are the private variables
    // These are the objects in the view
    private ConstraintLayout layoutReminder;
    // Dependency Objects
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private ActivityRemindersBinding binding;
    private Context mainPageLayout;
    private FragmentContainerView contentView;

    public ReminderPageFragment() {
        super(R.layout.activity_reminders);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the parent
        ViewGroup parent = (ViewGroup) view.getParent();
        // Get the page context
        mainPageLayout = view.getContext();
        // Bind the objects to the view IDs
        layoutReminder = view.findViewById(R.id.reminders);
        // Bind the content view ID
        contentView = parent.findViewById(R.id.contentView);
        // Attach the preferences
        preferenceManager = new PreferenceManager(layoutReminder.getContext().getApplicationContext());
        // Attach the database
        database = FirebaseFirestore.getInstance();
        // Attach the binding
        this.binding = ActivityRemindersBinding.bind(view);

        // Connecting recycler view to GUI
        RecyclerView reminderWithDateRecyclerView = view.findViewById(R.id.remindersWithDateRecyclerView);
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
