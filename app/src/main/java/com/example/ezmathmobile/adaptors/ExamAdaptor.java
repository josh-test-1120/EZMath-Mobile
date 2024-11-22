package com.example.ezmathmobile.adaptors;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.models.Scheduled;

import com.example.ezmathmobile.R;

import java.util.List;


/**
 * This is the ExamAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class ExamAdaptor extends RecyclerView.Adapter<ExamAdaptor.ExamViewHolder> {
    // These are the private variables
    private List<Scheduled> exams;

    /**
     * This is the constructor for the Adaptor
     * @param exams This is a List of scheduled exams
     */
    public ExamAdaptor(List<Scheduled> exams) {
        this.exams = exams;
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
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExamViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_test_manager, parent, false));
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
     * @return the size of the exams list
     */
    @Override
    public int getItemCount() {
        return exams.size();
    }

    /**
     * This is the ExamViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        ConstraintLayout layoutExam;
        //View viewBackground;
        TextView notificationName, notificationTime, notificationDate;

        /**
         * This is the ExamViewHolder constructor
         * @param itemView the view that is to be inflated
         */
        public ExamViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);
            // Bind the objects to the view IDs
            layoutExam = itemView.findViewById(R.id.main);
        }

        /**
         * This is the bind Exam method that will bind actions
         * and listeners to the exam
         * @param exam this is the scheduled exam to bind actions to
         */
        void bindExam(final Scheduled exam) {
            Log.d("Notif Data",exam.toString());
            // Update the view with the exam information
//            if (exam.examName != null) notificationName.setText(exam.examName);
//            if (exam.examDate != null) {
//                // Get localized string from the timestamp
//                String time = TimeConverter.localizeTime(exam.examDate);
//                String date = TimeConverter.localizeDate(exam.examDate);
//                // Update the UI
//                notificationTime.setText(time);
//                notificationDate.setText(date);
//            }
        }
    }
}