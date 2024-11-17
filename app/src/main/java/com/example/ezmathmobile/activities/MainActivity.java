package com.example.ezmathmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.adaptors.FooterAdaptor;
import com.example.ezmathmobile.adaptors.HeaderAdaptor;
import com.example.ezmathmobile.firebase.DatabaseService;
import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Notification;
import com.example.ezmathmobile.adaptors.NotificationAdaptor;
import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.models.User;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is the Main Activity view that implements PosterListener
 */
public class MainActivity extends AppCompatActivity {
    // Private variables
    private Button buttonWatchList;
    private ImageView testManagerBtn;
    private FirebaseFirestore database;

    /**
     * This is an override of the onCreate method
     * @param savedInstanceState the current saved state of the instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Attach the preferences
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        // Default listener Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Determine current user status
        Boolean loggedIn = preferenceManager.getBoolean(Constants.User.KEY_IS_SIGNED_IN);
        // Used to flush the preferences in testing
        //preferenceManager.clear();
        // If logged in, we have enough information to load the main page
        if (loggedIn) {
            // Bind the variables to the view IDs
            RecyclerView headerView = findViewById(R.id.headerView);
            RecyclerView notificationsView = findViewById(R.id.mainNotificationView);
            RecyclerView navigationView = findViewById(R.id.navigationView);
            TextView welcomeMessage = findViewById(R.id.welcomeMessage);
            TextView upcomingExamMessage = findViewById(R.id.upcomingExamMessage);
            TextView unreadNotificationMessage = findViewById(R.id.unreadNotificationMessage);

            // Generate the user's name from shared preferences
            String first_name = preferenceManager.getString(Constants.User.KEY_FIRSTNAME);
            String last_name = preferenceManager.getString(Constants.User.KEY_LASTNAME);
            String name = String.format("Welcome %s %s!",first_name,last_name);
            welcomeMessage.setText(name);

            // Get the data from the database
            database = FirebaseFirestore.getInstance();
            String userID = preferenceManager.getString(Constants.User.KEY_USERID);
            // Get the notifications
            queryNotifications(preferenceManager,upcomingExamMessage,unreadNotificationMessage,notificationsView);

            // Set the adaptor with the current header
            final HeaderAdaptor headerAdapter = new HeaderAdaptor();
            headerView.setAdapter(headerAdapter);
            // Set the adaptor with the current navigation
            final FooterAdaptor footerAdapter = new FooterAdaptor();
            navigationView.setAdapter(footerAdapter);
        }
        // else we need to load the SignIn Activity
        else {
            // Change activity to SignInActivity
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    /**
     * This will query the notifications from the firestore database
     * and do the relational sub-queries to put all the information together
     * @param preferences This is the preferences for the application
     * @param latestView This is the TextView that holds the latest notification
     * @param numberView This is the TextView that holds the number of notifications
     * @param notificationsView This is the RecycleView that we update the notifications in
     */
    public void queryNotifications(PreferenceManager preferences,TextView latestView, TextView numberView,
                                   RecyclerView notificationsView) {
        // Get information from preferences
        String userID = preferences.getString(Constants.User.KEY_USERID);
        // Get the notifications
        database.collection("Notifications")
                .whereEqualTo(Constants.User.KEY_USERID,userID)
                // Get the record
                .get()
                // If no error fetching data
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Finalize the notifications for use in lambda's
                        final List<Notification> notifications = new ArrayList<>();
                        // Get the database document data
                        final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        for (DocumentSnapshot document : documents) {
                            // Serialize the document to the class
                            Notification notification = document.toObject(Notification.class);
                            if (notification != null && Objects.equals(notification.type, "exam")) {
                                // Get the scheduled exam
                                database.collection("Scheduled")
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
                                                        if (notifications.size() == documents.size()) updateHomePage(notifications,latestView,numberView,notificationsView);
                                                    });
                                        });
                            }
                        }
                    }
                });
    }

    /**
     * This will update the home page with all the latest notification details
     * @param notifications These are the notifications to process
     * @param latestView This is the TextView that holds the latest notification
     * @param numberView This is the TextView that holds the number of notifications
     * @param notificationsView This is the RecycleView that we update the notifications in
     */
    public void updateHomePage(List<Notification> notifications, TextView latestView, TextView numberView,
                               RecyclerView notificationsView) {
        // Find the latest date
        int index = TimeConverter.findLatestDate(notifications);
        Notification latest = notifications.get(index);
        // Get localized string from the timestamp
        String time = TimeConverter.localizeTime(latest.examDate);
        String date = TimeConverter.localizeDate(latest.examDate);
        // String formatters
        String latestNotification = String.format("%s - %s on %s",latest.examName, time, date);
        String sizeNotifications = String.format("Unread Notifications: %d",notifications.size());
        // Update the UI
        latestView.setText(latestNotification);
        numberView.setText(sizeNotifications);

        // Set the adaptor with the current notifications
        final NotificationAdaptor notificationAdapter = new NotificationAdaptor(notifications);
        notificationsView.setAdapter(notificationAdapter);
    }
}