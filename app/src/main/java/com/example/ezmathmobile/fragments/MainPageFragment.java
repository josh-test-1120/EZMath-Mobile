package com.example.ezmathmobile.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.adaptors.NotificationMonthAdaptor;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Notification;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * This is the Main Page Fragment
 */
public class MainPageFragment extends Fragment {

    // These are the private objects in the view
    private ConstraintLayout layoutNotification;
    private TextView notificationName, notificationTime, notificationDate, welcomeMessage,
            usernameMessage, upcomingExamMessage, currentNotificationMessage,
            pastNotificationMessage;
    private ProgressBar progressBar;
    private RecyclerView notificationsView;
    // Get the application context
    private Context mainPageContext;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;

    /**
     * This is the default constructor
     */
    public MainPageFragment() {
        super(R.layout.main_container);
    }

    /**
     * This is the override for the onViewCreated method
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize context and preferences manager
        mainPageContext = view.getContext();
        preferenceManager = new PreferenceManager(mainPageContext.getApplicationContext());

        // Bind the variables to the view IDs
        notificationsView = view.findViewById(R.id.mainNotificationView);
        welcomeMessage = view.findViewById(R.id.welcomeMessage);
        usernameMessage = view.findViewById(R.id.userNameMessage);
        upcomingExamMessage = view.findViewById(R.id.upcomingExamMessage);
        currentNotificationMessage = view.findViewById(R.id.currentNotificationMessage);
        pastNotificationMessage = view.findViewById(R.id.pastNotificationMessage);
        progressBar = view.findViewById(R.id.progressBar);

        // Generate the user's name from shared preferences
        String first_name = preferenceManager.getString(Constants.User.KEY_FIRSTNAME);
        String last_name = preferenceManager.getString(Constants.User.KEY_LASTNAME);
        String welcome = "Welcome";
        String name = String.format("%s %s!", first_name, last_name);
        welcomeMessage.setText(welcome);
        usernameMessage.setText(name);

        // Get the data from the database
        database = FirebaseFirestore.getInstance();
        String userID = preferenceManager.getString(Constants.User.KEY_USERID);
        // Get the notifications
        queryNotifications(preferenceManager, upcomingExamMessage, currentNotificationMessage,
                pastNotificationMessage, notificationsView);

    }

    /**
     * This will query the notifications from the firestore database
     * and do the relational sub-queries to put all the information together
     *
     * @param preferences This is the preferences for the application
     * @param latestView This is the TextView that holds the latest notification
     * @param numberView This is the TextView that holds the number of notifications
     * @param pastNumberView This is the TextView that holds the number of notifications
     * @param notificationsView This is the RecycleView that we update the notifications in
     */
    public void queryNotifications(PreferenceManager preferences, TextView latestView, TextView numberView,
                                   TextView pastNumberView, RecyclerView notificationsView) {
        // Get information from preferences
        String userID = preferences.getString(Constants.User.KEY_USERID);
        loading(true);
        // Get the notifications
        database.collection("Notifications")
                .whereEqualTo(Constants.User.KEY_USERID, userID)
                // Get the record
                .get()
                // If no error fetching data
                .addOnCompleteListener(task -> {
                    Log.d("MainPageFragment success",Boolean.toString(task.isSuccessful()));
                    Log.d("MainPageFragment result",Boolean.toString(task.getResult() != null));
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Finalize the notifications for use in lambda's
                        final List<Notification> notifications = new ArrayList<>();
                        // Get the database document data
                        final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        Log.d("MainPageFragment documents",Integer.toString(documents.size()));
                        for (DocumentSnapshot document : documents) {
                            Log.d("MainPageFragment for loop","inside");
                            // Serialize the document to the class
                            Notification notification = document.toObject(Notification.class);
                            if (notification != null && Objects.equals(notification.type, "exam")) {
                                // Get the scheduled exam
                                database.collection(Constants.Scheduled.KEY_COLLECTION_SCHEDULED)
                                        .document(notification.typeid)
                                        .get()
                                        // If no errors fetching data
                                        .addOnCompleteListener(scheduletask -> {
                                            DocumentSnapshot scheduledDocument = scheduletask.getResult();
                                            // Serialize the document to the class
                                            Scheduled scheduled = scheduledDocument.toObject(Scheduled.class);
                                            // Attach the date to the notification
                                            notification.examDate = scheduled.date;
                                            // Get the Exam detail
                                            database.collection("Exams")
                                                    .document(scheduled.examid)
                                                    .get()
                                                    // If no errors fetching data
                                                    .addOnCompleteListener(examtask -> {
                                                        DocumentSnapshot examDocument = examtask.getResult();
                                                        // Serialize the document to the class
                                                        Exam exam = examDocument.toObject(Exam.class);
                                                        // Attach the exam name to the notification
                                                        notification.examName = exam.getName();
                                                        // Push the notification into the list
                                                        notifications.add(notification);
                                                        // When we have processed all callbacks for each notification
                                                        if (notifications.size() == documents.size()) {
                                                            updateHomePage(notifications, latestView, numberView, pastNumberView, notificationsView);
                                                            loading(false);
                                                        }
                                                    });
                                        });
                            }
                        }
                        // Empty data set
                        loading(false);
                        // String formatters
                        String latestNotification = "No notifications available for user";
                        String pastNotifications = "Zero Past Notifications";
                        String futureNotifications = "Zero Future Notifications";
                        // Update the UI
                        latestView.setText(latestNotification);
                        numberView.setText(futureNotifications);
                        pastNumberView.setText(pastNotifications);
                    }
                });
    }

    /**
     * This will update the home page with all the latest notification details
     *
     * @param notifications     These are the notifications to process
     * @param latestView        This is the TextView that holds the latest notification
     * @param numberView        This is the TextView that holds the number of notifications
     * @param notificationsView This is the RecycleView that we update the notifications in
     */
    public void updateHomePage(List<Notification> notifications, TextView latestView, TextView numberView,
                               TextView pastNumberView, RecyclerView notificationsView) {
        // Private variables
        int future = 0, past = 0;
        // Find the latest date
        int index = TimeConverter.findClosestDate(notifications);
        Notification latest = notifications.get(index);
        // Get localized string from the timestamp
        String time = TimeConverter.localizeTime(latest.examDate);
        String date = TimeConverter.localizeDate(latest.examDate);
        // String formatters
        String latestNotification = String.format("%s - %s on %s", latest.examName, time, date);
        // Iterate through the notifications to determine past or present
        for (Notification notification : notifications) {
            if (notification.examDate.toDate().before(new Date())) {
                past++;
            }
            else future++;
        }
        String futureNotifications = String.format("Future Exam Notifications: %d", future);
        String pastNotifications = String.format("Past Exam Notifications: %d", past);
        // Update the UI
        latestView.setText(latestNotification);
        numberView.setText(futureNotifications);
        pastNumberView.setText(pastNotifications);
        // Convert the notifications into month groups
        LinkedHashMap<String, List<Notification>> groupedByMonth = TimeConverter.sortByMonth(notifications);
        // Set the adaptor with the current notifications
        final NotificationMonthAdaptor notificationMonthAdaptor = new NotificationMonthAdaptor(groupedByMonth);
        notificationsView.setAdapter(notificationMonthAdaptor);
    }

    /**
     * Setting up progress bar visibility if user is loading or not
     * @param isLoading Whether the program is loading or not
     */
    private void loading(Boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
