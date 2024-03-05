package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity { // AppCombatActivity provides various methods and features to manage the lifecycle of an activity
    // IntroActivity is currently set to be the Activity that runs on app runtime. This is the screen that asks the user what role do they want.
    // For the future (Not sure if needed before Project 3 Deadline?), we will use SharedPreferences to save what the user selects, so each time they open the app
    // the app can remember if they are an organizer or just an attendee.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting content view to be the activity_intro.xml file
        setContentView(R.layout.activity_intro);

        Button buttonCreateProfile = findViewById(R.id.AttendeeButton);

        // Creating OnClickListener for the AttendeeButton (What happens when we press "Event Attendee")
        buttonCreateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating an intent to change from activity_intro to FirstTimeProfileCreationActivity
                Intent intent = new Intent(IntroActivity.this, FirstTimeProfileCreationActivity.class);
                startActivity(intent);
            }
        });
    }
}

