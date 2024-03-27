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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Activity for adding new events.
 */
public class AddEventActivity extends AppCompatActivity {
    private EditText eventName;
    private EditText eventDate;
    private EditText eventTime;
    private EditText eventAddress;
    private final String TAG = "addEventActivityScreen";
    private FirebaseUser currentUser;
    private Button save, cancel, buttonUploadImage;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri eventImageUri;
    private ImageView imageViewEventPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize UI components
        eventName = findViewById(R.id.edit_text_event_name);
        eventTime = findViewById(R.id.edit_text_event_time);
        eventDate = findViewById(R.id.edit_text_event_date);
        eventAddress = findViewById(R.id.edit_text_event_address);
        cancel = findViewById(R.id.button_cancel);
        save = findViewById(R.id.button_save);
        imageViewEventPoster = findViewById(R.id.imageViewEventPoster);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);


        // Set click listener for the cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set click listener for the save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call method to save event details
                saveEvent(eventName.getText().toString(), eventDate.getText().toString(), eventTime.getText().toString(), eventAddress.getText().toString());
            }
        });

        // Set click listener for the Upload Event Poster button
        buttonUploadImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    /**
     * Saves the event details to Firestore.
     *
     * @param eventName   The name of the event.
     * @param eventTime   The time of the event.
     * @param eventDate   The date of the event.
     * @param eventAddress The address of the event.
     */
    private void saveEvent(String eventName, String eventTime, String eventDate, String eventAddress) {
        // Get a reference to the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Create a new event object with the provided details
        Event event = new Event(eventName, eventTime, eventDate, eventAddress, currentUser.getUid());

        // Add the event to the Firestore database
        db.collection("events")
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Log success message and finish activity
                        String eventID = documentReference.getId();
                        Log.d(TAG, "Event added with ID: " + eventID);
                        addToMyEvents(eventID);

                        // Now upload the image, if one was chosen
                        if (eventImageUri != null) {
                            uploadEventImage(eventImageUri, eventID);
                        }

                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log failure message
                        Log.w(TAG, "Error adding event", e);
                    }
                });
    }

    /**
     * Adds the event ID to the user's created events.
     *
     * @param eventId The ID of the event to be added.
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
                        // Log success message
                        Log.d(TAG, "Event ID added to createdEvents array");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Log failure message
                        Log.w(TAG, "Error adding event ID to createdEvents array", e);
                        // Handle the failure to add the event ID to the createdEvents array
                    }
                });
    }

    // Stores the users uploaded event image on Firebase
    private void uploadEventImage(Uri imageUri, String eventId) {
        StorageReference eventImageRef = FirebaseStorage.getInstance().getReference("eventImages/" + eventId);
        eventImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                FirebaseFirestore.getInstance().collection("events").document(eventId)
                        .update("imageUrl", uri.toString()); // Update event with image URL
            });
        }).addOnFailureListener(e -> {
            // Handle upload errors here
        });
    }

    // The result of getting the image from the image upload, we save it and set it as the new preview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            eventImageUri = data.getData();
            imageViewEventPoster.setImageURI(eventImageUri); // Show the chosen image as a preview
        }
    }
}
