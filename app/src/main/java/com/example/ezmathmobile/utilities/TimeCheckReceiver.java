package com.example.ezmathmobile.utilities;

import static com.example.ezmathmobile.utilities.TimeConverter.localizeDate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeCheckReceiver extends BroadcastReceiver {
//    Context mainPageContext;
    PreferenceManager preferenceManager;
    FirebaseFirestore database;
    @Override
    public void onReceive(Context context, Intent intent) {
        // Perform your action
        Log.d("TimeCheckReceiver", "It's the right time! Performing action.");

        // Connecting field values of preferenceManager and database
        preferenceManager = new PreferenceManager(context.getApplicationContext());
        database = FirebaseFirestore.getInstance();
        getQueryCount(preferenceManager).addOnSuccessListener(this::SendReminder);

    }

    private Task<Integer> getQueryCount(PreferenceManager preferenceManager) {
        TaskCompletionSource<Integer> taskCompletionSource = new TaskCompletionSource<>();
        // Declaring local counter variable
        AtomicInteger count = new AtomicInteger();
        // UserID local variable
        String userID = preferenceManager.getString(Constants.User.KEY_USERID);

        // Query the database
        database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                .whereEqualTo(Constants.User.KEY_USERID, userID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            // Getting and localizing the date from the database document
                            String documentDate = localizeDate(document.getTimestamp("date"));
                            // Getting the local date now and formatting it
                            String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                            if (documentDate.equals(localDate)) {
                                count.getAndIncrement();
                            }
                        }
                    }
                    taskCompletionSource.setResult(count.get());
                });
        return taskCompletionSource.getTask();
    }
    private void SendReminder(int count) {
        // Declaring database and Hashmap
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> hashMap = new HashMap<>();

        // Adding data to the Hashmap
        // Datetime
        hashMap.put(Constants.Reminders.KEY_REMINDER_DATETIME, com.google.firebase.Timestamp.now());
        // Adding text
        if (count == 0) {
            hashMap.put(Constants.Reminders.KEY_REMINDER_TEXT, "No tests scheduled for today.");
        } else {
            hashMap.put(Constants.Reminders.KEY_REMINDER_TEXT, "There are " + count + " tests scheduled for today.");
        }
        // Adding type
        hashMap.put(Constants.Reminders.KEY_REMINDER_TYPE, "blue");

        // Adding Reminder data into the database
        database.collection(Constants.Reminders.KEY_COLLECTION_REMINDERS)
                .add(hashMap);
    }
}


