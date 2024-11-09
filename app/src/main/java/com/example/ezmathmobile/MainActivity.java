package com.example.ezmathmobile;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
        RecyclerView notificationsView = findViewById(R.id.mainNotificationView);

        // Prepare the data
        List<Notification> notifications = new ArrayList<>();

        // Create the posters
        Notification notification1 = new Notification(1,12345,
            "Exam notification","An exam is scheduled for this user",
                "FUN1", LocalTime.now(), LocalDate.now());

        notifications.add(notification1);


//        int testManagerBtn: ImageView = findViewById(R.id.testManagerButton)
//        /**
//         * Function to start the test manager activity when calendar button is pressed
//         */
//        testManagerBtn.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View?) {
//                val intent = Intent(this@MainActivity, TestManagerActivity::class.java)
//                startActivity(intent)
//            }
//        })
    }
}