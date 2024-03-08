package com.example.holosproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private Button finishEditProfileButton;

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

        if (currentUser != null) {
            String uid = currentUser.getUid();
            fetchUserProfile(uid);
        } else {
            // Handle case where there is no logged-in user
            Log.d(TAG, "No user logged in");
        }

        // Set up the button for finishing the edits to the profile
        finishEditProfileButton = findViewById(R.id.buttonFinishProfileCreation);
        finishEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        // The back button on the EditProfileActivity
        FloatingActionButton fabBack = findViewById(R.id.buttonEditProfileBack);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the activity to go back to the previous screen
                finish();
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
                        // Populate other fields similarly
                    } else {
                        Log.d(TAG, "No such document");
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

        // Validate input data
        if (newName.isEmpty() || newContact.isEmpty() || newHomepage.isEmpty()) {
            // Show error message
            return;
        }

        // Create a map of the data to update
        Map<String, Object> updatedUserData = new HashMap<>();
        updatedUserData.put("name", newName);
        updatedUserData.put("contact", newContact);
        updatedUserData.put("homepage", newHomepage);
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
}
