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

import java.util.List;

/**
 * This is the Exam Name adaptor for ListView
 */
public class ExamNameAdaptor extends ArrayAdapter<String> {

    /**
     * This is the default constructor
     * @param context This is the current context
     * @param names These are the exam names to process
     */
    public ExamNameAdaptor(@NonNull Context context, @NonNull List<String> names) {
        super(context, R.layout.exam_name_list, names);
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
        View examNamesView = convertView;

        //ListView examList = examTimesView.findViewById(R.id.inputTestExam);
        if (examNamesView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            examNamesView = inflater.inflate(R.layout.exam_name_list, parent, false);
        }
        String name = getItem(position);
        Log.d("ExamName Adaptor Time",name);
        TextView nameView = examNamesView.findViewById(R.id.examNameItem);
        nameView.setText(name);
        Log.d("ExamName Adaptor View",examNamesView.toString());
        // Return the modified view
        return examNamesView;
    }
}