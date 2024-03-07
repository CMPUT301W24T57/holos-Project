package com.example.holosproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * FileName: EditProfileActivity
 * Description: Contains the logic for Editing a users profile.
 * Brought to this screen when a user selects the "Edit Profile" option from the drawer menu.

 * XML Files associated with this are: activity_edit_profile.xml
 **/

public class EditProfileActivity  extends AppCompatActivity {

    private final String TAG = "EditProfileActivity";
    private EditText editTextName, editTextHomepage, editTextContact;

    // TODO: Populate the ImageView with users Profile Image

    // TODO: Populate Notification and Geolocation switch with corresponding setting (This currently isn't stored within Firebase)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Getting the current user from Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize your EditTexts and other UI components
        editTextName = findViewById(R.id.editTextName);
        editTextHomepage = findViewById(R.id.editTextHomepage);
        editTextContact = findViewById(R.id.editTextContact);

        if (currentUser != null) {
            String uid = currentUser.getUid();
            fetchUserProfile(uid);
        } else {
            // Handle case where there is no logged-in user
            Log.d(TAG, "No user logged in");
        }

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

    // Fetches User Profile, and populates our fields with the data
    private void fetchUserProfile(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userProfileRef = db.collection("userProfiles").document(uid);

        userProfileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Populate the UI with the user's profile data
                        editTextName.setText(document.getString("name"));
                        editTextContact.setText(document.getString("contact"));
                        editTextHomepage.setText(document.getString("homepage"));
                        // Populate other fields similarly
                    } else {
                        Log.d(TAG, "No such document");
                        // Handle case where the document doesn't exist
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    // Handle the failure
                }
            }
        });

        // TODO: If all fields are filled, and user selects finish editing, update that document in firebase with the new information. Then decide where to navigate the user
    }
}
