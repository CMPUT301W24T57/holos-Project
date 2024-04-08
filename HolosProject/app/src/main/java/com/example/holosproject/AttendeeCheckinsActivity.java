package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity to display attendee check-ins for a specific event.
 */
public class AttendeeCheckinsActivity extends AppCompatActivity {
    private ListView AttendeeCheckins;
    private AttendeeCheckinsAdapter adapter;
    private List<AttendeeCheckin> attendeeCheckins;
    private TextView checkInCount;
    private Integer totalCheckins = 0;

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state, if any.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_checkin_act);

        // Get the event ID from the intent
        String eventId = getIntent().getStringExtra("checkins");

        // Initialize views
        AttendeeCheckins = findViewById(R.id.AttendeeCheckins);
        checkInCount = findViewById(R.id.checkInsNum);
        attendeeCheckins = new ArrayList<>();

        // Initialize the adapter and set it to the ListView
        adapter = new AttendeeCheckinsAdapter(this, attendeeCheckins);
        AttendeeCheckins.setAdapter(adapter);

        // Back button
        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(v -> finish());

        // Navigation button
        Button navButton = findViewById(R.id.attendeeNav);
        navButton.setOnClickListener(v -> goToAttendeeView(eventId));

        // Fetch attendee check-ins from Firebase
        fetchAttendeeCheckins(eventId);
    }

    /**
     * Fetches attendee check-ins for the specified event from Firebase Firestore.
     *
     * @param eventId The ID of the event.
     */
    private void fetchAttendeeCheckins(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        HashMap<String, String> checkIns = (HashMap<String, String>) documentSnapshot.get("checkIns");
                        if (checkIns != null) {
                            Map<String, AttendeeCheckin> checkinMap = new HashMap<>();
                            totalCheckins = checkIns.size();
                            for (Map.Entry<String, String> entry : checkIns.entrySet()) {
                                String attendeeID = entry.getKey();
                                String checkInCount = entry.getValue();
                                AttendeeCheckin attendeeCheckin = new AttendeeCheckin(attendeeID, Integer.valueOf(checkInCount));
                                checkinMap.put(attendeeID, attendeeCheckin);
                            }
                            attendeeCheckins.clear();
                            attendeeCheckins.addAll(checkinMap.values());
                            adapter.notifyDataSetChanged();
                            checkInCount.setText("Total Checkins: " + totalCheckins);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("AttendeeCheckinsActivity", "Could not get check-ins", e);
                });
    }

    /**
     * Navigates to the attendee list activity.
     *
     * @param eventID The ID of the event.
     */
    private void goToAttendeeView(String eventID) {
        Intent intent = new Intent(AttendeeCheckinsActivity.this, AttendeeListActivity.class);
        intent.putExtra("EVENT_ID", eventID); // Pass the event ID to the attendee list activity
        startActivity(intent);
    }
}

