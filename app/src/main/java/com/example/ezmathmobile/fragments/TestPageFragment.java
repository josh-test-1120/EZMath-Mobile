package com.example.ezmathmobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.adaptors.ExamMonthAdaptor;
import com.example.ezmathmobile.databinding.ActivityMainBinding;
import com.example.ezmathmobile.databinding.ActivityTestManagerBinding;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * This is the Test Manager Page Fragment
 */
public class TestPageFragment extends Fragment {
    // These are the private variables
    // These are the objects in the view
    private ConstraintLayout layoutExam;
    // Dependency Objects
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private ActivityTestManagerBinding binding;
    private ActivityMainBinding parentBinding;
    private Context mainPageLayout;
    private FragmentContainerView contentView;
    private ViewGroup parent;
    private RecyclerView testContainer;

    /**
     * This is the default constructor
     */
    public TestPageFragment() {
        super(R.layout.activity_test_manager);
    }

    /**
     * This is the onViewCreated override method
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the parent
        ViewGroup parent = (ViewGroup) view.getParent();
        // Get the page context
        mainPageLayout = view.getContext();
        // Bind the objects to the view ID
        layoutExam = view.findViewById(R.id.main);
        this.parent = parent;
        // Bind the content view ID
        contentView = parent.findViewById(R.id.contentView);
        // Attach the preferences
        preferenceManager = new PreferenceManager(layoutExam.getContext().getApplicationContext());
        // Attach the database
        database = FirebaseFirestore.getInstance();
        // Attach the binding
        this.binding = ActivityTestManagerBinding.bind(view);
        testContainer = view.findViewById(R.id.testContainer);
        // Set the binding for the add new schedule button
        binding.buttonAddTest.setOnClickListener(v -> {
            // Redirect to the Add Test Fragment
            redirectAddTest();
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
                        scheduleDB.setId(schedule.getId());
                        //String scheduledID = schedule.getId();

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
                    // Empty data set
                    loading(false);
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

    /**
     * This is the method that will load the Test Add Fragment
     */
    private void redirectAddTest() {
        // Load the fragment for the Exam Page
        // Create a new bundle for passed data
        Bundle bundle = new Bundle();
        //bundle.put("some_int", 0);

        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.contentView, TestAddFragment.class, bundle)
                .setReorderingAllowed(true)
                .addToBackStack("TestAddManager") // Name can be null
                .commit();
    }
}