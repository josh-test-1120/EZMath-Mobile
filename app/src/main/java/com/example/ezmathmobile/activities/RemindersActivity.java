/**
 * This is the Activity Controller of the Reminders page. Data from the database would be used to
 * show on the screen here.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.adaptors.ReminderGroupAdapter;
import com.example.ezmathmobile.databinding.ActivityRemindersBinding;
import com.example.ezmathmobile.models.Reminder;
import com.example.ezmathmobile.models.ReminderDateBlock;
import com.example.ezmathmobile.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemindersActivity extends AppCompatActivity {

    /**
     * Class fields. Includes View data binding
     */
    private ActivityRemindersBinding binding;

    /**
     * A overridden method that creates the activity, fetches the data from the Firestore database,
     * populates the layouts, and binds them to display on the screen.
     * @param savedInstanceState A instance state of the application.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRemindersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Connecting recycler view to GUI
        RecyclerView reminderWithDateRecyclerView = findViewById(R.id.remindersWithDateRecyclerView);
        loading(true);

        // Creating the outer ReminderDateblock Arraylist that holds the categories
        List<ReminderDateBlock> remindersWithDateList = new ArrayList<>();

        //Creating Hashmap
        Map<String, List<Reminder>> remindersByDays = new HashMap<>();

        // Populating with data from the database
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_REMINDERS)
                .orderBy(Constants.KEY_REMINDER_DATETIME, Query.Direction.DESCENDING)
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
                                reminder = new Reminder(document.getString(Constants.KEY_REMINDER_TEXT),
                                        document.getString(Constants.KEY_REMINDER_TYPE), LocalDateTime.parse(document.getString(Constants.KEY_REMINDER_DATETIME)));
                                remindersByDays.computeIfAbsent(reminder.date.format(DateTimeFormatter
                                        .ofPattern("MMMM d, yyyy")), k -> new ArrayList<>()).add(reminder);
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
