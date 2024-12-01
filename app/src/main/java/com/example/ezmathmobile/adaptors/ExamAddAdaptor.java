package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivityTestAddBinding;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.models.Time;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This is the ExamAddAdaptor that is used in the RecycleView Adaptor
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
    public ExamAddAdaptor(Scheduled test, String examID, String examName, Timestamp examDate) {
        this.examID = examID;
        this.examName = examName;
        this.examDate = examDate;
    }

    /**
     * This is the default constructor (empty) for the Adaptor
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
     * @return A new ExamAddViewHolder class that has its layout inflated
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
     * This is the ExamAddViewHolder class that extends the RecycleView.ViewHolder
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
        private Spinner examNames, examTimes;
        // These are the variables and adaptors
        private String testID, examID, examName;
        private Timestamp examDate;
        private ExamNameAdaptor examNameAdaptor;
        private ExamTimeAdaptor examTimeAdaptor;

        /**
         * This is the ExamAddViewHolder constructor
         * @param itemView the view that is to be inflated
         * @param parent this is the parent view for reference
         * @param examID this is the string reprenstation of the examID
         * @param examName this is the exam name
         * @param examDate this is a Timestamp object of the time and date
         */
        public ExamAddViewHolder(@NonNull View itemView, ViewGroup parent, String examID, String examName, Timestamp examDate) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Initialize the variables
            this.examID = examID;
            this.testID = examID;
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
            //Setup a listener for clicking outside edit texts
            setupOutsideClickListener();
            if (examName != null) {
                // Set the exam name spinner
                List<String> names = new ArrayList<>();
                names.add(examName);
                // Set the exam name adaptor for the exam name
                ExamNameAdaptor examNameAdaptor = new ExamNameAdaptor(mainPageLayout.getApplicationContext(), names);
                binding.inputTestExam.setAdapter(examNameAdaptor);
                buildExamTimeList(binding);
            }
            else buildExamNameList(binding);
        }

        /**
         * Listeners for submit and cancel buttons. Either test will be added or user will
         * be taken back to test manager
         */
        private void onListeners() {
            // Setup the submit button listener
            binding.buttonSubmit.setOnClickListener(v -> {
                if (isValidExamDetails()) {
                    AddTest();
                }
            });
            // Setup the cancel button listener
            binding.buttonCancel.setOnClickListener(v -> {
                // Set the adaptor with the current main page
                final ExamPageAdaptor examPageAdaptor = new ExamPageAdaptor();
                contentView.setAdapter(examPageAdaptor);
            });
            // Set the listener for the exam name spinner
            binding.inputTestExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                /**
                 * Override for the OnItemSelected method
                 * @param parent this is the adapter object
                 * @param view this is the adapter view
                 * @param position this is the position in the array
                 * @param id this is the row id of the item
                 */
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // Get the item clicked
                    //int index = parent.getSelectedItemPosition();
                    examName = (String) parent.getItemAtPosition(position);
                    //Toast.makeText(parent.getContext(), "Exam Name " + name, Toast.LENGTH_SHORT).show();
                    // Build the exam times
                    buildExamTimeList(binding);
                    // Update the classID

                }

                /**
                 * This is the override for nothing being selected
                 * This is an empty method as we do nothing here
                 * @param parent this is the adapter object
                 */
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
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
            String userID = preferenceManager.getString(Constants.User.KEY_USERID);

            HashMap<String, String> exam = new HashMap<>();
            // Get the time from the time spinner
            int timeIndex = examTimes.getSelectedItemPosition();
            exam.put(Constants.Scheduled.KEY_SCHEDULED_TIME, (String) examTimes.getItemAtPosition(timeIndex));
