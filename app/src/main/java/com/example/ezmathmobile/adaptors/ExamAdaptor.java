package com.example.ezmathmobile.adaptors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.activities.TestAddActivity;
import com.example.ezmathmobile.activities.TestManagerActivity;
import com.example.ezmathmobile.databinding.ActivityTestManagerBinding;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Scheduled;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * This is the ExamAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class ExamAdaptor extends RecyclerView.Adapter<ExamAdaptor.ExamViewHolder> {

    private String examID;
    /**
     * This is the constructor for the Adaptor
     */
    public ExamAdaptor() { }

    /**
     * This is an override of the onCreateViewHolder method
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ExamViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Return the view inflated
        return new ExamViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_test_manager, parent, false), parent);
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {}

    /**
     * This is an override of the getItemCount method
     * @return the size of the exams list
     */
    @Override
    public int getItemCount() {
        return 1;
    }

    /**
     * This is the ExamViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        private ConstraintLayout layoutExam;
        // Dependency Objects
        private PreferenceManager preferenceManager;
        private FirebaseFirestore database;
        private ActivityTestManagerBinding binding;
        private Context mainPageLayout;
        private RecyclerView contentView;

        /**
         * This is the ExamViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public ExamViewHolder(@NonNull View itemView, ViewGroup parent) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Bind the objects to the view ID
            layoutExam = itemView.findViewById(R.id.main);
            // Bind the content view ID
            contentView = parent.findViewById(R.id.contentView);
            // Attach the preferences
            preferenceManager = new PreferenceManager(layoutExam.getContext().getApplicationContext());
            // Attach the database
            database = FirebaseFirestore.getInstance();
            // Attach the binding
            this.binding = ActivityTestManagerBinding.bind(itemView);
            // Set the binding for the add new schedule button
            binding.buttonAddTest.setOnClickListener(v -> {
                // Set the adaptor with the current main page
                final ExamAddAdaptor examAddAdaptor = new ExamAddAdaptor();
                contentView.setAdapter(examAddAdaptor);
            });
            // Populate the view
            loadTestDetails();
        }

        /**
         * Method to simplify Toast code, shows a toast of whatever message needs to be displayed
         * @param message Message to be displayed
         */
        private void showToast(String message){
            Toast.makeText(mainPageLayout.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        /**
         * Method to paste test details into test manager view from firestore
         */
        private void loadTestDetails() {
            // Get the userID
            String userID = preferenceManager.getString(Constants.User.KEY_USERID);
            List<Scheduled> scheduled = new ArrayList<>();

            loading(true);
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
                                        Log.d("Test Manager","view added");
                                        loading(false);
                                    })
                                    .addOnFailureListener(exception ->{
                                        loading(false);
                                        showToast(exception.getMessage());
                                    });
                        }
                    })
                    .addOnFailureListener(exception ->{
                        loading(false);
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
            Log.d("Test Manager","Starting Test Item Create");
            // Initialize a Layout Inflator with content for the Layout
            LayoutInflater li = LayoutInflater.from(layoutExam.getContext());
            // Create inflated view
            View testView = li.inflate(R.layout.exam_item_container, binding.testContainer, false);

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
            testView.findViewById(R.id.testDelete).setOnClickListener(v -> deleteTest(examID));
            testView.findViewById(R.id.testEdit).setOnClickListener(v -> editTest(examID,exam));

            return testView;
        }

        /**
         * Delete the test
         * @param examID this is the examID to delete
         */
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
                                        Toast.makeText(mainPageLayout, "Test successfully deleted", Toast.LENGTH_SHORT).show();
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
         * With the edit test method, we will return to the addtest activity. However, we will take the
         * information within that view and return it to the addtest activity, wherein that activity can
         * populate the edit text areas with that information.
         * @param examID ID of specified exam needing to be changed
         */
        private void editTest(String examID, Scheduled exam) {
            // Set the adaptor with the current main page
            final ExamAddAdaptor examAddAdaptor = new ExamAddAdaptor(examID, exam.getName(),exam.getDate());
            contentView.setAdapter(examAddAdaptor);

        }

        /**
         * Setting up progress bar visibility if user is loading or not
         * @param isLoading Whether the program is loading or not
         */

        private void loading(Boolean isLoading){
            if(isLoading){
                binding.testContainer.setVisibility(View.INVISIBLE);
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.INVISIBLE);
                binding.testContainer.setVisibility(View.VISIBLE);
            }
        }
    }
}