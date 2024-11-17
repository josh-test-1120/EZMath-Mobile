package com.example.ezmathmobile.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivityTestManagerBinding;
import com.example.ezmathmobile.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class TestManagerActivity extends AppCompatActivity {
    private FirebaseFirestore database;
    private ActivityTestManagerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseFirestore.getInstance();

        loadTestDetails();
    }

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
        //loading(true);
        database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    //Clearing up views before loading everything in
                   binding.testContainer.removeAllViews();
                    //Getting details from firestore
                   for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                       String examID = document.getString(Constants.Exam.KEY_EXAM_ID);
                       String examTime = document.getString(Constants.Exam.KEY_TEST_TIME);
                       String examDate = document.getString(Constants.Exam.KEY_TEST_DATE);
                        //Calling createNewTestView to set the text into each of the xml components
                       View testView = createTestView(examID, examTime, examDate);
                       binding.testContainer.addView(testView);
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
     * @param examID from previous method (firestore)
     * @param examTime from previous method (firestore)
     * @param examDate from previous method (firestore)
     * @return testView which is the container with all the test details
     */
    private View createTestView(String examID, String examTime, String examDate) {
        View testView = getLayoutInflater().inflate(R.layout.exam_item_container, binding.testContainer, false);

        //Pull text views from item container and put them into variables in java
        TextView examIDView = testView.findViewById(R.id.testName);
        TextView examTimeView = testView.findViewById(R.id.testTime);
        TextView examDateView = testView.findViewById(R.id.testDate);

        //Setting the view texts to whatever was given
        examIDView.setText(examID);
        examTimeView.setText(examTime);
        examDateView.setText(examDate);

        //Add some listeners for the delete and edit test buttons
        testView.findViewById(R.id.testDelete).setOnClickListener(v -> deleteTest(examID));
        //testView.findViewById(R.id.testEdit).setOnClickListener(v -> editTest(examID));

        return testView;
    }

    private void deleteTest(String examID) {
        database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                .whereEqualTo(Constants.Exam.KEY_EXAM_ID, examID)
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