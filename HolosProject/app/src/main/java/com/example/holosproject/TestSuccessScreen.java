package com.example.holosproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class TestSuccessScreen extends AppCompatActivity {
    private final String TAG = "TestScreen";
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_success_screen);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView textUsername = findViewById(R.id.test_username);

        // gets current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String uid = null;
        if (user != null) {
            String name = user.getDisplayName() != null ? user.getDisplayName() : "No name";
            String email = user.getEmail() != null ? user.getEmail() : "No email";
            // The user's ID, unique to the Firebase project
            DocumentReference userRef = db.collection("userProfiles").document(user.getUid());
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            textUsername.setText("Username: " + (String) document.get("name"));
                        }
                    }
                }
            });

            // test implementation of navigation
            checkUserRoleAndNavigate();
        } else {
            // Handle the case where user is null
            textUsername.setText("No user");
        }
    }

    /**
     * checks the user role and navigates 💀
     */
    private void checkUserRoleAndNavigate() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("userProfiles").document(currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            navigateBasedOnRole(role);
                        } else {
                            Log.d(TAG, "No such document");
                            // Handle case where user document doesn't exist
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        // Handle error
                    }
                }
            });
        } else {
            // navigates to login if no user
            startActivity(new Intent(this, Login.class));
            finish();
        }
    }

    /**
     * how we will present info to the user by the role
     * @param role
     * The role from the firebase userProfiles database
     */
    private void navigateBasedOnRole(String role) {
        if ("attendee".equals(role)) {
            startActivity(new Intent(this, AttendeeDashboardActivity.class));
        } else if ("admin".equals(role)) {
            startActivity(new Intent(this, AdminDashboardActivity.class));
            // LARGE NOTE HERE: Organizer and Attendee are gonna become the same role. Everything an organizer must do, an attendee
            // must be able to do as well. Because of this, we can just make them the same role.
            // in the future we can edit The code to reflect this. As of right now, if you sign up as an organizer, it will say you
            // are an attendee in the database, whatever.
        } else if ("organizer".equals(role)){
            startActivity(new Intent(this, OrganizerDashboardActivity.class));
        }
        finish(); // Close current activity
    }
}
