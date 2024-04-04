package com.example.holosproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * FileName: MainActivity
 * Description: checks if user is signed in and navigates them to their destination or login
 **/

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    // Declaring a private variable to hold a reference to the Firestore database
    private FirebaseFirestore database;
    // Firebase email authentication
    private FirebaseAuth mAuth;
    // This next line is an example of what it would look like to declare a CollectionReference Variable,
    // In Firestore, a collection is a group of documents. A CollectionReference is a reference to a specific collection in the Firestore database.
    private CollectionReference userAccountNamesRef;
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

        /*
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                    }
                });
        */
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToTestSuccessDashboard();
        } else {
            goToIntro();
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
}