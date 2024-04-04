package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

// Not in use anymore
public class Login extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;
    private FirebaseAuth mAuth;
    private Button signinButton;
    private static final String TAG = "LoginActivity";
    private String emailpad = "@holos.project";

    // I see zero security concerns here at all :-) lgtm
    private static final String ADMIN_USERNAME = "administrator";
    private static final String ADMIN_PASSWORD = "administrator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEditText = findViewById(R.id.login_activity_username);
        passwordEditText = findViewById(R.id.login_activity_password);
        mAuth = FirebaseAuth.getInstance();
        signinButton = findViewById(R.id.signin_activity_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        Button loginGoBackButton = findViewById(R.id.loginGoBackButton);
        loginGoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Call this method when the login button is pressed
    public void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (checkAdminCredentials(username, password)) {
            updateRoleToAdmin();
        } else {
            // Show error or make a toast that credentials are invalid
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    // Possibly the most secure login method ever created.
    private boolean checkAdminCredentials(String username, String password) {
        // Check if the credentials match the hardcoded admin credentials
        return ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password);
    }

    private void updateRoleToAdmin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userProfileRef = db.collection("userProfiles").document(currentUser.getUid());

            userProfileRef.update("role", "admin")
                    .addOnSuccessListener(aVoid -> {
                        // Navigate to admin dashboard or show success message
                        Toast.makeText(this, "Logged in as admin", Toast.LENGTH_SHORT).show();
                        navigateToAdminDashboard();
                    })
                    .addOnFailureListener(e -> {
                        // Log error or show failure message
                        Toast.makeText(this, "Failed to update role to admin", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle the case where there is no signed-in user
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToAdminDashboard() {
        // navigation to the admin dashboard after we logged in as an admin
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToastMessage(String message) {
        Toast.makeText(Login.this, message, Toast.LENGTH_SHORT).show();
    }


//    /**
//     * Signs the user into firebase.
//     * @param email
//     * The username, in order to use it for this project we pad with @holos.project because firebase does not allow usernames
//     * @param password
//     * The users password
//     */
//    private void signIn(String email, String password) {
//        // [START sign_in_with_email]
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            Toast.makeText(Login.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//                    }
//                });
//        // [END sign_in_with_email]
//    }
//
//    /**
//     * This it what controls what happens on login success or failure.
//     * @param user
//     * the FireBase user object.
//     */
//    private void updateUI(FirebaseUser user) {
//        if (user != null) {
//            //
//            Intent intent = new Intent(Login.this, TestSuccessScreen.class);
//            startActivity(intent);
//            finish(); // Close the current activity
//        } else {
//            // Sign in has failed
//            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//        }
//    }
}