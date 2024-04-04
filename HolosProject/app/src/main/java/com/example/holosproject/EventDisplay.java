package com.example.holosproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.squareup.picasso.Picasso;

public class EventDisplay extends AppCompatActivity {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = database.collection("events");

    /**
     * Retrieves and displays the details of the event specified by the eventID.
     *
     * @param eventID The ID of the event to be displayed.
     */
    private void handleEvent(String eventID) {
        DocumentReference docRef = eventsRef.document(eventID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String address = document.getString("address");
                        String creator = document.getString("creator");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String eventId = document.getId();
                        String posterUrl = document.getString("imageUrl");

                        TextView dateDisplay = findViewById(R.id.event_Date);
                        TextView creatorDisplay = findViewById(R.id.event_Creator);
                        TextView eventDisplay = findViewById(R.id.eventTitle);
                        ImageView posterDisplay = findViewById(R.id.eventPoster);
                        ImageView avatarDisplay = findViewById(R.id.event_creatorAvatar);
                        Picasso.get().load(posterUrl).into(posterDisplay);

                        dateDisplay.setText(getString(R.string.prefixDate, date, time));
                        eventDisplay.setText(name);
                        db.collection("userProfiles").document(creator).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String creatorName = documentSnapshot.getString("name");
                                        String creatorAvatar = documentSnapshot.getString("profileImageUrl");
                                        if (name != null) {
                                            creatorDisplay.setText(getString(R.string.prefixOrganizer, creatorName));
                                        }
                                        if (creatorAvatar != null) {
                                            Picasso.get().load(creatorAvatar).into(avatarDisplay);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    /**
     * Navigates back to the dashboard activity.
     *
     * @param eventID The ID of the event.
     */
    private void backToDashboard(String eventID) {
        Intent intent = new Intent(this, AttendeeDashboardActivity.class);
        intent.putExtra("title", eventID);
        startActivity(intent);
    }

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
            String eventID = bundle.getString("contents");
            handleEvent(eventID);
            Button rsvpButton = findViewById(R.id.rsvpButton);
            rsvpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backToDashboard(eventID);
                }
            });
        }
    }
}