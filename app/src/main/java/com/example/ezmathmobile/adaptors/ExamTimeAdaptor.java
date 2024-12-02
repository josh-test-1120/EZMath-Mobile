package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.Time;

import java.util.List;

/**
 * This is the Exam Name adaptor for ListView
 */
public class ExamTimeAdaptor extends ArrayAdapter<Time> {
    // Private variables
    private final List<Time> times;
    private final Context currentContext;

    /**
     * This is the default constructor
     * @param context This is the current context
     * @param times These are the exam names to process
     */
    public ExamTimeAdaptor(@NonNull Context context, @NonNull List<Time> times) {
        super(context, R.layout.exam_name_list, times);
        this.times = times;
        this.currentContext = context;
    }

    /**
     * This is the override for the getView method
     * @param position this is the position
     * @param convertView this is the convertView
     * @param parent this is the parent
     * @return an updated view is returned
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Assign the converted view to a default list view
        View examTimesView = convertView;
        //ListView examList = examTimesView.findViewById(R.id.inputTestExam);
        if (examTimesView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            examTimesView = inflater.inflate(R.layout.exam_name_list, parent, false);
        }
        Time time = getItem(position);
        //String timeString = "1234";
        //if (time != null) timeString = TimeConverter.localizeTime(time);
        Log.d("ExamTime Adaptor Time",time.toString());
        TextView nameView = examTimesView.findViewById(R.id.examNameItem);
        nameView.setText(time.toString());
        Log.d("ExamTime Adaptor View",examTimesView.toString());
        // Return the modified view
        return examTimesView;
    }
}