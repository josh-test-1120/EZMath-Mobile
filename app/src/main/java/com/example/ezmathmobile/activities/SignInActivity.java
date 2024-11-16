package com.example.ezmathmobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivitySignInBinding;
import com.example.ezmathmobile.utilities.Constants;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * This is the Sign In Activity view
 */
public class SignInActivity extends AppCompatActivity {
    // Instance Variables
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    /**
     * This is an override of the onCreate method
     * @param savedInstanceState the current saved state of the instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Attach the view binding
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Attach the preferences
        preferenceManager = new PreferenceManager(getApplicationContext());
        // Attach the button listeners
        setListeners();
    }

    /**
     * This will set the listener for the various buttons
     * in the view
     */
    private void setListeners() {
        // Set the create new account button to
        // Change to the sign up activity
        binding.createNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class)));
        // Sign in button does validations, then signs in
        binding.signInButton.setOnClickListener(v ->  {
            if (signInValidations()) {
                SignIn();
            }
        });

    }

    /**
     * Process the sign in action
     * This will handle UI interactions, data updates
     * and validations
     */
    private void SignIn() {
        // Check loading
        loadingData(true);
        // Initialize the database object
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        // Get from firebase database
        database.collection(Constants.KEY_COLLECTION_USERS)
                // ensure password matches
                .whereEqualTo(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString())
                // ensure email matches
                .whereEqualTo(Constants.KEY_EMAIL,binding.inputEmail.getText().toString())
                .get()
                // If all matches
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        // Get the database document data
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        // Update the preferences
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USERID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_FIRSTNAME,documentSnapshot.getString(Constants.KEY_FIRSTNAME));
                        preferenceManager.putString(Constants.KEY_LASTNAME,documentSnapshot.getString(Constants.KEY_LASTNAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        // Notify that login was valid
                        showToast("Login Successful");
                        // Change to the Main Activity view
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    // Failure to confirm or find user
                    else {
                        loadingData(false);
                        showToast("Unable to Sign in with credentials");
                    }
                });
    }

    /**
     * Updates the UI elements when data fetching and processing
     * happens and when it completes
     * @param isLoading this is the current loading state
     */
    private void loadingData(Boolean isLoading) {
        if (isLoading) {
            binding.signInButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.signInButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Validate the sign in form inputs to ensure
     * they conform to database contracts (constraints)
     * or valid pattern types
     * @return result of the validation checks
     */
    private boolean signInValidations() {
        // Get some of the variables from binding
        String email = binding.inputEmail.getText().toString();
        // Conditional checks for conformance of inputs
        if (email.trim().isEmpty()) {
            showToast("Please enter your email");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address");
            return false;
        }
        else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Please enter your password");
            return false;
        }
        // All validations passed
        else {
            return true;
        }
    }

    /**
     * Launches a toast based on message received in consistent fashion
     * @param message string to place into toast
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}