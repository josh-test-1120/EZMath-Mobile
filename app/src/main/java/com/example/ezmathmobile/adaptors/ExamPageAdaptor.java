package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.databinding.ActivityMainBinding;
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
import java.util.LinkedHashMap;
import java.util.List;


/**
 * This is the ExamPageAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class ExamPageAdaptor extends RecyclerView.Adapter<ExamPageAdaptor.ExamPageViewHolder> {

    /**
     * This is the constructor for the Adaptor
     */
    public ExamPageAdaptor() { }

    /**
     * This is an override of the onCreateViewHolder method
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ExamViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public ExamPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Return the view inflated
        return new ExamPageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_test_manager, parent, false), parent);
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ExamPageViewHolder holder, int position) {

    }

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
    public static class ExamPageViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        private ConstraintLayout layoutExam;
        // Dependency Objects
        private PreferenceManager preferenceManager;
        private FirebaseFirestore database;
        private ActivityTestManagerBinding binding;
        private ActivityMainBinding parentBinding;
        private Context mainPageLayout;
        private RecyclerView contentView;
        private ViewGroup parent;

        private RecyclerView testContainer;

        /**
         * This is the ExamViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public ExamPageViewHolder(@NonNull View itemView, ViewGroup parent) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Bind the objects to the view ID
            layoutExam = itemView.findViewById(R.id.main);
            this.parent = parent;

            // Bind the content view ID
            contentView = parent.findViewById(R.id.contentView);
            // Attach the preferences
            preferenceManager = new PreferenceManager(layoutExam.getContext().getApplicationContext());
            // Attach the database
            database = FirebaseFirestore.getInstance();
            // Attach the binding
            this.binding = ActivityTestManagerBinding.bind(itemView);
            testContainer = itemView.findViewById(R.id.testContainer);
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

            loading(true);
            database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                    .whereEqualTo(Constants.Scheduled.KEY_SCHEDULED_USERID,userID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        final List<Scheduled> scheduled = new ArrayList<>();
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
                                        // Once all elements have been processed
                                        if (scheduled.size() == queryDocumentSnapshots.size()) {
                                            loading(false);
                                            createTestViews(scheduled);
                                        }
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
         * @param exams List of Scheduled to display
         * @return testView which is the container with all the test details
         */
        private void createTestViews(List<Scheduled> exams) {
            Log.d("Test Manager Month","Starting month build");
            // Convert the exams to Hash by month
            LinkedHashMap<String,List<Scheduled>> examsByMonth = TimeConverter.sortByMonthExams(exams);

            Log.d("ExamMonths Size",Integer.toString(examsByMonth.size()));
            ExamMonthAdaptor examMonthAdaptor = new ExamMonthAdaptor(examsByMonth, parent);
            testContainer.setAdapter(examMonthAdaptor);

        }

        /**
         * Setting up progress bar visibility if user is loading or not
         * @param isLoading Whether the program is loading or not
         */
        private void loading(Boolean isLoading){
            if(isLoading){
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}