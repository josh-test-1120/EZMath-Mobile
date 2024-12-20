package com.example.ezmathmobile.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import com.example.ezmathmobile.adaptors.NavigationAdaptor;
import com.example.ezmathmobile.R;
import com.example.ezmathmobile.fragments.MainPageFragment;
import com.example.ezmathmobile.fragments.ReminderPageFragment;
import com.example.ezmathmobile.fragments.TestPageFragment;
import com.example.ezmathmobile.models.NavigationCard;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;

import com.example.ezmathmobile.utilities.TimeCheckReceiver;

import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * This is the Main Activity view that implements PosterListener
 */
public class MainActivity extends AppCompatActivity {
    // Private variables
    private Button buttonWatchList;
    private ImageView testManagerBtn;
    private FirebaseFirestore database;

    // These are the objects in the view
    private GridView navigationGrid;
    //private ImageView homeButton, testManagerButton, remindersButton;
    private FragmentContainerView contentView;
    private RoundedImageView imageProfile;

    private PreferenceManager preferenceManager;

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
        preferenceManager = new PreferenceManager(getApplicationContext());

        if (savedInstanceState == null) {
            // Create a new bundle for passed data
            Bundle bundle = new Bundle();
            //bundle.put("some_int", 0);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.contentView, MainPageFragment.class, bundle)
                    .commit();
        }
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
        // Bind the image profile to the header
        imageProfile = findViewById(R.id.imageProfile);

        // Setup the Activity
        //setListeners();
        buildNavigation();
        loadUserDetails();

        // If logged in, we have enough information to load the main page
        if (loggedIn) {
            // Load the fragment for the Main Page
            // Create a new bundle for passed data
            Bundle bundle = new Bundle();
            //bundle.put("some_int", 0);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.contentView, MainPageFragment.class, bundle)
                    .setReorderingAllowed(true)
                    .addToBackStack("MainPage") // Name can be null
                    .commit();
        }
        // else we need to load the SignIn Activity
        else {
            // Change activity to SignInActivity
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        // Create AlarmReminder
        setAlarmedReminder();
    }

