package com.example.ezmathmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ezmathmobile.databinding.ActivityTestAddBinding;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.util.Date;
import java.util.HashMap;

public class TestAddActivity extends AppCompatActivity {

    private ActivityTestAddBinding binding;
    private PreferenceManager preferenceManager;
    private String examID;
    private Timestamp timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //If edit test button was pressed...
        if(getIntent().getExtras() != null) {
//            binding.inputTestClass.setText(getIntent().getStringExtra("classID"));
            examID = getIntent().getStringExtra("examID");
            binding.inputTestExam.setText(getIntent().getStringExtra("examName"));
            timestamp = TimeConverter.stringToTimestamp(getIntent().getStringExtra("examDate"));
        }

        preferenceManager = new PreferenceManager(getApplicationContext());

        onListeners();
        setupCalendar(binding);
    }

    /**
     * Listeners for submit and cancel buttons. Either test will be added or user will
     * be taken back to test manager
     */
    private void onListeners() {
        binding.buttonSubmit.setOnClickListener(v -> {
            if (isValidExamDetails()) {
                AddTest();
            }
        });
        binding.buttonCancel.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), TestManagerActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Method to simplify Toast code, shows a toast of whatever message needs to be displayed
     *
     * @param message Message to be displayed
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Method for adding test, user will be sent back to test manager activity and
     * test will be added to the database
     */
    private void AddTest() {
        loading(true);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> exam = new HashMap<>();
        exam.put(Constants.Exam.KEY_TEST_TIME, binding.inputTestTime.getText().toString());
//        exam.put(Constants.Exam.KEY_TEST_DATE, binding.inputTestDate.getText().toString());
//        exam.put(Constants.Exam.KEY_EXAM_ID, binding.inputTestExam.getText().toString());
        exam.put(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());

        database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                .add(exam)
                .addOnSuccessListener(documentReference -> {
                    //Save exam details to preference manager
                    preferenceManager.putBoolean(Constants.User.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.Exam.KEY_TEST_TIME, binding.inputTestTime.getText().toString());
//                    preferenceManager.putString(Constants.Exam.KEY_TEST_DATE, binding.inputTestDate.getText().toString());
//                    preferenceManager.putString(Constants.Exam.KEY_EXAM_ID, binding.inputTestExam.getText().toString());
                    preferenceManager.putString(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());

                    Intent intent = new Intent(getApplicationContext(), TestManagerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }).addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    /**
     * Validating user details for each of the edit texts
     *
     * @return Whether or not the details are valid
     */
    private Boolean isValidExamDetails() {
        if (binding.inputTestTime.getText().toString().trim().isEmpty()) {
            showToast("Please Enter test time");
            return false;
//        } else if (binding.calendarView.getText().toString().trim().isEmpty()) {
//            showToast("Please Enter test date");
//            return false;
        } else if (binding.inputTestExam.getText().toString().trim().isEmpty()) {
            showToast("Please Enter exam ID");
            return false;
        } else if (binding.inputTestClass.getText().toString().trim().isEmpty()) {
            showToast("Please enter class ID");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Setting up progress bar visibility if user is loading or not
     *
     * @param isLoading Whether the program is loading or not
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSubmit.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSubmit.setVisibility(View.VISIBLE);
        }
    }

    private void setupCalendar(ActivityTestAddBinding binding) {
        // Add Listener in calendar
        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                // In this Listener have one method
                // and in this method we will
                // get the value of DAYS, MONTH, YEARS
                public void onSelectedDayChange(
                        @NonNull CalendarView view,
                        int year,
                        int month,
                        int dayOfMonth)
                {

                    // Store the value of date with
                    // format in String type Variable
                    // Add 1 in month because month
                    // index is start with 0
                    String Date
                            = dayOfMonth + "-"
                            + (month + 1) + "-" + year;

                    // set this date in TextView for Display
                    //binding.date_view.setText(Date);
                }
            });
    }
}