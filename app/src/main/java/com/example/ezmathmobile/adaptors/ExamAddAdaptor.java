package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.graphics.Color;
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
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
    private Scheduled test;
    private List<Timestamp> examTimes;

    /**
     * This is the constructor for the Adaptor
     * @param examID This is a string of the examID
     * @param examName This is a string of the examName
     * @param examDate This is a firebase timestamp of the examDate
     */
    public ExamAddAdaptor(Scheduled test, String examID, String examName, Timestamp examDate, List<Timestamp> examTimes) {
        this.examID = examID;
        this.examName = examName;
        this.examDate = examDate;
        this.test = test;
        this.examTimes = examTimes;
    }

    /**
     * This is the default constructor (empty) for the Adaptor
     */
    public ExamAddAdaptor() {
        this.examID = null;
        this.examName = null;
        this.examDate = null;
        this.test = null;
        this.examTimes = null;
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
                parent, false),parent,test,examID,examName,examDate,examTimes);
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
        // These are the variables and adaptors
        private String testID, examID, examName;
        private Timestamp examDate;
        private ExamNameAdaptor examNameAdaptor;
        private ExamTimeAdaptor examTimeAdaptor;
        private Boolean dateValid = false;
        private List<Timestamp> examTimes;

        /**
         * This is the ExamAddViewHolder constructor
         * @param itemView the view that is to be inflated
         * @param parent this is the parent view for reference
         * @param examID this is the string reprenstation of the examID
         * @param examName this is the exam name
         * @param examDate this is a Timestamp object of the time and date
         */
        public ExamAddViewHolder(@NonNull View itemView, ViewGroup parent, Scheduled test, String examID,
                                 String examName, Timestamp examDate, List<Timestamp> examTimes) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Initialize the variables
            this.examID = examID;

            if (test != null) {
                Log.d("Exam Add Test Object",test.toString());
                this.testID = test.getId();
            }
            else this.testID = null;
            this.examName = examName;
            this.examDate = examDate;
            this.examTimes = examTimes;
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
            else {
                buildExamNameList(binding);
            }
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
                    examName = (String) parent.getItemAtPosition(position);
                    // Get the examID
                    database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                            .whereEqualTo(Constants.Exam.KEY_TEST_NAME,examName)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
