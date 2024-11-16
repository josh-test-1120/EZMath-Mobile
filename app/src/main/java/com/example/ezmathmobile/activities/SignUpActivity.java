package com.example.ezmathmobile.activities;

import com.example.ezmathmobile.utilities.Constants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ezmathmobile.R;
import com.example.ezmathmobile.databinding.ActivitySignInBinding;
import com.example.ezmathmobile.databinding.ActivitySignUpBinding;
import com.example.ezmathmobile.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * This is the Sign Up Activity view
 */
public class SignUpActivity extends AppCompatActivity {
    // Instance Variables
    private ActivitySignUpBinding binding;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    /**
     * This is an override of the onCreate method
     * @param savedInstanceState the current saved state of the instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Attach the view binding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
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
        // Set the Sign in button to go back to initial page
        binding.SignIn.setOnClickListener(v -> onBackPressed());
        // Have the sign up button validate and then handle the sign up
        binding.signUpButton.setOnClickListener(v -> {
            if (signUpValidations()) {
                signUp();
            }
        });
        // Have the image layout click load images so one can be picked
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    /**
     * Launches a toast based on message received in consistent fashion
     * @param message string to place into toast
     */
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Process the sign up action
     * This will handle UI interactions, data updates
     * and validations
     */
    private void signUp() {
        // Check loading
        loadingData(true);
        // Initialize the database object and user Hash
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        int studentID = 1 + (int)(Math.random() * 65535); // 1 to 65535
        // Create the user according to database contract hash
        user.put(Constants.KEY_USERID,Integer.toString(studentID));
        user.put(Constants.KEY_FIRSTNAME,binding.inputFirstName.getText().toString());
        user.put(Constants.KEY_LASTNAME,binding.inputLastName.getText().toString());
        user.put(Constants.KEY_EMAIL,binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD,binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE,encodedImage);
        // Post to firebase database
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                // Success adding user
                .addOnSuccessListener(documentReference -> {
                    loadingData(false);
                    // Save the user information to Preference Manager
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(Constants.KEY_USERID,documentReference.getId());
//                    preferenceManager.putString(Constants.KEY_USERID,Integer.toString(studentID));
                    preferenceManager.putString(Constants.KEY_FIRSTNAME,binding.inputFirstName.getText().toString());
                    preferenceManager.putString(Constants.KEY_LASTNAME,binding.inputLastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                    // Change activity to MainActivity
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                // Failure to add user
                .addOnFailureListener(exception -> {
                    loadingData(false);
                    showToast(exception.getMessage());
                });
    }

    /**
     * Encodes a BitMap object into a BASE64 string so it can be
     * stored in a database
     * @param image bitmap image to be encoded
     * @return string of the image in BASE64
     */
    private String encodeImage(Bitmap image) {
        int previewWidth = 150;
        int previewHeight = image.getHeight() * previewWidth / image.getWidth();

        Bitmap previewImage = Bitmap.createScaledBitmap(image,previewWidth,previewHeight,false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        previewImage.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    /**
     * This will take the result of an activity that picks an image
     * and then decode the image represented as a BASE64 string
     */
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // If the Intent response was ok
                if (result.getResultCode() == RESULT_OK) {
                    // Get the URI data
                    Uri imageUri = result.getData().getData();
                    // Try and decode
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap image = BitmapFactory.decodeStream(inputStream);

                        binding.imageProfile.setImageBitmap(image);
                        binding.addImageMessage.setVisibility(View.GONE);
                        encodedImage = encodeImage(image);
                    }
                    // Exception if decode fails
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    /**
     * Validate the sign up form inputs to ensure
     * they conform to database contracts (constraints)
     * or valid pattern types
     * @return result of the validation checks
     */
    private Boolean signUpValidations() {
        // Get some of the variables from binding
        String firstName = binding.inputFirstName.getText().toString();
        String lastName = binding.inputLastName.getText().toString();
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        String passwordConfirm = binding.inputConfirmPassword.getText().toString();
        // Conditional checks for conformance of inputs
        if (encodedImage == null) {
            showToast("Please select an image for profile");
            return false;
        }
        else if (firstName.trim().isEmpty() || lastName.trim().isEmpty()) {
            showToast("Please enter your first and last name");
            return false;
        }
        else if (email.trim().isEmpty()) {
            showToast("Please enter your email");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address");
            return false;
        }
        else if (password.trim().isEmpty()) {
            showToast("Please enter your password");
            return false;
        }
        else if (passwordConfirm.trim().isEmpty()) {
            showToast("Please confirm your password");
            return false;
        }
        else if (!password.equals(passwordConfirm)) {
            showToast("Password and confirmation do not match and must be the same");
            return false;
        }
        // All validations passed
        else {
            return true;
        }
    }

    /**
     * Updates the UI elements when data fetching and processing
     * happens and when it completes
     * @param isLoading this is the current loading state
     */
    private void loadingData(Boolean isLoading) {
        if (isLoading) {
            binding.signUpButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.signUpButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}