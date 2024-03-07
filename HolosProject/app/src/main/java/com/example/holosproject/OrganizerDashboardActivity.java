package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class OrganizerDashboardActivity extends AppCompatActivity
                             implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView eventsRecyclerView;
    private FloatingActionButton fabAddEvent;
    private OrganizerDashboardEventsAdapter eventsAdapter;

    private List<Event> eventsList; // model class
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private final String TAG = "OrganizerDashboardActivity";

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // If the user selects one of the items in the drawer, what happens? (navigate to that respective activity)
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_view_all_events) {
            Intent intent = new Intent(this, ViewAllEventsActivity.class);
            startActivity(intent);
            finish();

        }
        else if (id == R.id.nav_view_registered_events) {   // If we want to navigate to the view we are already in, just close the drawer
            Intent intent = new Intent(this, AttendeeDashboardActivity.class);
            startActivity(intent);
            finish();
        }

        else if (id == R.id.nav_view_organizer_dashboard) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_dashboard);

        // Setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        // Setting up the recyclerview
        eventsRecyclerView = findViewById(R.id.recycler_view_events); // Assuming this is the ID in your XML
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsList = new ArrayList<>();
        // TODO: Populate the ArrayList with the users created events
        eventsAdapter = new OrganizerDashboardEventsAdapter(eventsList);
        eventsRecyclerView.setAdapter(eventsAdapter);

        // Setup the drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Setup NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this); // This line sets up the listener

        fabAddEvent = findViewById(R.id.fab_add_event); // Replace with the actual ID from your XML
        fabAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
        });


//        eventsList.add(new Event("test","test","test","test","test"));
//        Log.d(TAG, "Test events have been added.");
        //fetchUserEvents();
    }
    /*
    private void populateTestEvents() {
        eventsList.clear(); // Clear the list before adding test events

        // Add test events
        eventsList.add(new Event("Event 1", "2024-03-08", "10:00 AM", "Address 1", "Creator 1"));
        eventsList.add(new Event("Event 2", "2024-03-09", "11:00 AM", "Address 2", "Creator 2"));
        eventsList.add(new Event("Event 3", "2024-03-10", "12:00 PM", "Address 3", "Creator 3"));

        // Notify the adapter that the dataset has changed
        eventsAdapter.notifyDataSetChanged();
    }
    private void fetchUserEvents() {

        if (currentUser != null) {

            String userId = currentUser.getUid();
            db.collection("userProfiles").document(userId).get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "document exists");
                                List<String> myEvents = (List<String>) document.get("createdEvents");
                                if (myEvents != null && !myEvents.isEmpty()) {
                                    Log.d(TAG, "MyEvents have been found");
                                    fetchEventsFromCollection(myEvents);
                                }
                            }
                        } else {
                            // Handle failure
                        }
                    });
        }
    }
    private void fetchEventsFromCollection(List<String> myEvents) {
        for (String eventId : myEvents) {
            Log.d(TAG, "Your events");
            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Event event = documentSnapshot.toObject(Event.class);
                            eventsList.add(event);
                            eventsAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Document does not exist");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error getting document", e);
                    });
        }
    }
*/
}