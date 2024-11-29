package com.example.ezmathmobile.adaptors;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.models.Exam;
import com.example.ezmathmobile.models.Scheduled;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.example.ezmathmobile.utilities.TimeConverter;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.models.Notification;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * This is the NotificationAdaptor that is used in the RecycleView Adaptor
 * This extends the RecycleView.Adaptor class
 */
public class MainPageAdaptor extends RecyclerView.Adapter<MainPageAdaptor.MainPageViewHolder> {

    /**
     * This is the constructor for the Adaptor
     */
    public MainPageAdaptor() {
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
    public MainPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainPageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.main_container, parent, false));
    }

    /**
     * This is an override of the onBindViewHolder method
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MainPageViewHolder holder, int position) {}

    /**
     * This is an override of the getItemCount method
     * @return the size of the notifications list
     */
    @Override
    public int getItemCount() {
        return 1;
    }

    /**
     * This is the NotificationViewHolder class that extends the RecycleView.ViewHolder
     */
    public static class MainPageViewHolder extends RecyclerView.ViewHolder {
        // These are the objects in the view
        ConstraintLayout layoutNotification;
        //View viewBackground;
        TextView notificationName, notificationTime, notificationDate, welcomeMessage,
                upcomingExamMessage, unreadNotificationMessage;
        ProgressBar progressBar;
        RecyclerView notificationsView;
        // Get the application context
        Context mainPageContext;
        PreferenceManager preferenceManager;
        FirebaseFirestore database;

        /**
         * This is the NotificationViewHolder constructor
         *
         * @param itemView the view that is to be inflated
         */
        public MainPageViewHolder(@NonNull View itemView) {
            // Run the parent class constructor
            super(itemView);
            // Initialize context and preferences manager
            mainPageContext = itemView.getContext();
            preferenceManager = new PreferenceManager(mainPageContext.getApplicationContext());

            // Bind the variables to the view IDs
            notificationsView = itemView.findViewById(R.id.mainNotificationView);
            welcomeMessage = itemView.findViewById(R.id.welcomeMessage);
            upcomingExamMessage = itemView.findViewById(R.id.upcomingExamMessage);
            unreadNotificationMessage = itemView.findViewById(R.id.unreadNotificationMessage);
            progressBar = itemView.findViewById(R.id.progressBar);

            // Generate the user's name from shared preferences
            String first_name = preferenceManager.getString(Constants.User.KEY_FIRSTNAME);
            String last_name = preferenceManager.getString(Constants.User.KEY_LASTNAME);
            String name = String.format("Welcome %s %s!", first_name, last_name);
            welcomeMessage.setText(name);

            // Get the data from the database
            database = FirebaseFirestore.getInstance();
            String userID = preferenceManager.getString(Constants.User.KEY_USERID);
            // Get the notifications
            queryNotifications(preferenceManager, upcomingExamMessage, unreadNotificationMessage, notificationsView);
        }

        /**
         * This is the bind Notification method that will bind actions
         * and listeners to the notification
         *
         * @param notification this is the notification to bind actions to
         */
        void bindNotification(final Notification notification) {
            Log.d("Notif Data", notification.toString());
            // Update the view with the poster information
            if (notification.examName != null) notificationName.setText(notification.examName);
            if (notification.examDate != null) {
                // Get localized string from the timestamp
                String time = TimeConverter.localizeTime(notification.examDate);
                String date = TimeConverter.localizeDate(notification.examDate);
                // Update the UI
                notificationTime.setText(time);
                notificationDate.setText(date);
            }
        }

        /**
         * This will query the notifications from the firestore database
         * and do the relational sub-queries to put all the information together
         *
         * @param preferences       This is the preferences for the application
         * @param latestView        This is the TextView that holds the latest notification
         * @param numberView        This is the TextView that holds the number of notifications
         * @param notificationsView This is the RecycleView that we update the notifications in
         */
        public void queryNotifications(PreferenceManager preferences, TextView latestView, TextView numberView,
                                       RecyclerView notificationsView) {
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
                                                            if (notifications.size() == documents.size()) {
                                                                updateHomePage(notifications, latestView, numberView, notificationsView);
                                                                loading(false);
                                                            }
                                                        });
                                            });
                                }
                            }
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
                                   RecyclerView notificationsView) {
            // Find the latest date
            int index = TimeConverter.findClosestDate(notifications);
            Notification latest = notifications.get(index);
            // Get localized string from the timestamp
            String time = TimeConverter.localizeTime(latest.examDate);
            String date = TimeConverter.localizeDate(latest.examDate);
            // String formatters
            String latestNotification = String.format("%s - %s on %s", latest.examName, time, date);
            String sizeNotifications = String.format("Unread Notifications: %d", notifications.size());
            // Update the UI
            latestView.setText(latestNotification);
            numberView.setText(sizeNotifications);

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
}