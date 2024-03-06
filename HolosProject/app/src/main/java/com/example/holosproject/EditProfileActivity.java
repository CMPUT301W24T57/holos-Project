package com.example.holosproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * FileName: EditProfileActivity
 * Description: Contains the logic for Editing a users profile.
 * Brought to this screen when a user selects the "Edit Profile" option from the drawer menu.

 * XML Files associated with this are: activity_edit_profile.xml
 **/

public class EditProfileActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize fields with the user's existing data
        // ...

        // Set up listeners and handlers for saving the edited profile
        // ...

        FloatingActionButton fabBack = findViewById(R.id.buttonEditProfileBack);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the activity to go back to the previous screen
                finish();
            }
        });
    }

    // TODO: Populate the fields (Text, Switches, Image) with data from Firebase

    // TODO: If all fields are filled, and user selects finish editing, update that document in firebase with the new information. Then decide where to navigate the user
}
