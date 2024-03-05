package com.example.holosproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * FileName: MainActivity
 * Description: MainActivity currently servers (i believe) zero purpose. It is not the activity that is run opening the app, ive changed that to be FirstTimeProfileCreation.

 *  Associated with the activity_main layout, but ive made no changes to it, and that layout is also currently serving no purpose. I think i will end up deleting these two files.
 **/

public class MainActivity extends AppCompatActivity {

    // Declaring a private variable to hold a reference to the Firestore database
    private FirebaseFirestore database;
    // This next line is an example of what it would look like to declare a CollectionReference Variable,
    // In Firestore, a collection is a group of documents. A CollectionReference is a reference to a specific collection in the Firestore database.
    private CollectionReference userAccountNamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the Firestore database instance when the activity is created
        database = FirebaseFirestore.getInstance();
        // This next line initializes "userAccountNamesRef" by obtaining a reference to the "Profile Account Names" collection in the Firestore database.
        // userAccountNamesRef holds a reference to the "Proifle Account Names" collection in our database.
        userAccountNamesRef = database.collection("Profile Account Names");
    }
}