package com.example.holosproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddEventActivity extends AppCompatActivity {
    private EditText eventName;
    private EditText eventDate;
    private EditText eventTime;
    private EditText eventAddress;
    private EditText eventDescription;// leaving out for now
    private final String TAG = "addEventActivityScreen";
    private FirebaseUser currentUser;
    private Button save;
    private Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        eventName = findViewById(R.id.edit_text_event_name);
        eventTime = findViewById(R.id.edit_text_event_time);
        eventDate = findViewById(R.id.edit_text_event_date);
        eventAddress = findViewById(R.id.edit_text_event_address);
        cancel = findViewById(R.id.button_cancel);
        save = findViewById(R.id.button_save);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent(eventName.getText().toString(), eventTime.getText().toString(),eventDate.getText().toString(),eventAddress.getText().toString());
            }
        });

        // Here, you'll have your logic for adding events
        // This includes initializing your views, setting up click listeners, and handling the event creation process
        // ...
    }

    /*
    Adds the event object to events in firebase.
     */
    private void saveEvent(String eventName, String eventTime, String eventDate, String eventAddress) {
        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Create a new event object with the provided details
        Event event = new Event(eventName, eventTime,eventDate, eventAddress, currentUser.getUid());

        // Add the event to the Firestore database
        db.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String eventID = documentReference.getId();
                        Log.d(TAG, "Event added with ID: " + documentReference.getId());
                        addToMyEvents(eventID);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding event", e);
                    }
                });
    }

    /*
    adds the eventId to created events
     */
    private void addToMyEvents(String eventId) {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case where the user is not signed in
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Finds the user's profile
        DocumentReference userProfileRef = db.collection("userProfiles").document(currentUser.getUid());

        // Adds the eventId to created events.
        userProfileRef.update("createdEvents", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Event ID added to createdEvents array");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding event ID to createdEvents array", e);
                        // Handle the failure to add the event ID to the createdEvents array
                    }
                });
    }



}