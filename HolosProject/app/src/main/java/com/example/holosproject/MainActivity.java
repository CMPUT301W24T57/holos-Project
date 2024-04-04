package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * FileName: MainActivity
 * Description: checks if user is signed in and navigates them to their destination or login
 **/

public class MainActivity extends AppCompatActivity {

    // Declaring a private variable to hold a reference to the Firestore database
    private FirebaseFirestore database;
    // Firebase email authentication
    private FirebaseAuth mAuth;
    // This next line is an example of what it would look like to declare a CollectionReference Variable,
    // In Firestore, a collection is a group of documents. A CollectionReference is a reference to a specific collection in the Firestore database.
    private CollectionReference userAccountNamesRef;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // Initializing the Firestore database instance when the activity is created
        database = FirebaseFirestore.getInstance();
        // This next line initializes "userAccountNamesRef" by obtaining a reference to the "Profile Account Names" collection in the Firestore database.
        // userAccountNamesRef holds a reference to the "Profile Account Names" collection in our database.
        userAccountNamesRef = database.collection("Profile Account Names");
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToTestSuccessDashboard();
        } else {
            createNewUser();
        }
    }

    /**
     * Navigates to IntroActivity
     */
    private void goToIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish(); // Prevents user from clicking back button
    }

    /**
     * Navigates to test
     */
    private void goToTestSuccessDashboard() {
        Intent intent = new Intent(this, TestSuccessScreen.class);
        startActivity(intent);
        finish(); // Prevents user from clicking back button
    }

    private void createNewUser() {
        mAuth = FirebaseAuth.getInstance();
        String defaultUrl = "https://firebasestorage.googleapis.com/v0/b/cmput-301-holosproject.appspot.com/o/profileImages%2Fdefault.png?alt=media&token=c8fccd35-cabe-4274-9f9a-f4c0607b2e4c";
        mAuth.signInAnonymously()
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // The user information that will be stored
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("role", "attendee");
                            userProfile.put("name", "Anonymous User");
                            userProfile.put("contact", "None");
                            userProfile.put("homepage", "None");
                            userProfile.put("myEvents", new ArrayList<String>());
                            userProfile.put("createdEvents", new ArrayList<String>());
                            userProfile.put("geolocationEnabled", false);

                            userProfile.put("profileImageUrl", defaultUrl);

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
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // If the user is signed in go to the next activity
            Intent intent = new Intent(MainActivity.this, TestSuccessScreen.class);
            startActivity(intent);
            finish(); // prevents them from being able to use the back button
        } else {
            // authentication failed
            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }

}