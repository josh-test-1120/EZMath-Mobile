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
        Boolean loggedIn = preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN);
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


            // Generate the user's name
            String first_name = preferenceManager.getString(Constants.KEY_FIRSTNAME);
            String last_name = preferenceManager.getString(Constants.KEY_LASTNAME);
            String name = String.format("Welcome %s %s!",first_name,last_name);

            database = FirebaseFirestore.getInstance();
            String userID = preferenceManager.getString(Constants.KEY_USERID);
            DatabaseService database1 = new DatabaseService(Constants.KEY_COLLECTION_USERS);
            database1.getID(userID);
            int timeout = 0;
            while (database1.documents.size() == 0 && timeout < 3900) { timeout++; };
            Log.d("Notifications Size",Integer.toString(database1.documents.size()));


            // Get the notifications
            database.collection("Notifications")
                    .whereEqualTo(Constants.KEY_USERID,userID)
                    // Get the record
                    .get()
                    // If no error fetching
                    .addOnCompleteListener(task -> {
                        //Log.d("This is the status of task: %s", Boolean.toString(task.isSuccessful()));
                        //Log.d("This is the status of task: %s", task.getResult().toString());
                        if (task.isSuccessful() && task.getResult() != null) {
                            final List<Notification> notifications = new ArrayList<>();
                            //Log.d("Database record was found %s","True");
                            // Get the database document data
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            Log.d("Inner Notif:",Integer.toString(task.getResult().size()));
                            for (DocumentSnapshot document : documents) {
                                Notification notification = document.toObject(Notification.class);
                                if (notification != null && Objects.equals(notification.type, "exam")) {
                                    // Get the scheduled exams
                                    database.collection("Scheduled")
                                        .document(notification.typeid)
                                        .get()
                                        .addOnCompleteListener(scheduletask -> {
                                            DocumentSnapshot scheduledDocument = scheduletask.getResult();
                                            Scheduled scheduled = scheduledDocument.toObject(Scheduled.class);
                                            notification.examDate = scheduled.date;
                                            Log.d("Exam ID",scheduled.examid);
                                            // Get the scheduled exams
                                            database.collection("Exams")
                                                    .document(scheduled.examid)
                                                    .get()
                                                    .addOnCompleteListener(examtask -> {
                                                        DocumentSnapshot examDocument = examtask.getResult();
                                                        Exam exam = examDocument.toObject(Exam.class);
                                                        notification.examName = exam.getName();
                                                        Log.d("Full Notif",notification.toString());
                                                        notifications.add(notification);
                                                        if (notifications.size() == documents.size()) {
                                                            // Set the adaptor with the current notifications
                                                            final NotificationAdaptor notificationAdapter = new NotificationAdaptor(notifications);
                                                            notificationsView.setAdapter(notificationAdapter);
                                                        }
                                                    });
                                        });
                                }
                            }
                            Log.d("Notifications Size:",Integer.toString(notifications.size()));
                            if (notifications.size() > 0) {
                                // Set the adaptor with the current notifications
                                final NotificationAdaptor notificationAdapter = new NotificationAdaptor(notifications);
                                notificationsView.setAdapter(notificationAdapter);
                            }


                            //Log.d("Database:",documentSnapshot[0].toString());
                            //Log.d("Notifications Size:",Integer.toString(notifications.size()));
                        }
                    });


            welcomeMessage.setText(name);
            upcomingExamMessage.setText("FUN1 - 3:00 PM on 12/2/2025");
            unreadNotificationMessage.setText("Unread Notifications: 0");


//            // Create the notifications
//            Notification notification1 = new Notification(1,"12345",
//                    "Exam notification","An exam is scheduled for this user",
//                    "FUN1", "exam", Timestamp.now());
//
//            // Add the notifications to the List
//            notifications.add(notification1);

            Query query = FirebaseFirestore.getInstance()
                    .collection("Notifications")
                    .whereEqualTo(Constants.KEY_USERID,userID)
                    .limit(20);

            // Configure recycler adapter options:
            //  * query is the Query object defined above.
            //  * Notification.class instructs the adapter to convert each DocumentSnapshot to a Notification object
            FirestoreRecyclerOptions<Notification> options = new FirestoreRecyclerOptions.Builder<Notification>()
                    .setQuery(query, Notification.class)
                    .build();
//            // Set the adaptor with the current notifications
//            final NotificationAdaptor notificationAdapter = new NotificationAdaptor(notifications);
//            notificationsView.setAdapter(notificationAdapter);
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

    private void handleDatabaseResults(DocumentSnapshot document) {
        User user =  document.toObject(User.class);
        Log.d("User:",user.toString());

    }
}