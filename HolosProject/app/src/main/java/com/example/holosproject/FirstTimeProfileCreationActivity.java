package com.example.holosproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
        mAuth.signInAnonymously()
                .addOnCompleteListener(FirstTimeProfileCreationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // TODO: Add data validation
                            // The user information that will be stored
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("role", "attendee");
                            userProfile.put("name", name);
                            userProfile.put("contact", contact);
                            userProfile.put("homepage", homepage);
                            userProfile.put("myEvents", new ArrayList<String>());
                            userProfile.put("createdEvents", new ArrayList<String>());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUriToUpload = data.getData(); // Save the selected image URI
            imageViewProfile.setImageURI(imageUriToUpload); // Update the ImageView
        }
    }

    // method to upload the image
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

}
