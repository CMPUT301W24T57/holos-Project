package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class AddEventActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private EditText eventLocationEditText;
    private EditText eventDescriptionEditText;
    private Button cancelButton;
    private Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventNameEditText = findViewById(R.id.edit_text_event_name);
        eventDateEditText = findViewById(R.id.edit_text_event_date);
        eventTimeEditText = findViewById(R.id.edit_text_event_time);
        eventLocationEditText = findViewById(R.id.edit_text_event_address);
        eventDescriptionEditText = findViewById(R.id.edit_text_event_description);
        cancelButton = findViewById(R.id.button_cancel);
        saveButton = findViewById(R.id.button_save);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameEditText.getText().toString();
                String eventDate = eventDateEditText.getText().toString();
                String eventTime = eventTimeEditText.getText().toString();
                String eventLocation = eventLocationEditText.getText().toString();
                String eventDescription = eventDescriptionEditText.getText().toString();

                if (!eventName.isEmpty() && !eventDate.isEmpty() && !eventTime.isEmpty() && !eventLocation.isEmpty() && !eventDescription.isEmpty()) {
                    Event newEvent = new Event(eventName, eventDate, eventTime, eventLocation, eventDescription);

                    finish();


                } else {
                    // Show error message if any field is empty
                    Toast.makeText(AddEventActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TODO: Add button functionality to the "Upload QR and Postier Image
        
    }


}

