package com.example.holosproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDisplay extends AppCompatActivity {

    // we are passing the same string back and forth and doing the same thing multiple times
    // once we add an array of events to users this will be more efficient
    private void backToDashboard(String eventTitle) {
        Intent intent = new Intent(this, AttendeeDashboardActivity.class);
        intent.putExtra("title",eventTitle);
        startActivity(intent);
    }
    private FirebaseFirestore database = FirebaseFirestore.getInstance();

    private CollectionReference eventRef = database.collection("eventTestNW");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String eventTitle = bundle.getString("contents");
            TextView titleDisplay = findViewById(R.id.event_Name);
            titleDisplay.setText(eventTitle);
            Button rsvpButton = findViewById(R.id.rsvpButton);
            rsvpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backToDashboard(eventTitle);
                }
            });
        }
    }
}