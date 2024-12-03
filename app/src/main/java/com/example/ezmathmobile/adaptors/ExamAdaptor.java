package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivityMainBinding;
import com.example.ezmathmobile.databinding.ActivityTestManagerBinding;
import com.example.ezmathmobile.databinding.ExamItemContainerBinding;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.HashMap;

/**
 * This is the NotificationAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class ExamAdaptor extends RecyclerView.Adapter<ExamAdaptor.ExamViewHolder> {
    // These are the private variables
    private List<Scheduled> exams;
    private ViewGroup mainParent;


    /**
     * This is the constructor for the Adaptor
     * @param exams This is a List of Notifications
     */
    public ExamAdaptor(List<Scheduled> exams, ViewGroup mainParent) {
        this.exams = exams;
        this.mainParent = mainParent;
    }

    /**
     * This is an override of the onCreateViewHolder method
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new NotificationViewHolder class that has its layout inflated
     */
    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExamViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_item_container, parent, false), parent, mainParent);
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        if (position < exams.size()) holder.bindExam(exams.get(position));
    }

    /**
     * This is an override of the getItemCount method
     * @return the size of the notifications list
     */
    @Override
    public int getItemCount() {
        return exams.size();
    }

    /**
     * This is the NotificationViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        private LinearLayout layoutExam;
        // Dependency Objects
        private PreferenceManager preferenceManager;
        private FirebaseFirestore database;
        private Context mainPageLayout;
        // View Objects
        private ExamItemContainerBinding binding;
        private ActivityMainBinding mainBinding;
        private ActivityTestManagerBinding testBinding;
        private RecyclerView contentView;
        private ProgressBar progressBar;

        /**
         * This is the NotificationViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public ExamViewHolder(@NonNull View itemView, ViewGroup parent, ViewGroup mainParent) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Bind the objects to the view ID
            layoutExam = itemView.findViewById(R.id.testContainer);
            // Attach the preferences
            preferenceManager = new PreferenceManager(layoutExam.getContext().getApplicationContext());
            // Attach the binding
            this.binding = ExamItemContainerBinding.bind(itemView);
            // Bind the content view ID
            contentView = mainParent.findViewById(R.id.contentView);
            // Bind the progress bar
            progressBar = mainParent.findViewById(R.id.progressBar);;
            // Bind the database object
            database = FirebaseFirestore.getInstance();
        }

        /**
         * This is the bind exam method that will bind actions
         * and listeners to the exam
         * @param exam this is the exam to bind actions to
         */
        void bindExam(final Scheduled exam) {
            Log.d("Notif Data",exam.toString());

            // Get localized string from the timestamp
            String time = TimeConverter.localizeTime(exam.getDate());
            String date = TimeConverter.localizeDayOnly(exam.getDate());

            //Setting the view texts to whatever was given
            binding.testName.setText(exam.getName());
            binding.testTime.setText(time);
            binding.testDate.setText(date);

            //Add some listeners for the delete and edit test buttons
            binding.testDelete.setOnClickListener(v -> {
                deleteTest(exam.getId());
                AddDeletionReminder(exam.name);
            });
            binding.testEdit.setOnClickListener(v -> getExamTimes(exam.getExamid(),exam));
        }

        /**
         * Method to simplify Toast code, shows a toast of whatever message needs to be displayed
         * @param message Message to be displayed
         */
        private void showToast(String message){
            Toast.makeText(mainPageLayout.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }

        /**
         * Delete the test
         * @param examID this is the examID to delete
         */
        private void deleteTest(String examID) {
            loading(true);
            database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                    .whereEqualTo(FieldPath.documentId(), examID)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            // Serialize the document to the class
                            Scheduled test = queryDocumentSnapshots.getDocuments().get(0).toObject(Scheduled.class);
                            test.setId(document.getId());
                            // Unsynchronize the dependent collections
                            Log.d("Scheduled Delete","unsynchronize");
                            test.unSyncCollections();
                            database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                                    .document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(mainPageLayout, "Test successfully deleted", Toast.LENGTH_SHORT).show();
                                        // Set the adaptor with the current Test Manager page
                                        final ExamPageAdaptor examPageAdaptor = new ExamPageAdaptor();
                                        contentView.setAdapter(examPageAdaptor);
                                    })
                                    .addOnFailureListener(e -> {
                                        loading(false);
                                        showToast(e.getMessage());
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        loading(false);
                        showToast(e.getMessage());
                    });
        }

        /**
         * A method that adds a test deletion confirmation reminder in the Firebase database.
         */
        private void AddDeletionReminder(String examID) {
            // Declaring Hashmap
            HashMap<String, Object> hashMap = new HashMap<>();

            // Adding data to the Hashmap
            // Datetime
            hashMap.put(Constants.Reminders.KEY_REMINDER_DATETIME, com.google.firebase.Timestamp.now());
            // Adding text
            hashMap.put(Constants.Reminders.KEY_REMINDER_TEXT, examID + " test has been successfully" +
                    " deleted");
            // Adding type
            hashMap.put(Constants.Reminders.KEY_REMINDER_TYPE, "red");

            // Adding userid
            hashMap.put(Constants.User.KEY_USERID, preferenceManager.getString(Constants.User.KEY_USERID));

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
        private void editTest(String examID, Scheduled test, Exam exam) {
            Log.d("ExamMonth Edit",examID);
            // Set the adaptor with the current main page
            final ExamAddAdaptor examAddAdaptor = new ExamAddAdaptor(test, examID, exam.getName(), test.getDate(), exam.getTimes());
            contentView.setAdapter(examAddAdaptor);

        }

        /**
         * Get the examTimes from the database based on the scheduled exam name
         * @param examID this is the examID
         * @param test this is the scheduled test object
         */
        private void getExamTimes(String examID, Scheduled test) {
            // Get the exam details
            database.collection(Constants.Exam.KEY_COLLECTION_EXAMS)
                    .whereEqualTo(Constants.Exam.KEY_TEST_NAME,test.getName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Serialize the document to the class
                        Exam exam = queryDocumentSnapshots.getDocuments().get(0).toObject(Exam.class);
                        editTest(examID, test, exam);
                    })
                    .addOnFailureListener(exception -> {
                        showToast("Exception getting exam times: " + exception.getMessage());
                    });
        }

        /**
         * Setting up progress bar visibility if user is loading or not
         * @param isLoading Whether the program is loading or not
         */
        private void loading(Boolean isLoading){
            if(isLoading){
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}