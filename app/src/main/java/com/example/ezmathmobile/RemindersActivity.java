/**
 * This is the Activity Controller of the Reminders page. Data from the database would be used to
 * show on the screen here.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.adaptors.ReminderAdapter;
import com.example.ezmathmobile.databinding.ActivityRemindersBinding;
import com.example.ezmathmobile.models.Reminder;
import com.example.ezmathmobile.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {

    /**
     * Class fields. Includes View data binding
     */
    private ActivityRemindersBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRemindersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Connecting recycler view to GUI
        RecyclerView reminderRecyclerView = findViewById(R.id.remindersWithDateRecyclerView);

        // Reminders list
        List<Reminder> remindersList = new ArrayList<>();
        loading(true);

        Reminder r1 = new Reminder("R1 test has been successfully scheduled", "green");
        remindersList.add(r1);

        Reminder r2 = new Reminder("You have 1 upcoming test today", "blue");
        remindersList.add(r2);

        Reminder r3 = new Reminder("R1 test starts in 15 minutes", "red");
        remindersList.add(r3);

//        // Populating with data from the database
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        database.collection(Constants.KEY_COLLECTION_REMINDERS)
//                // Getting the query results
//                .get()
//                // When the query is successful
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful() && task.getResult() != null
//                            && task.getResult().getDocuments().size() > 0) {
//                        // Iterating over reminder query results
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Reminder reminder = new Reminder(document.getString("text"),
//                                    document.getString("type"));
//
//                            // Adding reminder to ArrayList
//                            remindersList.add(reminder);
//                        }
//                    }
//                    // Removing Progressbar
//                    loading(false);
//                });


        //Declaring the Reminder adapter
        final ReminderAdapter reminderAdapter = new ReminderAdapter(remindersList);
        reminderRecyclerView.setAdapter(reminderAdapter);
        loading(false);
    }

    /**
     * A helper function that controls the ProgressBar visibility depending
     * on the loading state.
     * @param isLoading Boolean type value that indicates loading or not.
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}
