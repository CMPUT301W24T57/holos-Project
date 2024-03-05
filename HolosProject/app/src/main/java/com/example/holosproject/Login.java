package com.example.holosproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private EditText username;
    private EditText password;
    private FirebaseAuth mAuth;
    private Button signinButton;
    private static final String TAG = "LoginActivity";
    private String emailpad = "@holos.project";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.login_activity_username);
        password = findViewById(R.id.login_activity_password);
        mAuth = FirebaseAuth.getInstance();
        signinButton = findViewById(R.id.signin_activity_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(username.getText().toString()+emailpad, password.getText().toString());
            }
        });
    }

    /**
     * Signs the user into firebase.
     * @param email
     * The username, in order to use it for this project we pad with @holos.project because firebase does not allow usernames
     * @param password
     * The users password
     */
    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    /**
     * This it what controls what happens on login success or failure.
     * @param user
     * the FireBase user object.
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            //
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
        } else {
            // Sign in has failed
            Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
        }
    }
}