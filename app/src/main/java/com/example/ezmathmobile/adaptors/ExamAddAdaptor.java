package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.activities.TestManagerActivity;
import com.example.ezmathmobile.databinding.ActivityTestAddBinding;
import com.example.ezmathmobile.databinding.ActivityTestManagerBinding;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;


/**
 * This is the ExamAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class ExamAddAdaptor extends RecyclerView.Adapter<ExamAddAdaptor.ExamAddViewHolder> {
    // These are the private variables
    private String examID, examName, e;
    private Timestamp examDate;

    /**
     * This is the constructor for the Adaptor
     * @param examID This is a string of the examID
     * @param examName This is a string of the examName
     * @param examDate This is a firebase timestamp of the examDate
     */
    public ExamAddAdaptor(String examID, String examName, Timestamp examDate) {
        this.examID = examID;
        this.examName = examName;
        this.examDate = examDate;
    }

    /**
     * This is the constructor for the Adaptor
     */
    public ExamAddAdaptor() {
        this.examID = null;
        this.examName = null;
        this.examDate = null;
    }

    /**
     * This is an override of the onCreateViewHolder method
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ExamViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public ExamAddViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExamAddViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_test_add,
                parent, false),parent,examID,examName,examDate);
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ExamAddViewHolder holder, int position) { }

    /**
     * This is an override of the getItemCount method
     * @return the size of the exams list
     */
    @Override
    public int getItemCount() { return 1; }

    /**
     * This is the ExamViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class ExamAddViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        private ConstraintLayout layoutAddExam;
        // Dependency Objects
        private PreferenceManager preferenceManager;
        private FirebaseFirestore database;
        private ActivityTestAddBinding binding;
        private Context mainPageLayout;
        private RecyclerView contentView;

        //View viewBackground;
        private String examID, examName;
        private Timestamp examDate;

        /**
         * This is the ExamViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public ExamAddViewHolder(@NonNull View itemView, ViewGroup parent, String examID, String examName, Timestamp examDate) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Initialize the variables
            this.examID = examID;
            this.examName = examName;
            this.examDate = examDate;
            // Bind the content view ID
            contentView = parent.findViewById(R.id.contentView);
            // Bind the objects to the view ID
            layoutAddExam = itemView.findViewById(R.id.main);
            // Attach the preferences
            preferenceManager = new PreferenceManager(layoutAddExam.getContext().getApplicationContext());
            // Attach the database
            database = FirebaseFirestore.getInstance();
            // Attach the binding
            this.binding = ActivityTestAddBinding.bind(itemView);
            // Setup the listeners and calendar
            onListeners();
            setupCalendar(binding);
            // Populate the view
            binding.inputTestExam.setText(examName);
            if (examDate != null) {
                // Get localized string from the timestamp
                String time = TimeConverter.localizeTime(examDate);
                String date = TimeConverter.localizeDate(examDate);
                // Update the time field
                binding.inputTestTime.setText(time);
            }
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
                // Set the adaptor with the current main page
                final ExamAdaptor examAdaptor = new ExamAdaptor();
                contentView.setAdapter(examAdaptor);
            });
        }

        /**
         * Method to simplify Toast code, shows a toast of whatever message needs to be displayed
         *
         * @param message Message to be displayed
         */
        private void showToast(String message) {
            Toast.makeText(mainPageLayout.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

            database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                    .add(exam)
                    .addOnSuccessListener(documentReference -> {
                        //Save exam details to preference manager
                        preferenceManager.putBoolean(Constants.User.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.Exam.KEY_TEST_TIME, binding.inputTestTime.getText().toString());
//                    preferenceManager.putString(Constants.Exam.KEY_TEST_DATE, binding.inputTestDate.getText().toString());
//                    preferenceManager.putString(Constants.Exam.KEY_EXAM_ID, binding.inputTestExam.getText().toString());
                        preferenceManager.putString(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());

                        loading(false);
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
}