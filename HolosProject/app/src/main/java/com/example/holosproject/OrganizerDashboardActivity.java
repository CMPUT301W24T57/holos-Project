package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;

/**
 * OrganizerDashboardActivity: Represents the dashboard for organizers.
 * Displays a list of events created by the organizer, allows adding new events,
 * and provides navigation options via a navigation drawer.
 */
public class OrganizerDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView eventsRecyclerView;
    private FloatingActionButton fabAddEvent;
    private OrganizerDashboardEventsAdapter eventsAdapter;

    private List<Event> eventsList; // List to hold event objects
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private final String TAG = "OrganizerDashboardActivity";


    /**
     * Initializes activity components and UI elements.
     * @param savedInstanceState The saved instance state.
     */
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

        CheckDisplayAdminDashboard(currentUser.getUid());

        // Setup the recyclerview
        eventsRecyclerView = findViewById(R.id.recycler_view_events);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsList = new ArrayList<>();
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
        navigationView.setNavigationItemSelectedListener(this);

        fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
        });

        // Fetch events created by the user
        fetchUserEvents();
    }

    /**
     * Fetches events created by the current user from Firestore.
     */
    private void fetchUserEvents() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("userProfiles").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                List<String> myEvents = (List<String>) document.get("createdEvents");
                                if (myEvents != null && !myEvents.isEmpty()) {
                                    fetchEventsFromCollection(myEvents);
                                }
                            }
                        }
                    });
        }
    }

    /**
     * Fetches details of events from Firestore based on event IDs.
     * @param myEvents List of event IDs.
     */
    private void fetchEventsFromCollection(List<String> myEvents) {
        for (String eventId : myEvents) {
            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String date = documentSnapshot.getString("date");
                            String time = documentSnapshot.getString("time");
                            String address = documentSnapshot.getString("address");
                            String creator = documentSnapshot.getString("creator");
                            ArrayList<String> attendees = (ArrayList<String>) documentSnapshot.get("attendees");

                            Event event = new Event(name, date, time, address, creator);
                            event.setEventId(eventId);
                            event.setAttendees(attendees);
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
    // TODO: Fix the bug where events you create don't display until you leave the screen and come back

    /**
     * Determines if the drawer should display "Admin Dashboard" based on if the users role is "admin".
     * @param userId: the users ID
     */
    // Grabs users role. Used to determine if user can access the admin dashboard or not.
    private void CheckDisplayAdminDashboard(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userProfiles").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if ("admin".equals(role)) {
                    // Show the admin dashboard option in the navigation drawer.
                    Menu menu = navigationView.getMenu();
                    MenuItem adminDashboardMenuItem = menu.findItem(R.id.nav_admin_dashboard);
                    adminDashboardMenuItem.setVisible(true);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error, e.g., show a message to the user.
        });
    }
    /**
     * Handles navigation item selection from the navigation drawer.
     * Opens corresponding activities based on the selected item.
     * @param item The selected item from the navigation drawer.
     * @return true if the event was handled successfully, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_view_all_events) {
            Intent intent = new Intent(this, ViewAllEventsActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_view_registered_events) {
            Intent intent = new Intent(this, AttendeeDashboardActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_view_organizer_dashboard) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_admin_dashboard) {
            Intent intent = new Intent(this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
