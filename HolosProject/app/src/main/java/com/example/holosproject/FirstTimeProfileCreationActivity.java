package com.example.holosproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * FileName: FirstTimeProfileCreationActivity
 * Description: This is the activity that is shown after a user selects their role, prompting them to create a profile by entering their information.
 *  If the user fills in all the fields, we will try to add the data to firebase. If it gets added, we move to the Attendee Dashboard. Otherwise stay on this page.

 *  Associated with the activity_first_time_profile_creation.xml layout.
 **/

public class FirstTimeProfileCreationActivity extends AppCompatActivity {
    // This activity is shown during the first runtime of the app. If a user initially selects "Attendee", then this
    // activity will appear, prompting them to create a profile.

    // UI Elements
    private ImageView imageViewProfile;
    private EditText editTextName;
    private EditText editTextPassword;
    private EditText editTextUsername;
    private EditText editTextContact;
    private EditText editTextHomepage;
    private Switch switchNotifications;
    private Switch switchGeolocation;
    private Button buttonFinishProfileCreation;
    private static final String TAG = "EmailPasswordActivity";

    // Firestore database reference
    private FirebaseFirestore database;
    private CollectionReference userProfileInfoRef;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_profile_creation);
        mAuth = FirebaseAuth.getInstance();
        imageViewProfile = findViewById(R.id.imageViewProfile);
        editTextName = findViewById(R.id.editTextName);
        editTextContact = findViewById(R.id.editTextContact);
        editTextHomepage = findViewById(R.id.editTextHomepage);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchGeolocation = findViewById(R.id.switchGeolocation);
        buttonFinishProfileCreation = findViewById(R.id.buttonFinishProfileCreation);

        // TODO: image support


        buttonFinishProfileCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = editTextName.getText().toString().trim();
                final String contact = editTextContact.getText().toString().trim();
                final String homepage = editTextHomepage.getText().toString().trim();
                createAccount(name, contact, homepage);
            }
        });
    }

    /**
     * Creating an account without email or password.
     * @param name
     * The users name.
     * @param contact
     * The users password.
     * @param homepage
     * The users homepage.
     */
    private void createAccount(String name, String contact, String homepage) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(FirstTimeProfileCreationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // The user information that will be stored
                            Map<String, Object> userProfile = new HashMap<>();
                            userProfile.put("role", "attendee");
                            userProfile.put("name", name);
                            userProfile.put("contact", contact);
                            userProfile.put("homepage", homepage);
                            // TODO: Add the image name or image reference to userProfile map

                            // Store the user profile in Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("userProfiles").document(user.getUid())
                                    .set(userProfile)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");
                                            updateUI(user);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                            updateUI(null);
                                        }
                                    });
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(FirstTimeProfileCreationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // If the user is signed in go to the next activity
            Intent intent = new Intent(FirstTimeProfileCreationActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // prevents them from being able to use the back button
        } else {
            // authentication failed
            Toast.makeText(FirstTimeProfileCreationActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}
