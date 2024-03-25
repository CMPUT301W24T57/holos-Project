package com.example.holosproject;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: AdminViewEventsActivity
 * Description: Admin can view all events on Firebase, and can delete them.
 * AdminViewEventsActivity is associated with admin_view_events.xml and admin_view_events_item_event.xml
 **/
public class AdminViewEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminViewEventsAdapter adapter;
    private List<Event> eventList;

    // Static variable to control test mode
    public static boolean isTestMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_events);

        // Back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Finish this activity and go back to the previous one in the stack
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventList = new ArrayList<>();

        if (isTestMode) {
            eventList = MockDataProvider.getMockEvents();
        } else {
            fetchEvents();
        }

        adapter = new AdminViewEventsAdapter(this, eventList);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Fetches events from the Firestore database.
     */
    private void fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        eventList.clear(); // Clear the list before adding new items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Manual deserialization
                            String name = document.getString("name");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String address = document.getString("address");
                            String creator = document.getString("creator");
                            String eventId = document.getId();
                            ArrayList<String> attendees = (ArrayList<String>) document.get("attendees");

                            Event event = new Event(name, date, time, address, creator);
                            event.setEventId(eventId);
                            event.setAttendees(attendees); // Assuming you have a setter for attendees
                            eventList.add(event);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                        // Handle the error properly
                    }
                });
    }

    // Method used to enable test mode. Used within the app tests.
    public static void enableTestMode() {
        isTestMode = true;
    }

    // Method used to disable test mode. Used within the app tests.
    public static void disableTestMode() {
        isTestMode = false;
    }

}
