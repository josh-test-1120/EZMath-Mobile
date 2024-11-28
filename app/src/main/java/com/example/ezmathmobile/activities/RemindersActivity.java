/**
 * This is the Activity Controller of the Reminders page. Data from the database would be used to
 * show on the screen here.
 * @author Telmen Enkhtuvshin
 */
package com.example.ezmathmobile.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ezmathmobile.databinding.ActivityRemindersBinding;
public class RemindersActivity extends AppCompatActivity {

    /**
     * Class fields. Includes View data binding
     */
    private ActivityRemindersBinding binding;

    /**
     * A overridden method that creates the activity, fetches the data from the Firestore database,
     * populates the layouts, and binds them to display on the screen.
     * @param savedInstanceState A instance state of the application.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRemindersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
