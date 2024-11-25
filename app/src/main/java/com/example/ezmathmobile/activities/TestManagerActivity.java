package com.example.ezmathmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivityTestManagerBinding;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestManagerActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private ActivityTestManagerBinding binding;
    private PreferenceManager preferenceManager;
    private final int ADDEXAM_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Attach the preferences
        preferenceManager = new PreferenceManager(getApplicationContext());

        database = FirebaseFirestore.getInstance();

        loadTestDetails();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == ADDEXAM_ACTIVITY) {
//            if(resultCode == Activity.RESULT_OK){
//                String result=data.getStringExtra("result");
//            }
//            if (resultCode == Activity.RESULT_CANCELED) {
//                // Write your code if there's no result
//            }
//        }
//    } //onActivityResult

    /**
     * Method to simplify Toast code, shows a toast of whatever message needs to be displayed
     * @param message Message to be displayed
     */
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method to paste test details into test manager view from firestore
     */
    private void loadTestDetails() {
        // Get the userID
        String userID = preferenceManager.getString(Constants.User.KEY_USERID);
        List<Scheduled> scheduled = new ArrayList<>();

        //loading(true);
        database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                .whereEqualTo(Constants.Scheduled.KEY_SCHEDULED_USERID,userID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    //Clearing up views before loading everything in
                    binding.testContainer.removeAllViews();
                    //Getting details from firestore
                    for (DocumentSnapshot schedule : queryDocumentSnapshots) {
                        // Serialize the document to the class
                        Scheduled scheduleDB = schedule.toObject(Scheduled.class);
                        String scheduledID = schedule.getId();

                        database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                                .document(scheduleDB.getExamid())
                                .get()
                                .addOnSuccessListener(exam -> {
                                    // Serialize the document to the class
                                    Exam examDB = exam.toObject(Exam.class);
                                    if (examDB != null) scheduleDB.setName(examDB.getName());
                                    // Add scheduled to list
                                    scheduled.add(scheduleDB);
                                    View testView = createTestView(scheduleDB, scheduledID);
                                    binding.testContainer.addView(testView);
                                })
                                .addOnFailureListener(exception ->{
                                    //loading(false);
                                    showToast(exception.getMessage());
                                });
                    }
                })
                .addOnFailureListener(exception ->{
                    //loading(false);
                    showToast(exception.getMessage());
                });
    }

    /**
     * Creating a view and putting all my test details into it from previous method. This is putting
     * the details specifically into my exam item container view
     * @param exam from previous method (firestore)
     * @return testView which is the container with all the test details
     */
    private View createTestView(Scheduled exam, String examID) {
        View testView = getLayoutInflater().inflate(R.layout.exam_item_container, binding.testContainer, false);

        //Pull text views from item container and put them into variables in java
        TextView examIDView = testView.findViewById(R.id.testName);
        TextView examTimeView = testView.findViewById(R.id.testTime);
        TextView examDateView = testView.findViewById(R.id.testDate);

        // Get localized string from the timestamp
        String time = TimeConverter.localizeTime(exam.getDate());
        String date = TimeConverter.localizeDate(exam.getDate());

        //Setting the view texts to whatever was given
        examIDView.setText(exam.getName());
        examTimeView.setText(time);
        examDateView.setText(date);

        //Add some listeners for the delete and edit test buttons
        testView.findViewById(R.id.testDelete).setOnClickListener(v -> {
            deleteTest(examID);
            AddDeletionReminder(examID);
        });
        testView.findViewById(R.id.testEdit).setOnClickListener(v -> editTest(examID,exam));

        return testView;
    }

    private void deleteTest(String examID) {
        database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                .whereEqualTo(FieldPath.documentId(), examID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                                .document(document.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Test successfully deleted", Toast.LENGTH_SHORT).show();
                                    //Refresh test list
                                    loadTestDetails();
                                })
                                .addOnFailureListener(e -> {
                                    showToast(e.getMessage());
                                });
                    }
                });
    }

    /**
     * A method that adds a test deletion confirmation reminder in the Firebase database.
     */
    private void AddDeletionReminder(String examID) {
        // Declaring database and Hashmap
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> hashMap = new HashMap<>();

        // Adding data to the Hashmap
        // Datetime
        hashMap.put(Constants.Reminders.KEY_REMINDER_DATETIME, LocalDateTime.now().toString());
        // Adding text
        hashMap.put(Constants.Reminders.KEY_REMINDER_TEXT, examID + " test has been successfully" +
                "deleted");
        // Adding type
        hashMap.put(Constants.Reminders.KEY_REMINDER_TYPE, "red");

        // Adding Reminder data into the database
        database.collection(Constants.Reminders.KEY_COLLECTION_REMINDERS)
                .add(hashMap);
    }

    /**
     * With the edit test method, we will return to the addtest activity. However, we will take the
     * information within that view and return it to the addtest activity, wherein that activity can
     * populate the edit text areas with that information.
     * @param examID ID of specified exam needing to be changed
     */
    private void editTest(String examID, Scheduled exam) {
        binding.buttonAddTest.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TestAddActivity.class);
            intent.putExtra("examID", examID);
            intent.putExtra("examDate",TimeConverter.timestampToString(exam.getDate()));
            intent.putExtra("examName", exam.getName());
//            intent.putExtra("classID", preferenceManager.getString(Constants.Exam.KEY_CLASS_ID));
            startActivity(intent);
        });

    }

    /**
     * Setting up progress bar visibility if user is loading or not
     * @param isLoading Whether the program is loading or not
     */
    /*
    Still figuring out where to put the progress bar
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.testContainer.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.testContainer.setVisibility(View.VISIBLE);
        }
    }*/

}