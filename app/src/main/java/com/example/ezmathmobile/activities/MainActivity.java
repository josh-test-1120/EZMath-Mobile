package com.example.ezmathmobile.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ezmathmobile.adaptors.ExamPageAdaptor;
import com.example.ezmathmobile.adaptors.MainPageAdaptor;
import com.example.ezmathmobile.adaptors.ReminderPageAdaptor;
import com.example.ezmathmobile.R;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

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
    private RoundedImageView imageProfile;
    PreferenceManager preferenceManager;

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

        imageProfile = findViewById(R.id.imageProfile);

        setListeners();
        loadUserDetails();
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
            final ExamPageAdaptor examPageAdaptor = new ExamPageAdaptor();
            contentView.setAdapter(examPageAdaptor);
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
        // This sets the listener for the profile menu
        imageProfile.setOnClickListener(v -> {
            // Initializing the popup menu and give it the style and reference anchor
            //PopupMenu popupMenu = new PopupMenu(MainActivity.this, imageProfile);
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

    private void loadUserDetails() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.User.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        Log.d("Image Info:",preferenceManager.getString(Constants.User.KEY_IMAGE));
        imageProfile.setImageBitmap(bitmap);
    }
}