//                                Exam examDB = queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class);
                                examID = queryDocumentSnapshots.getDocuments().get(0).getId();
                                // Build the exam times
                                buildExamTimeList(binding);
                            })
                            .addOnFailureListener(exception -> {
                                showToast("Exception getting Exam record: " + exception.getMessage());
                            });
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
            String dateString = preferenceManager.getString(Constants.Scheduled.KEY_SCHEDULED_DATE);

            HashMap<String, Object> test = new HashMap<>();

            // Get the date from the date spinner
            long milliseconds = binding.calendarView.getDate();
            Date dateObject = new Date(milliseconds);
            Timestamp dateTimestamp = new Timestamp(dateObject);
            Timestamp timestampObject = TimeConverter.stringToTimestamp(dateString);
            test.put(Constants.Scheduled.KEY_SCHEDULED_DATE,timestampObject);

            // Get the time from the time spinner
            int timeIndex = binding.inputTestTime.getSelectedItemPosition();
            Time timeObject = (Time) binding.inputTestTime.getItemAtPosition(timeIndex);
            test.put(Constants.Scheduled.KEY_SCHEDULED_TIME, TimeConverter.localizeTime(timestampObject));
            test.put(Constants.Scheduled.KEY_SCHEDULED_EXAMID, examID);
            test.put(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());
            test.put(Constants.User.KEY_USERID, userID);

            // Check if testID exists
            loading(true);
            if (testID != null) Log.d("Exam Add Test ID", testID);
            Log.d("Exam Add Test Date", dateTimestamp.toString());
            Log.d("Exam Add Test Date String", TimeConverter.localizeDate(timestampObject));
            if (testID != null) {
                database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                        .document(testID)
                        .set(test, SetOptions.merge())
                        .addOnSuccessListener(queryDocument -> {
                            Log.d("Exam Add Test Lookup", "Success");
                            showToast("Test Updated");
                            loading(false);
                            updatePreferences(binding);

//                            // Update the dependent collections
//                            database.document(testID).get().addOnSuccessListener(documentSnapshot -> {
//                                Scheduled scheduled = documentSnapshot.toObject(Scheduled.class);
//                                if (scheduled != null) scheduled.syncCollections();
//                            });
                            // Redirect to the Main test page
                            redirect();
                        })
                        .addOnFailureListener(exception -> {
                            loading(false);
                            showToast(exception.getMessage());
                        });
            }
            else {
                database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                        .add(test)
                        .addOnSuccessListener(queryDocument -> {
                            showToast("Test Added");
                            loading(false);
                            // Update the preferences
                            updatePreferences(binding);
//                            // Update the dependent collections
//                            queryDocument.get().addOnSuccessListener(documentSnapshot -> {
//                                Scheduled scheduled = documentSnapshot.toObject(Scheduled.class);
//                                if (scheduled != null) scheduled.syncCollections();
//                            });
                            // Redirect to the Main test page
                            redirect();
                        })
                        .addOnFailureListener(exception -> {
                            loading(false);
                            showToast(exception.getMessage());
                        });
            }
        }

        private void updatePreferences(ActivityTestAddBinding binding) {
            // Get the date from the date spinner
            long milliseconds = binding.calendarView.getDate();
            Date dateObject = new Date(milliseconds);
            Timestamp dateTimestamp = new Timestamp(dateObject);

            // Get the time from the time spinner
            int timeIndex = binding.inputTestTime.getSelectedItemPosition();
            Time timeObject = (Time) binding.inputTestTime.getItemAtPosition(timeIndex);

            // Save exam details to preference manager
            preferenceManager.putString(Constants.Scheduled.KEY_SCHEDULED_DATE,
                    TimeConverter.timestampToString(dateTimestamp));
            preferenceManager.putString(Constants.Scheduled.KEY_SCHEDULED_TIME,
                    TimeConverter.localizeTime(timeObject.getTime()));
            preferenceManager.putString(Constants.Scheduled.KEY_SCHEDULED_EXAMID, examName);
            preferenceManager.putString(Constants.Exam.KEY_CLASS_ID, binding.inputTestClass.getText().toString());
        }

        private void redirect() {
            // Set the adaptor with the current main page
            final ExamPageAdaptor examPageAdaptor = new ExamPageAdaptor();
            contentView.setAdapter(examPageAdaptor);
        }

        /**
         * Validating user details for each of the edit texts
         * @return Whether or not the details are valid
         */
        private Boolean isValidExamDetails() {
            if (!dateValid) {
                showToast("Please select a valid date");
                return false;
            } else if (binding.inputTestExam.getAdapter().getCount() == 0) {
                showToast("Please select exam ID");
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
        private void setupCalendar(ActivityTestAddBinding binding, Timestamp date) {
            // Private variables
            long milliseconds;
            if (examTimes != null) Log.d("Exam Add Test Calendar Times:", examTimes.toString());
            else Log.d("Exam Add Test Calendar Times:", "failed");
            updateValidDates();
            // Initialize the existing date to today
            if (date != null) { milliseconds = date.toDate().getTime(); }
            else { milliseconds = new Date().getTime(); }
            // Attach the date as the current date
            binding.calendarView.setDate(milliseconds);
            // Add Listener for calendar change event
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
                    Log.d("CalendarSetup: ",month + " " + dayOfMonth);
                    Timestamp timestamp = TimeConverter.calendarInfoToTimestamp(dayOfMonth,
                            month, year);
                    // Iterate through the valid dates and see if it matches
                    Boolean valid = false;
                    for (Timestamp validTime : examTimes) {
                        String date = TimeConverter.localizeDate(timestamp);
                        String validDate = TimeConverter.localizeDate(validTime);
                        Log.d("ExamAdaptor new Add:",date);
                        Log.d("ExamAdaptor valid Add:",validDate);
                        if (date.equals(validDate)) valid = true;
                    }
                    // Handle date check
                    if (valid) {
                        // Update the preference manager
                        preferenceManager.putString(Constants.Scheduled.KEY_SCHEDULED_DATE,
                                TimeConverter.timestampToString(timestamp));
                        dateValid = true;
                    }
                    else {
                        showToast("Not a valid date!");
                        dateValid = false;
                    }
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
                            // Ensure a document exists and times are valid
                            if (examDB != null) {
                                // Bind the class ID to the view
                                binding.inputTestClass.setText(examDB.getClassID());
                                // Bind the examTimes from this exam
                                examTimes = examDB.getTimes();
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
                                    // Setup the calendar
                                    setupCalendar(binding, examDate);
                                    updateValidDates();
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
                            // Get the first iteration of examTimes to display
                            if (examTimes == null) examTimes = examDB.getTimes();
                        }
                        // Ensure that names exist
                        if (!names.isEmpty()) {
                            Log.d("Test Build Name Exam List", names.toString());
                            // Set the exam time adaptor for the time elements
                            ExamNameAdaptor examNameAdaptor = new ExamNameAdaptor(mainPageLayout.getApplicationContext(), names);
                            binding.inputTestExam.setAdapter(examNameAdaptor);
                            // Setup the calendar
                            setupCalendar(binding, examDate);
                        }
                    });
        }

        private void updateValidDates() {
            // Loop through the acceptable days
            binding.calendarDesc.setText("Valid Dates:\n");
            for (Timestamp examTime : examTimes) {
                binding.calendarDesc.append(TimeConverter.localizeDate(examTime) + "\t\t");
            }
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