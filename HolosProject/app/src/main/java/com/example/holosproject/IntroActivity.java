package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.widget.Toast;

/**
 * FileName: IntroActivity
 * Description: This is the activity that is shown the very first time a user runs the app. Once they have selected their role, this will not be the opening page.
 *  This activity simply prompts the user to select if they are attendee, organizer or administrator.

 *  Associated with the activity_intro.xml layout.
 **/

public class IntroActivity extends AppCompatActivity { // AppCombatActivity provides various methods and features to manage the lifecycle of an activity
    // IntroActivity is currently set to be the Activity that runs on app runtime. This is the screen that asks the user what role do they want.
    // For the future (Not sure if needed before Project 3 Deadline?), we will use SharedPreferences to save what the user selects, so each time they open the app
    // the app can remember if they are an organizer or just an attendee.
    private Button attendeeButton;

    private FirebaseAuth mAuth;
    private Button organzierButton;
    private Button administratorButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        attendeeButton = findViewById(R.id.AttendeeButton);
        organzierButton = findViewById(R.id.OrganizerButton);
        administratorButton = findViewById(R.id.AdministratorButton);
//        loginButton = findViewById(R.id.login_button);
        mAuth = FirebaseAuth.getInstance();


        attendeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, FirstTimeProfileCreationActivity.class);
                startActivity(intent);
            }
        });
        organzierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, OrganizerCreation.class);
                startActivity(intent);
            }
        });
        administratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, FirstTimeProfileCreationActivity.class);
                startActivity(intent);
            }
        });
/*        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IntroActivity.this, Login.class);
                startActivity(intent);
            }
        });*/
    }

}