    /**
     * This is the setListeners method for the different buttons in the navigation bar
     **/
    private void setListeners() {
        // This sets the listener for the profile menu
        imageProfile.setOnClickListener(v -> {
            // Initializing the popup menu and give it the style and reference anchor
            PopupMenu popupMenu = new PopupMenu(new ContextThemeWrapper(MainActivity.this, R.style.Theme_ProfileMenuPopup), imageProfile);
            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            // Set the item click listeners
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                /**
                 * This is the override for the handling of menu clicks
                 * @param menuItem this is the menu item clicked
                 * @return a boolean that reflects the state of the item loader
                 */
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    // Toast message on menu item clicked
                    Toast.makeText(MainActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                    preferenceManager.putBoolean(Constants.User.KEY_IS_SIGNED_IN,false);
                    // Change activity to SignInActivity
                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
            });
            // Showing the popup menu
            popupMenu.show();
        });
    }

    /**
     * Build the navigation bar dynamically based on GridView design
     */
    private void buildNavigation() {
        // Create the array for the adaptor
        ArrayList<NavigationCard> navigationCardArrayList = new ArrayList<NavigationCard>();
        // Create the navigation elements
        navigationCardArrayList.add(new NavigationCard("Home",R.drawable.home));
        navigationCardArrayList.add(new NavigationCard("Exams",R.drawable.calendar));
        navigationCardArrayList.add(new NavigationCard("Reminders",R.drawable.reminder_bell));
        navigationCardArrayList.add(new NavigationCard("Logout",R.drawable.logout));

        // Setup the adaptor
        final NavigationAdaptor navigationAdaptor = new NavigationAdaptor(this,navigationCardArrayList);
        navigationGrid.setAdapter(navigationAdaptor);

        // Setup the onclick listeners
        navigationGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * This is the override method for onItemClick
             * @param parent this is the parent adapter
             * @param view this is the view
             * @param position this is the position
             * @param id this is the id
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView button = view.findViewById(R.id.cardImage);
                Bundle bundle;
                FragmentManager fragmentManager;

                // Handle the position in the grid
                switch (position) {
                    case 0:
                        // change the color of the current image view button
                        button.setImageResource(R.drawable.home2);
                        // delay the color change to half a second before reverting back to original color
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setImageDrawable(ContextCompat.getDrawable(button.getContext(), R.drawable.home));
                            }
                        }, 500);
                        // Load the fragment for the Main Page
                        // Create a new bundle for passed data
                        bundle = new Bundle();
                        //bundle.put("some_int", 0);

                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.contentView, MainPageFragment.class, bundle)
                                .setReorderingAllowed(true)
                                .addToBackStack("MainPage") // Name can be null
                                .commit();
                        break;
                    case 1:
                        // change the color of the current image view button
                        button.setImageResource(R.drawable.calendar2);
                        // delay the color change to half a second before reverting back to original color
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setImageDrawable(ContextCompat.getDrawable(button.getContext(), R.drawable.calendar));
                            }
                        }, 500);
                        // Load the fragment for the Exam Page
                        // Create a new bundle for passed data
                        bundle = new Bundle();
                        //bundle.put("some_int", 0);

                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.contentView, TestPageFragment.class, bundle)
                                .setReorderingAllowed(true)
                                .addToBackStack("ExamManager") // Name can be null
                                .commit();
                        break;
                    case 2:
                        // change the color of the current image view button
                        button.setImageResource(R.drawable.reminder_bell2);
                        // delay the color change to half a second before reverting back to original color
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setImageDrawable(ContextCompat.getDrawable(button.getContext(), R.drawable.reminder_bell));
                            }
                        }, 500);
                        // Load the fragment for the Reminder Page
                        // Create a new bundle for passed data
                        bundle = new Bundle();
                        //bundle.put("some_int", 0);

                        fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.contentView, ReminderPageFragment.class, bundle)
                                .setReorderingAllowed(true)
                                .addToBackStack("ReminderManager") // Name can be null
                                .commit();

                        break;
                        // Test case for logout in Navigation bar
                    case 3:
                        // change the color of the current image view button
                        button.setImageResource(R.drawable.logout2);
                        // delay the color change to half a second before reverting back to original color
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                button.setImageDrawable(ContextCompat.getDrawable(button.getContext(), R.drawable.logout));
                                // Logout the user out
                                Toast.makeText(MainActivity.this, "Logging you out", Toast.LENGTH_LONG).show();
                                preferenceManager.putBoolean(Constants.User.KEY_IS_SIGNED_IN,false);
                                // Change activity to SignInActivity
                                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }, 500);
                }
            }
        });
    }

    /**
     * Load the user's profile image and other details
     * into the home page
     */
    private void loadUserDetails() {
        if (preferenceManager.getString(Constants.User.KEY_IMAGE) != null) {
            byte[] bytes = Base64.decode(preferenceManager.getString(Constants.User.KEY_IMAGE),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            Log.d("Image Info:",preferenceManager.getString(Constants.User.KEY_IMAGE));
            imageProfile.setImageBitmap(bitmap);
        }
    }

    /**
     * A app wide scheduled task executor method that sends a reminder data the database
     * at a certain time every day.
     */
    private void setAlarmedReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, TimeCheckReceiver.class);

        // Use PendingIntent.FLAG_NO_CREATE to check if the alarm exists
        PendingIntent existingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE
        );

        // If the alarm is already set, skip setting it again
        if (existingIntent != null) {
            Log.d("AlarmManager", "Alarm already set. Skipping initialization.");
            return;
        }

        // Otherwise, create a new PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Get the current time
        Calendar calendar = Calendar.getInstance();

        // Set the alarm to 7:00 AM
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // If the alarm time is in the past, adjust it to the next day
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Set the alarm
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d("AlarmManager", "Alarm set for: " + calendar.getTime());
        }
    }
}