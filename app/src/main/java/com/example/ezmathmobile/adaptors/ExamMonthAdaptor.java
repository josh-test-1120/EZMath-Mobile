package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivityTestManagerBinding;
import com.example.ezmathmobile.databinding.ExamMonthContainerBinding;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Scheduled;
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
public class ExamMonthAdaptor extends RecyclerView.Adapter<ExamMonthAdaptor.ExamMonthViewHolder> {

    private String examID;
    private LinkedHashMap<String,List<Scheduled>> exams;
    private ViewGroup mainParent;


    /**
     * This is the constructor for the Adaptor
     * @param exams This is a List of Scheduled
     */
    public ExamMonthAdaptor(LinkedHashMap<String,List<Scheduled>> exams, ViewGroup mainParent) {
        Log.d("Month Notif",exams.toString());
        this.exams = exams;
        this.mainParent = mainParent;
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
    public ExamMonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Return the view inflated
        return new ExamMonthViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_month_container, parent, false), parent, mainParent);
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ExamMonthViewHolder holder, int position) {
        Object[] keys = exams.keySet().toArray();
        String month = (String) keys[position];
        if (position < exams.size()) holder.bindMonth(exams.get(month),month);
    }

    /**
     * This is an override of the getItemCount method
     * @return the size of the exams list
     */
    @Override
    public int getItemCount() {
        return exams.size();
    }

    /**
     * This is the ExamViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class ExamMonthViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        private ConstraintLayout layoutExam;
        // Dependency Objects
        private PreferenceManager preferenceManager;
        private FirebaseFirestore database;
        private ExamMonthContainerBinding binding;
        private ActivityTestManagerBinding parentBinding;
        private Context mainPageLayout;
        private ProgressBar progressBar;
        private RecyclerView contentView;
        private ViewGroup mainParent;

        private TextView monthName;
        private RecyclerView examMonthView;

        /**
         * This is the ExamViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public ExamMonthViewHolder(@NonNull View itemView, ViewGroup parent, ViewGroup mainParent) {
            // Run the parent class constructor
            super(itemView);
            // Get the page context
            mainPageLayout = itemView.getContext();
            // Bind the objects to the view ID
            layoutExam = itemView.findViewById(R.id.main);
            // Bind the parent elements
            contentView = parent.findViewById(R.id.testContainer);
            progressBar = mainParent.findViewById(R.id.progressBar);
            this.mainParent = mainParent;

            // Attach the preferences
            preferenceManager = new PreferenceManager(layoutExam.getContext().getApplicationContext());
            // Attach the database
            database = FirebaseFirestore.getInstance();
            // Attach the binding
            this.binding = ExamMonthContainerBinding.bind(itemView);
            monthName = itemView.findViewById(R.id.monthText);
            examMonthView = itemView.findViewById(R.id.examMonthContainer);

        }

        /**
         * This is the bind Notification method that will bind actions
         * and listeners to the notification
         * @param List<Scheduled> this is a List of notifications to bind actions to
         */
        void bindMonth(final List<Scheduled> exams, String month) {
            Log.d("Month Notif",String.valueOf(month));
            if (exams != null) {
                Log.d("ExamMonth Notif",String.valueOf(month));
                Log.d("ExamMonth Notif", exams.toString());
                // Set the name
                monthName.setText(String.valueOf(month));
                Log.d("ExamMonth Field",monthName.getText().toString());

                // Set the adaptor with the current month notifications
                final ExamAdaptor examAdaptor = new ExamAdaptor(exams, mainParent);
                examMonthView.setAdapter(examAdaptor);
            }
        }
    }
}