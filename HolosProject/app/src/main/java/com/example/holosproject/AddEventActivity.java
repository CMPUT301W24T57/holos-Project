package com.example.holosproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
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
    private static final int PICK_QR_REQUEST = 2;
    private Uri eventImageUri;

    private Uri qrCodeUri;
    private ImageView imageViewEventPoster;

    private Button uploadQR;
    private ActivityResultLauncher<String> mGetContent;
    private String customQR = null;
    private EditText eventLimit;


    /**
     * This method is called when the activity is first created. It initializes UI components,
     * sets up event listeners for various actions such as selecting time and date, uploading QR images,
     * canceling the operation, saving event details, and uploading event posters.
     * It also retrieves the current user information from Firebase Authentication.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state, if any.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        // Initialize UI components
        eventName = findViewById(R.id.edit_text_event_name);
        eventTime = findViewById(R.id.edit_text_event_time);

        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        eventTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimePickerDialog();
                }
            }
        });
        eventDate = findViewById(R.id.edit_text_event_date);

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        eventDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePickerDialog();
                }
            }
        });

        eventAddress = findViewById(R.id.edit_text_event_address);
        cancel = findViewById(R.id.button_cancel);
        save = findViewById(R.id.button_save);
        uploadQR = findViewById(R.id.button_upload_qr);
        eventLimit = findViewById(R.id.edit_text_event_limit);

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
    /**
     * Launches an intent to pick a QR image from the device's gallery.
     * The result of the selection will be handled in the method onActivityResult().
     */
    private void pickQRImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_QR_REQUEST);
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
        String eventLims = eventLimit.getText().toString();
        if (!eventLims.isEmpty()) {
            int eventLim = Integer.parseInt(eventLims);
            event.setLimit(eventLim);
        }
        if (customQR != null) {
            event.setCustomQRContents(customQR);
        }

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
                        if (customQR != null) {
                            handleCustomQR(customQR, eventID);
                            uploadCustomQR(qrCodeUri, eventID);
                        }

                        // delay to ensure event gets uploaded completely before going back

                        try {
                            Toast.makeText(AddEventActivity.this, "Please wait, uploading your new event...", Toast.LENGTH_SHORT).show();
                            Thread.sleep(1100);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
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

    /**
     * Uploads the event image to Firebase Storage and updates the event document in Firestore
     * with the image URL upon successful upload.
     *
     * @param imageUri The URI of the image to be uploaded.
     * @param eventId  The ID of the event for which the image is being uploaded.
     */
    private void uploadEventImage(Uri imageUri, String eventId) {
        StorageReference eventImageRef = FirebaseStorage.getInstance().getReference("eventImages/" + eventId + "img");
        eventImageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                FirebaseFirestore.getInstance().collection("events").document(eventId)
                        .update("imageUrl", uri.toString()); // Update event with image URL
            });
        }).addOnFailureListener(e -> {
            // Handle upload errors here
        });
    }

    /**
     * Uploads a custom QR image to Firebase Storage and updates the event document in Firestore
     * with the QR image URL upon successful upload.
     *
     * @param qrUri   The URI of the QR image to be uploaded.
     * @param eventId The ID of the event for which the QR image is being uploaded.
     */
    private void uploadCustomQR(Uri qrUri, String eventId) {
        StorageReference eventImageRef = FirebaseStorage.getInstance().getReference("eventImages/" + eventId + "qr");
        eventImageRef.putFile(qrUri).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                FirebaseFirestore.getInstance().collection("events").document(eventId)
                        .update("qrUrl", uri.toString()); // Update event with image URL
            });
        }).addOnFailureListener(e -> {
            // Handle upload errors here
        });
    }

    /**
     * This method is called when an activity launched for result returns.
     * It handles the result of selecting an image or a QR code from the device's gallery.
     * If an image is selected, it sets the URI of the selected image to be used as the event poster.
     * If a QR code is selected, it triggers the scanning of the custom QR code.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        An Intent object that carries the result data.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            eventImageUri = data.getData();
            imageViewEventPoster.setImageURI(eventImageUri); // Show the chosen image as a preview
        }
        else if (requestCode == PICK_QR_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            qrCodeUri = data.getData();
            scanCustomQR(qrCodeUri);
        }
    }
    /**
     * Scans a custom QR code image to extract its content.
     *
     * @param qrCodeUri The URI of the QR code image to be scanned.
     */
    private void scanCustomQR(Uri qrCodeUri) {
        try
        {
            InputStream inputStream = getContentResolver().openInputStream(qrCodeUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null)
            {
                Log.e("TAG", "uri is not a bitmap," + qrCodeUri.toString());
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
                //Toast.makeText(this, "The content of the QR image is: " + qrResult.getText(), Toast.LENGTH_SHORT).show();
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
            Log.e("TAG", "can not open file" + qrCodeUri.toString(), e);
        }
    }

    /**
     * Displays a DatePickerDialog to allow the user to select a date for the event.
     * The selected date will be displayed in the associated EditText field.
     */
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        eventDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);

        // Show DatePickerDialog
        datePickerDialog.show();
    }

    /**
     * Displays a TimePickerDialog to allow the user to select a time for the event.
     * The selected time will be displayed in the associated EditText field.
     */
    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time to the EditText
                        String AM_PM;
                        if(hourOfDay < 12) {
                            AM_PM = "AM";
                        } else {
                            AM_PM = "PM";
                        }

                        eventTime.setText(String.format("%02d:%02d", hourOfDay, minute) + " " + AM_PM);
                    }
                }, hour, minute, true);

        // Show TimePickerDialog
        timePickerDialog.show();
    }
}
