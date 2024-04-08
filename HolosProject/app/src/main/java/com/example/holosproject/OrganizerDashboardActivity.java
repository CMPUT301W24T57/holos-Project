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

        // Setup NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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


        // Update the navigation drawer header with user info
        NavigationDrawerUtils.updateNavigationHeader(navigationView);

        fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            startActivity(intent);
            recreate();
        });
    }

    /**
     * After returning from a fragment, we fetch user events again
     */
    protected void onResume() {
        super.onResume();
        // Clear the events list before fetching to avoid duplicates
        eventsList.clear();
        // Fetch the events again when coming back to this activity
        // we wait half a second so images load lol
       fetchUserEvents();

        NavigationView navigationView = findViewById(R.id.nav_view);
        NavigationDrawerUtils.updateNavigationHeader(navigationView);
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
                                else {
                                    // Important to clear the list and update adapter if there are no events
                                    eventsList.clear();
                                    eventsAdapter.notifyDataSetChanged();
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
        // Clear the events list before adding new events to avoid duplicates
        eventsList.clear();

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





                            int limit = Integer.MAX_VALUE;
                            Long limitLong = documentSnapshot.getLong("limit");
                            if (limitLong != null) {
                                limit = limitLong.intValue();
                            }
                            String imageUrl = documentSnapshot.getString("imageUrl"); // Get the image URL from the document
                            System.out.println("Event name: " + name + "URL: " + imageUrl);
                            Event event = new Event(name, date, time, address, creator);
                            event.setImageUrl(imageUrl);
                            event.setEventId(eventId);
                            event.setAttendees(attendees);
                            event.setLimit(limit);
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

    /**
     * Finds user role, to determine what displays in the drawer menu
     * @param userId: the users ID
     */
    private void CheckDisplayAdminDashboard(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Menu menu = navigationView.getMenu();
        MenuItem adminLoginMenuItem = menu.findItem(R.id.admin_login);

        db.collection("userProfiles").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if ("admin".equals(role)) {
                    // Show the admin dashboard option in the navigation drawer.
                    MenuItem adminDashboardMenuItem = menu.findItem(R.id.nav_admin_dashboard);
                    adminDashboardMenuItem.setVisible(true);

                    // hide the login option
                    adminLoginMenuItem.setVisible(false);
                } else {
                    // if role is not admin, then show the login option
                    adminLoginMenuItem.setVisible(true);
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
        else if (id == R.id.admin_login) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
