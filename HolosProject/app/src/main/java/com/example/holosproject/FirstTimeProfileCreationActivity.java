package com.example.holosproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * FileName: FirstTimeProfileCreationActivity
 * Description: This is the activity that is shown after a user selects their role, prompting them to create a profile by entering their information.
 *  If the user fills in all the fields, we will try to add the data to firebase. If it gets added, we move to the Attendee Dashboard. Otherwise stay on this page.

 *  Associated with the activity_first_time_profile_creation.xml layout.
 **/

public class FirstTimeProfileCreationActivity extends AppCompatActivity {
    // This activity is shown during the first runtime of the app. If a user initially selects "Attendee", then this
    // activity will appear, prompting them to create a profile.

    // UI Elements
    private ImageView imageViewProfile;
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private EditText editTextContact;
    private EditText editTextHomepage;
    private Switch switchNotifications;
    private Switch switchGeolocation;
    private Button buttonFinishProfileCreation;
    private static final String TAG = "EmailPasswordActivity";
    private static final int PICK_IMAGE_REQUEST = 1; // Define a request code for image picking
    private ImageUploader imageUploader; // Instance variable for the ImageUploader
    private String uploadedImageUrl = null; // This will store the uploaded image URL
    private Uri imageUriToUpload = null; // This will temporarily store the selected image URI


    // Firestore database reference
    private FirebaseFirestore database;
    private CollectionReference userProfileInfoRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    // location stuff
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    /**
     * Overrides the onCreate method to initialize the first-time profile creation activity.
     * Sets the content view to the first-time profile creation layout.
     * Initializes FirebaseAuth instance and UI components such as image view, edit texts, switches, and buttons.
     * @param savedInstanceState A Bundle containing the saved state of the activity.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_profile_creation);
        mAuth = FirebaseAuth.getInstance();
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextName = findViewById(R.id.editTextName);
        editTextContact = findViewById(R.id.editTextContact);
        editTextHomepage = findViewById(R.id.editTextHomepage);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchGeolocation = findViewById(R.id.switchGeolocation);
        buttonFinishProfileCreation = findViewById(R.id.buttonFinishProfileCreation);



        buttonFinishProfileCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString().trim();
                final String contact = editTextContact.getText().toString().trim();
                final String homepage = editTextHomepage.getText().toString().trim();
                createAccount(name, contact, homepage);
            }
        });

        imageUploader = new ImageUploader(new ImageUploader.ImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String downloadUrl) {
                // Store the image URL to add it to the profile later
                uploadedImageUrl = downloadUrl;
                Toast.makeText(FirstTimeProfileCreationActivity.this, "Image uploaded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onImageUploadFailure(Exception e) {
                // Handle the failure, e.g., show a message
                Toast.makeText(FirstTimeProfileCreationActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }


        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        switchGeolocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchGeolocation.isChecked()) {
                    ActivityCompat.requestPermissions(FirstTimeProfileCreationActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FirstTimeProfileCreationActivity.this);
                    // Set the title and message for the dialog
                    builder.setTitle("Notice")
                            .setMessage("You must go into app permissions to revoke location privileges.")
                            .setCancelable(false) // Set if dialog is cancelable
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss(); // Dismiss the dialog
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    switchGeolocation.setChecked(true);
                }
            }
        });
    }

    /**
     * Method for selecting an image to be uploaded as a profile image
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Creating an account without email or password.
     * @param name
     * The users name.
     * @param contact
     * The users password.
     * @param homepage
     * The users homepage.
     */
    private void createAccount(String name, String contact, String homepage) {
        // first, validate the inputs
        if (name.isEmpty() || !isValidName(name)) {
            editTextName.setError("Please enter a valid name.");
            return;
        }
        if (contact.isEmpty() || !isValidEmail(contact)) {
            editTextContact.setError("Please enter a valid email address.");
            return;
        }
        if (homepage.isEmpty() || !isValidUrl(homepage)) {
            editTextHomepage.setError("Please enter a valid URL.");
            return;
        }



        // Get the state of the geolocation switch
        boolean isGeolocationEnabled = switchGeolocation.isChecked();

        mAuth.signInAnonymously()
                .addOnCompleteListener(FirstTimeProfileCreationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                            // The user information that will be stored
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("role", "attendee");
                            userProfile.put("name", name);
                            userProfile.put("contact", contact);
                            userProfile.put("homepage", homepage);
                            userProfile.put("myEvents", new ArrayList<String>());
                            userProfile.put("createdEvents", new ArrayList<String>());
                            userProfile.put("geolocationEnabled", isGeolocationEnabled);



                            // Check if the image has been uploaded and set the URL
                            if (uploadedImageUrl != null && !uploadedImageUrl.isEmpty()) {
                                userProfile.put("profileImageUrl", uploadedImageUrl);
                            }

                            // Store the user profile in Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("userProfiles").document(user.getUid())
                                    .set(userProfile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            if (imageUriToUpload != null) {
                                                // Upload users profile image after creating the profile
                                                uploadProfileImage(imageUriToUpload, user.getUid());
                                            }
                                            updateUI(user);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                            updateUI(null);
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FirstTimeProfileCreationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    /**
     * Updates the user interface based on the provided FirebaseUser.
     * If the user is signed in, it starts the MainActivity and finishes the current activity to prevent users from using the back button.
     * If authentication fails, it displays a toast message indicating authentication failure.
     * @param user The FirebaseUser object representing the signed-in user.
     */

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // If the user is signed in go to the next activity
            Intent intent = new Intent(FirstTimeProfileCreationActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // prevents them from being able to use the back button
        } else {
            // authentication failed
            Toast.makeText(FirstTimeProfileCreationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the result of an activity started for result.
     * If the result is from picking an image and it's successful, it saves the selected image URI and updates the ImageView with the selected image.
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode The result code returned by the child activity.
     * @param data An Intent that carries the result data.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriToUpload = data.getData(); // Save the selected image URI
            imageViewProfile.setImageURI(imageUriToUpload); // Update the ImageView
        }
    }

    // Method to upload profile image.
    // We call this method AFTER the profile was successfully created.
    /**
     * Uploads the selected profile image to Firebase Storage and updates the user's profile image URL in Firestore.
     * @param imageUri The URI of the selected image to upload.
     * @param userId The ID of the user whose profile image is being updated.
     */
    private void uploadProfileImage(Uri imageUri, String userId) {
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profileImages/" + userId + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();

                    // Directly update the Firestore document with the download URL
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("userProfiles").document(userId)
                            .update("profileImageUrl", downloadUrl)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(FirstTimeProfileCreationActivity.this, "Profile image updated.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(FirstTimeProfileCreationActivity.this, "Failed to update profile image URL.", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(FirstTimeProfileCreationActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Check if the name is valid.
     */
    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z\\s]+"); // Adjust regex as per your requirements
    }

    /**
     * Check if the email is valid.
     */
    private boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Check if the URL is valid.
     */
    private boolean isValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchGeolocation.setChecked(true);
            }
            else {
                switchGeolocation.setChecked(false);
            }
        }
    }

}