//            exam.put(Constants.Scheduled.KEY_SCHEDULED_DATE, Timestamp.now());
            exam.put(Constants.Scheduled.KEY_SCHEDULED_EXAMID, examID);
            exam.put(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());
            exam.put(Constants.User.KEY_USERID, userID);

            // Get the exam ID from the exam name
            database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                    .whereEqualTo(Constants.Exam.KEY_TEST_NAME,examName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Serialize the document to the class
                        Exam examDB = queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class);
                        examID = queryDocumentSnapshots.getDocuments().get(0).getId();
                        
                        Log.d("Test Add ExamID",examID);
                        exam.put(Constants.Scheduled.KEY_SCHEDULED_EXAMID,examID);

                        database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                                .add(exam)
                                .addOnSuccessListener(documentReference -> {

                                    documentReference.update(Constants.Scheduled.KEY_SCHEDULED_DATE,Timestamp.now());
                                    // Save exam details to preference manager
                                    preferenceManager.putString(Constants.Scheduled.KEY_SCHEDULED_TIME, (String) examTimes.getItemAtPosition(timeIndex));
    //                        preferenceManager.putString(Constants.Scheduled.KEY_SCHEDULED_DATE, binding.inputTestDate.getText().toString());
                                    preferenceManager.putString(Constants.Scheduled.KEY_SCHEDULED_EXAMID, examName);
                                    preferenceManager.putString(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());

                                    // Set the adaptor with the current main page
                                    final ExamPageAdaptor examPageAdaptor = new ExamPageAdaptor();
                                    contentView.setAdapter(examPageAdaptor);

                                    loading(false);
                                }).addOnFailureListener(exception -> {
                                    loading(false);
                                    showToast(exception.getMessage());
                                });

                    });
        }

        /**
         * Validating user details for each of the edit texts
         * @return Whether or not the details are valid
         */
        private Boolean isValidExamDetails() {
//        } else if (binding.calendarView.getText().toString().trim().isEmpty()) {
//            showToast("Please Enter test date");
//            return false;
             if (binding.inputTestExam.getAdapter().getCount() == 0) {
                showToast("Please Select exam ID");
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

        /**
         * This is to setup the calendar for viewing the days and getting
         * the day result
         * @param binding this is the view bindings
         */
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
                    String day
                            = dayOfMonth + "-"
                            + (month + 1) + "-" + year;
                    Log.d("CalendarSetup:",day);
                    // set this date in TextView for Display
                    //binding.date_view.setText(Date);
                }
            });
        }

        /**
         * Build the ExamTime List based on information from the database
         * @param binding this is the view bindings
         */
        private void buildExamTimeList(ActivityTestAddBinding binding) {
            // Get the list of exams available
            if (examName != null) {
                database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                        .whereEqualTo(Constants.Exam.KEY_TEST_NAME,examName)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            // Serialize the document to the class
                            Exam examDB = queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class);
                            // Bind the class ID to the view
                            binding.inputTestClass.setText(examDB.getClassID());
                            // Ensure a document exists and times are valid
                            if (examDB != null) {
                                Log.d("Test Add ExamID", examDB.getName());
                                if (examDB.getTimes() != null) {
                                    // Wrapped time object for toString handling
                                    List<Time> times = new ArrayList<>();
                                    for (Timestamp timestamp : examDB.getTimes()) {
                                        times.add(new Time(timestamp));
                                    }
                                    // Set the exam time adaptor for the time elements
                                    ExamTimeAdaptor examTimeAdaptor = new ExamTimeAdaptor(mainPageLayout.getApplicationContext(), times);
                                    binding.inputTestTime.setAdapter(examTimeAdaptor);
                                }
                            }
                        });
            }
        }

        /**
         * Build the ExamTime List based on information from the database
         * @param binding this is the view bindings
         */
        private void buildExamNameList(ActivityTestAddBinding binding) {
            // Private variables
            final List<String> names = new ArrayList<>();
            // Get the list of exams available
            database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Iterate through the exams
                        for (DocumentSnapshot exam : queryDocumentSnapshots) {
                            // Serialize the document to the class
                            Exam examDB = exam.toObject(Exam.class);
                            // Add the exam name to the list
                            names.add(examDB.getName());
                            Log.d("Test Build Name Exam Object", examDB.toString());
                            //Log.d("Test Build Name Exam Name", examDB.getName());
                        }
                        // Ensure that names exist
                        if (!names.isEmpty()) {
                            Log.d("Test Build Name Exam List", names.toString());
                            // Set the exam time adaptor for the time elements
                            ExamNameAdaptor examNameAdaptor = new ExamNameAdaptor(mainPageLayout.getApplicationContext(), names);
                            binding.inputTestExam.setAdapter(examNameAdaptor);
                        }
                    });
        }

        /**
         *
         */
        private void setupOutsideClickListener(){
            layoutAddExam.setOnTouchListener((v, event) -> {
                hideKeyboard();
                v.performClick();
                return false;
            });
        }

        /**
         * Using InputMethodManager we can hide the keyboard when clicked outside of the edit text fields
         */
        private void hideKeyboard() {
            InputMethodManager imm = (InputMethodManager) mainPageLayout.getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocus = itemView.findFocus();
            if (imm != null && currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }
}