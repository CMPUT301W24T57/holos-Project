package com.example.holosproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirstTimeProfileCreationActivity extends AppCompatActivity {
    // This activity is shown during the first runtime of the app. If a user initially selects "Attendee", then this
    // activity will appear, prompting them to create a profile.

    // UI Elements
    private ImageView imageViewProfile;
    private EditText editTextName;
    private EditText editTextContact;
    private EditText editTextHomepage;
    private Switch switchNotifications;
    private Switch switchGeolocation;
    private Button buttonFinishProfileCreation;

    // Firestore database reference
    private FirebaseFirestore database;
    private CollectionReference userProfileInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_profile_creation);

        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextName = findViewById(R.id.editTextName);
        editTextContact = findViewById(R.id.editTextContact);
        editTextHomepage = findViewById(R.id.editTextHomepage);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchGeolocation = findViewById(R.id.switchGeolocation);
        buttonFinishProfileCreation = findViewById(R.id.buttonFinishProfileCreation);

        // TODO: Set up a listener for the imageViewProfile to open an image selector, handle uploading an image, etc
        // TODO: Finish setting up profile creation with profile image support, currently have no profile image support yet.

        // Initialize Firestore
        database = FirebaseFirestore.getInstance();
        userProfileInfoRef = database.collection("User Profile Data");

        buttonFinishProfileCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    // This method validates that all of the fields contain data. If they do, then we create this users profile and store their data on Firebase.
    // TODO: Store Android ID On firebase so we can link each profiles data to each phone. We may not have to implement this before Project 3 Deadline.
    private void saveUserProfile() {
        String name = editTextName.getText().toString().trim();
        String contact = editTextContact.getText().toString().trim();
        String homepage = editTextHomepage.getText().toString().trim();

        // Do we need to store geolocation/notifications settings in the database? Can this get stored within the app?
        boolean notifications = switchNotifications.isChecked();
        boolean geolocation = switchGeolocation.isChecked();

        // Validating that profile creation fields are not empty
        if (name.isEmpty() || contact.isEmpty() || homepage.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new user profile map to store the values
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("name", name);
        userProfile.put("contact", contact);
        userProfile.put("homepage", homepage);
        userProfile.put("notifications", notifications);
        userProfile.put("geolocation", geolocation);

        // Add a new document with a generated ID to the collection
        userProfileInfoRef.add(userProfile)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Handles the successful addition of the profile, can now navigate to another activity
                        Toast.makeText(FirstTimeProfileCreationActivity.this, "Profile Created", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Log.e("ProfileCreation", "Error creating profile", e);
                        Toast.makeText(FirstTimeProfileCreationActivity.this, "Error creating profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
