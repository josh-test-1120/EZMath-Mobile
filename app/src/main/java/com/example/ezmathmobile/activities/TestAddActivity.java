package com.example.ezmathmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ezmathmobile.databinding.ActivityTestAddBinding;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class TestAddActivity extends AppCompatActivity {

    private ActivityTestAddBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        onListeners();
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
        exam.put(Constants.Exam.KEY_TEST_DATE, binding.inputTestDate.getText().toString());
        exam.put(Constants.Exam.KEY_EXAM_ID, binding.inputTestExam.getText().toString());
        exam.put(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());

        database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                .add(exam)
                .addOnSuccessListener(documentReference -> {
                    //Save exam details to preference manager
                    preferenceManager.putBoolean(Constants.User.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.Exam.KEY_TEST_TIME, binding.inputTestTime.getText().toString());
                    preferenceManager.putString(Constants.Exam.KEY_TEST_DATE, binding.inputTestDate.getText().toString());
                    preferenceManager.putString(Constants.Exam.KEY_EXAM_ID, binding.inputTestExam.getText().toString());
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
        } else if (binding.inputTestDate.getText().toString().trim().isEmpty()) {
            showToast("Please Enter test date");
            return false;
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
}