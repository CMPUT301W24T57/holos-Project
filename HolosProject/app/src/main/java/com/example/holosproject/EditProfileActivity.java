package com.example.holosproject;

import android.content.Intent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

/**
 * FileName: EditProfileActivity
 * Description: Contains the logic for Editing a users profile.
 * Brought to this screen when a user selects the "Edit Profile" option from the drawer menu.

 * XML Files associated with this are: activity_edit_profile.xml
 **/

public class EditProfileActivity extends AppCompatActivity {

    private final String TAG = "EditProfileActivity";
    private EditText editTextName, editTextHomepage, editTextContact;
    private Button finishEditProfileButton, removeProfileImageButton;
    private Switch geolocationSwitch;
    private static final int PICK_IMAGE_REQUEST = 123; // Constant for the request code for picking image
    private ImageUploader imageUploader; // Instance variable for the ImageUploader


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Getting the current user from Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize EditTexts and other UI components
        editTextName = findViewById(R.id.editTextName);
        editTextHomepage = findViewById(R.id.editTextHomepage);
        editTextContact = findViewById(R.id.editTextContact);
        geolocationSwitch = findViewById(R.id.switchGeolocation);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            fetchUserProfile(uid);
        } else {
            // Handle case where there is no logged-in user
            Log.d(TAG, "No user logged in");
        }

        // Initialize buttons for finishing edits to profile, and for removing uploaded profile image
        finishEditProfileButton = findViewById(R.id.buttonFinishProfileCreation);
        removeProfileImageButton = findViewById(R.id.buttonRemoveProfileImage);

        finishEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // Initialize the ImageUploader with a listener
        imageUploader = new ImageUploader(new ImageUploader.ImageUploadListener() {
            @Override
            public void onImageUploadSuccess(String downloadUrl) {
                // Image uploaded successfully
                Toast.makeText(EditProfileActivity.this, "Image Uploaded!", Toast.LENGTH_SHORT).show();
                removeProfileImageButton.setVisibility(View.VISIBLE); // Make the remove button visible
                // Set the image URL in the user's profile here if needed
            }
            @Override
            public void onImageUploadFailure(Exception e) {
                // Image upload failed
                Toast.makeText(EditProfileActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the button for editing the profile image
        ImageView profileImage = findViewById(R.id.imageViewProfile);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to pick an image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        // Set up the remove image button click listener
        removeProfileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeProfileImage(currentUser.getUid());
            }
        });
    }

    /**
     * fetchUserProfile retrieves the current user profile and populates UI fields with the data.
     *
     * @param uid The user ID to fetch the profile for.
     */
    private void fetchUserProfile(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userProfileRef = db.collection("userProfiles").document(uid);

        userProfileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Populate the UI with the user's profile data
                        editTextName.setText(document.getString("name"));
                        editTextContact.setText(document.getString("contact"));
                        editTextHomepage.setText(document.getString("homepage"));

                        // Set the switch to the value stored in the user's profile
                        Boolean geolocation = document.getBoolean("geolocationEnabled");
                        geolocationSwitch.setChecked(geolocation != null && geolocation);

                        // Populate the image view with profile image (if it exists)
                        String imageUrl = document.getString("profileImageUrl");
                        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                            ImageView profileImage = findViewById(R.id.imageViewProfile);
                            Glide.with(EditProfileActivity.this).load(imageUrl).into(profileImage);     // Using Glide to load images
                            removeProfileImageButton.setVisibility(View.VISIBLE);   // If the user does have a profile image, then we can set the "remove profile image" button to visible
                        }
                    } else {
                        Log.d(TAG, "No such document");
                        removeProfileImageButton.setVisibility(View.GONE); // Hide removeImageButton if no profile image exists
                        // Handle case where the document doesn't exist
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    // Handle the failure
                }
            }
        });
    }

    /**
     * updateProfile validates input data and updates the user's profile in Firebase Firestore.
     */
    private void updateProfile() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Get the current user's ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Collect data from input fields
        String newName = editTextName.getText().toString();
        String newContact = editTextContact.getText().toString();
        String newHomepage = editTextHomepage.getText().toString();
        boolean geolocationEnabled = geolocationSwitch.isChecked();

        // Validate input data
        if (newName.isEmpty() || !isValidName(newName)) {
            editTextName.setError("Please enter a valid name.");
            return;
        }
        if (newContact.isEmpty() || !isValidEmail(newContact)) {
            editTextContact.setError("Please enter a valid email address.");
            return;
        }
        if (newHomepage.isEmpty() || !isValidUrl(newHomepage)) {
            editTextHomepage.setError("Please enter a valid URL.");
            return;
        }

        // Create a map of the data to update
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("name", newName);
        updatedUserData.put("contact", newContact);
        updatedUserData.put("homepage", newHomepage);
        updatedUserData.put("geolocationEnabled", geolocationEnabled);
        // Add more fields to update here

        // Update the user's profile document in Firestore
        db.collection("userProfiles").document(userId)
                .update(updatedUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Handle success
                        Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure
                        Toast.makeText(EditProfileActivity.this, "Profile Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //  handle image selection from user
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            // Set the image URI to the ImageView and start uploading
            ImageView profileImage = findViewById(R.id.imageViewProfile);
            profileImage.setImageURI(imageUri);
            imageUploader.uploadProfileImage(imageUri); // Upload the image
        }
    }

    // Method to remove the profile image (activated when user presses "Remove Profile Image" button)
    private void removeProfileImage(String userId) {
        // Remove image from Firebase Storage
        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profileImages/" + userId + ".jpg");
        profileImageRef.delete().addOnSuccessListener(aVoid -> {
            // Image removed from Storage, now remove the URL from Firestore
            FirebaseFirestore.getInstance().collection("userProfiles").document(userId)
                    .update("profileImageUrl", FieldValue.delete())
                    .addOnSuccessListener(aVoid1 -> {
                        Toast.makeText(EditProfileActivity.this, "Profile image removed.", Toast.LENGTH_SHORT).show();
                        ImageView profileImage = findViewById(R.id.imageViewProfile);
                        profileImage.setImageResource(R.drawable.ic_launcher_foreground); // Set default image
                        removeProfileImageButton.setVisibility(View.GONE); // Hide the remove button
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to remove profile image.", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Toast.makeText(EditProfileActivity.this, "Failed to remove profile image from storage.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Check if the name is valid.
     * For example, it can't be empty and it can't contain digits or special characters.
     */
    private boolean isValidName(String name) {
        return name.matches("[a-zA-Z\\s]+"); // This regex makes it so name only has alphabetic characters and whitespace
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

}
