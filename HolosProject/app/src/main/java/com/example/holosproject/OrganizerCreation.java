package com.example.holosproject;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * FileName: FirstTimeProfileCreationActivity
 * Description: This is the activity that is shown after a user selects their role, prompting them to create a profile by entering their information.
 *  If the user fills in all the fields, we will try to add the data to firebase. If it gets added, we move to the Attendee Dashboard. Otherwise stay on this page.

 *  Associated with the activity_first_time_profile_creation.xml layout.
 **/

public class OrganizerCreation extends AppCompatActivity {
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
    private static final String TAG = "OrganizerCreation";

    // Firestore database reference
    private FirebaseFirestore database;
    private CollectionReference userProfileInfoRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    String emailpad = "@holos.project";


    /**
     * This activity allows organizers to create their profiles.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_creation);
        mAuth = FirebaseAuth.getInstance();
        imageViewProfile = findViewById(R.id.organizer_imageViewProfile);
        editTextName = findViewById(R.id.organizer_editTextName);
        editTextContact = findViewById(R.id.organizer_editTextContact);
        editTextHomepage = findViewById(R.id.organizer_editTextHomepage);
        switchNotifications = findViewById(R.id.organizer_switchNotifications);
        switchGeolocation = findViewById(R.id.organizer_switchGeolocation);
        buttonFinishProfileCreation = findViewById(R.id.organizer_buttonFinishProfileCreation);

        // TODO: Set up a listener for the imageViewProfile to open an image selector, handle uploading an image, etc
        // TODO: Finish setting up profile creation with profile image support, currently have no profile image support yet.

        // Initialize Firestore

        buttonFinishProfileCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString().trim();
                final String contact = editTextContact.getText().toString().trim();
                final String homepage = editTextHomepage.getText().toString().trim();
                createAccount(name, contact, homepage);
            }
        });

    }

    /**
     * Creates the account with email and password.
     *
     * @param name     The user's name.
     * @param contact  The user's contact information.
     * @param homepage The user's homepage.
     */
    private void createAccount(String name, String contact, String homepage) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(OrganizerCreation.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUser:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // The user information that will be stored
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("role", "attendee");
                            userProfile.put("name", name);
                            userProfile.put("contact", contact);
                            userProfile.put("homepage", homepage);
                            userProfile.put("myEvents", new ArrayList<String>());
                            userProfile.put("createdEvents", new ArrayList<String>());
                            // TODO: Add the image name or image reference to userProfile map

                            // Store the user profile in Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("userProfiles").document(user.getUid())
                                    .set(userProfile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
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
                            Toast.makeText(OrganizerCreation.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    /**
     * Updates the UI based on the user's authentication status.
     *
     * @param user The FirebaseUser object representing the current user.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // If the user is signed in go to the next activity
            Intent intent = new Intent(OrganizerCreation.this, MainActivity.class);
            startActivity(intent);
            finish(); // prevents them from being able to use the back button
        } else {
            // Sign-in has failed
            Toast.makeText(OrganizerCreation.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}



