package com.example.ezmathmobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.RemindersActivity;

/**
 * This is the NavigationBarActivity class
 **/
public class NavigationBarActivity extends AppCompatActivity {
    // navigation bar components
    ConstraintLayout navigationBar;
    GridLayout navigationLayout;
    ImageView homeButton, testManagerButton, chatButton, remindersButton;

    /**
     * This is an override of the onCreate method
     * @param savedInstanceState the current saved state of the instance
 `  **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize the navigation bar components
        navigationBar = findViewById(R.id.navigation_bar);
        navigationLayout = findViewById(R.id.navigationLayout);
        homeButton = findViewById(R.id.homeBtn);
        testManagerButton = findViewById(R.id.testManagerBtn);
        /*
        // This is for the chat feature if we have time:
        chatButton = findViewById(R.id.chatBtn);
         */
        remindersButton = findViewById(R.id.remindersBtn);
        setListeners(); // set the button listeners
    }

    /**
     * This is the setListeners method for the different buttons in the navigation bar
    **/
    private void setListeners() {
        // Change to MainActivity (home screen) if homeButton clicked
        homeButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        // Change to TestManagerActivity if testManagerButton clicked
        testManagerButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),TestManagerActivity.class)));
        // Change to RemindersActivity if remindersButton clicked
        remindersButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), RemindersActivity.class)));
        /* This is for the chat feature if we have time:
        // Change to chat screen activity if chatButton clicked
        chatButton.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),MainActivity.class)));
         */
    }
}
