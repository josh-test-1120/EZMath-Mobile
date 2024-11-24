package com.example.ezmathmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.adaptors.ExamAdaptor;
import com.example.ezmathmobile.adaptors.FooterAdaptor;
import com.example.ezmathmobile.adaptors.HeaderAdaptor;
import com.example.ezmathmobile.adaptors.MainPageAdaptor;
import com.example.ezmathmobile.adaptors.ReminderPageAdaptor;
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

    // These are the objects in the view
    private GridLayout navigationGrid;
    private ImageView homeButton, testManagerButton, remindersButton;
    private RecyclerView contentView;

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
        // Bind the content view IDs
        contentView = findViewById(R.id.contentView);
        // Bind the objects to the Navigation view IDs
        navigationGrid = findViewById(R.id.navigationGrid);
        //menuButton = itemView.findViewById(R.id.homeBtn);
        homeButton = findViewById(R.id.homeButton);
        testManagerButton = findViewById(R.id.testManagerButton);
        //messageButton = findViewById(R.id.chatBtn);
        remindersButton = findViewById(R.id.remindersBtn);

        setListeners();
        // If logged in, we have enough information to load the main page
        if (loggedIn) {
            // Set the adaptor with the current main page
            final MainPageAdaptor mainPageAdaptor = new MainPageAdaptor();
            contentView.setAdapter(mainPageAdaptor);
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
     * This is the setListeners method for the different buttons in the navigation bar
     **/
    private void setListeners() {
        // Change to MainActivity (home screen) if homeButton clicked
        homeButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
            // Set the adaptor with the current main page
            final MainPageAdaptor mainPageAdaptor = new MainPageAdaptor();
            contentView.setAdapter(mainPageAdaptor);
        });
        // Change to TestManagerActivity if testManagerButton clicked
        testManagerButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), TestManagerActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
            // Set the adaptor with the current main page
            final ExamAdaptor examAdaptor = new ExamAdaptor();
            contentView.setAdapter(examAdaptor);
        });
        // Change to RemindersActivity if remindersButton clicked
        remindersButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), RemindersActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);

            final ReminderPageAdaptor reminderPageAdaptor = new ReminderPageAdaptor();
            contentView.setAdapter(reminderPageAdaptor);
        });
        /* This is for the chat feature if we have time:
        // Change to chat screen activity if chatButton clicked
        chatButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),MainActivity.class)));
         */
    }
}