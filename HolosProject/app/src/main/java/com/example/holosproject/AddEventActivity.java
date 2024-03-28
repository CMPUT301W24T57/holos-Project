package com.example.holosproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    private Button uploadQR;
    private ActivityResultLauncher<String> mGetContent;
    private String customQR = null;

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
        uploadQR = findViewById(R.id.button_upload_qr);

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        Uri imageUri = result;
                        // taken from https://stackoverflow.com/questions/29649673/scan-barcode-from-an-image-in-gallery-android
                        try
                        {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            if (bitmap == null)
                            {
                                Log.e("TAG", "uri is not a bitmap," + imageUri.toString());
                                return;
                            }
                            int width = bitmap.getWidth(), height = bitmap.getHeight();
                            int[] pixels = new int[width * height];
                            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                            bitmap.recycle();
                            bitmap = null;
                            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                            MultiFormatReader reader = new MultiFormatReader();
                            try
                            {
                                Result qrResult = reader.decode(bBitmap);
                                Toast.makeText(this, "The content of the QR image is: " + qrResult.getText(), Toast.LENGTH_SHORT).show();
                                // Handle the custom QR code...
                                customQR = qrResult.getText();
                            }
                            catch (NotFoundException e)
                            {
                                Log.e("TAG", "decode exception", e);
                                Toast.makeText(this, "You have uploaded an invalid QR code, try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (FileNotFoundException e)
                        {
                            Log.e("TAG", "can not open file" + imageUri.toString(), e);
                        }
                    }
                });

        // Set listener for uploading a QR image
        uploadQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickQRImage();
            }
        });
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

    private void pickQRImage() {
        mGetContent.launch("image/*");
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
                        // add an extra check in ID if the creator added a custom QR code
                        if (customQR != null) {
                            handleCustomQR(customQR, eventID);
                        }
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
     * Handles adding a custom, user-chosen QR code and links it to an event.
     * @param customQR: the data from a custom user-uploaded QR code
     * @param eventID: the event that the custom QR code needs to be linked to
     */
    private void handleCustomQR(String customQR, String eventID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();
        data.put("linkedEvent", eventID);
        db.collection("Custom QR Data")
                .document(Integer.toString(Objects.hashCode(customQR)))
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Document created successfully.");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
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
