package com.example.ezmathmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the Main Activity view that implements PosterListener
 */
public class MainActivity extends AppCompatActivity {
    // Private variables
    private Button buttonWatchList;
    private ImageView testManagerBtn;

    /**
     * This is an override of the onCreate method
     * @param savedInstanceState the current saved state of the instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind the variables to the view IDs
        RecyclerView headerView = findViewById(R.id.headerView);
        RecyclerView notificationsView = findViewById(R.id.mainNotificationView);
        RecyclerView navigationView = findViewById(R.id.navigationView);
        TextView welcomeMessage = findViewById(R.id.welcomeMessage);
        TextView upcomingExamMessage = findViewById(R.id.upcomingExamMessage);
        TextView unreadNotificationMessage = findViewById(R.id.unreadNotificationMessage);

        List<Notification> notifications = new ArrayList<>();
        // This is where we should pull user details from database
        welcomeMessage.setText("Welcome EZMath Student");
        upcomingExamMessage.setText("FUN1 - 3:00 PM on 12/2/2025");
        unreadNotificationMessage.setText("Unread Notifications: 0");

        // Create the notifications
        Notification notification1 = new Notification(1,12345,
            "Exam notification","An exam is scheduled for this user",
                "FUN1", LocalTime.now(), LocalDate.now());

        // Add the notifications to the List
        notifications.add(notification1);

        // Set the adaptor with the current notifications
        final NotificationAdaptor notificationAdapter = new NotificationAdaptor(notifications);
        notificationsView.setAdapter(notificationAdapter);
        // Set the adaptor with the current header
        final HeaderAdaptor headerAdapter = new HeaderAdaptor();
        headerView.setAdapter(headerAdapter);
        // Set the adaptor with the current navigation
        final FooterAdaptor footerAdapter = new FooterAdaptor();
        navigationView.setAdapter(footerAdapter);

         /*
        Ready to add when navbar is redone
        testManagerBtn = findViewById(R.id.testManagerButton);

        testManagerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TestManagerActivity.class));
            }
        });*/
    }
}