package com.example.holosproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * FileName: AttendeeDashboardActivity
 * Description: This is the logic for the AttendeeDashboard activity. It contains the logic for displaying the RecyclerView, which is the current implemented view for the
 * list of events. This is where the click listeners for the different sections of this screen will go. (The QR Code, Each Event, the drawer pop out menu, etc.)
 * This dashboard shows all of the events the user is currently enrolled in. There is a different activity for all open events.
 * This file also contains first implementation of the drawer menu. This shit was really hard to set up to be honest, lots of different parts.
 * The XML files associated with the drawer are: drawer_menu.xml, drawer_menu_header.xml, and activity_attendee_dashboard.xml.
 * AttendeeDashboardActivity is associated with the item_attendee_dashboard.xml layout, and the activity_attendee_dashboard.xml layout.
 *
 * @noinspection ALL
 */

public class TestAttendeeDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "TestScreen";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    // Using a RecyclerView to display all of the Events our user is currently enrolled in
    private RecyclerView eventsRecyclerView;
    private AttendeeDashboardEventsAdapter eventsAdapter;
    private List<Event> eventList = new ArrayList<>(); // This is the data source

    // References to The drawer menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    // References for attendee QR scan:
    private FloatingActionButton scanButton;
    private boolean geolocation;
    private ListenerRegistration eventsListener;
    private FusedLocationProviderClient fusedLocationClient;
    private String locationID;

    private static boolean testMode = false;

    private static boolean testPassed = false;

    private List<Event> mockEvents = null;

    private Event testEvent;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_dashboard);

        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Update the navigation drawer header with user info
        NavigationDrawerUtils.updateNavigationHeader(navigationView);

        // Toolbar is the section at the top of screen.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        System.out.println(testMode);
        // calling fetchUserProfile to see if the user is an admin, if they are, allow them to access the Admin Dashboard through the Drawer.
        fetchTestProfile();

        // Setting up the RecyclerView
        // Most of the code for this is found within the AttendeeDashboardEventsActivity file
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new AttendeeDashboardEventsAdapter(eventList);
        eventsRecyclerView.setAdapter(eventsAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        scanButton = findViewById(R.id.fabQRCode);
        scanButton.setOnClickListener(v -> {
                testScanQRCode();
        });

        // if we got here from the check-in display screen,
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String eventID = bundle.getString("title");
                mockEvents = MockDataProvider.getMockEvents();
                for (Event event : mockEvents) {
                    if (event.getEventId() == eventID) {
                        testRSVPEvent(eventID, event);
                    }
                }
        }

    }


    /**
     * Grabs user profile, to determine what they view in the Drawer Menu
     * @param userId: the users ID
     */
    private void fetchUserProfile(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
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
                }
                else {
                    // if role is not admin, then show the login option
                    adminLoginMenuItem.setVisible(true);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error, e.g., show a message to the user.
        });
    }

    private void fetchTestProfile() {
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        Menu menu = navigationView.getMenu();
        MenuItem adminLoginMenuItem = menu.findItem(R.id.admin_login);
        adminLoginMenuItem.setVisible(true);
    }

    // OnNavigationItemSelected: When a user selects an item from the nav drawer menu, what should happen?
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // If the user selects one of the items in the drawer, what happens? (navigate to that respective activity)
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_view_all_events) {
            Intent intent = new Intent(this, ViewAllEventsActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_view_registered_events) {   // If we want to navigate to the view we are already in, just close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_view_organizer_dashboard) {
            Intent intent = new Intent(this, OrganizerDashboardActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_admin_dashboard) {
            // start the admin dashboard activity
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


    private void testScanQRCode() {
        List<Event> mockEvents = MockDataProvider.getMockEvents();
        String mockID = mockEvents.get(0).getEventId();
        handleTestCode(mockID);

    }

    /**
     * Sends user to a (barebones) event details display screen where they can check in.
     *
     * @param scanContents: the contents of a QR code scan, should be the ID of a valid event
     */

    private void goToEventDisplay(String scanContents) {
        Intent intent = new Intent(this, TestEventDisplay.class);
        intent.putExtra("contents", scanContents);
        startActivity(intent);
    }

    /**
     * Sends user to the event details on the all events screen (to differ from check in)
     *
     * @param scanContents: the contents of a QR code scan, should be the ID of a valid event
     */

    private void goToPromoDisplay(String scanContents) {
        Intent intent = new Intent(this, ViewAllEventsActivity.class);
        intent.putExtra("promo", scanContents);
        startActivity(intent);
    }

    /**
     * Adds an event ID to a user's list of events they are going to attend.
     *
     * @param userId  the current user's ID
     * @param eventId the ID of the event to be added
     */

    private void addUserEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("userProfiles").document(userId);

        userRef.update("myEvents", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's list"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding event to user's list", e));
    }

    /**
     * Adds an event to a user if necessary and adds the user to the event's checkins.
     *
     * @param eventID  The ID of the event to be added.
     * @param document A document representing the event.
     */

    private void testRSVPEvent(String eventID, Event event) {
        HashMap<String, String> checkIns = (HashMap<String, String>) event.getCheckIns();
        ArrayList<String> attendees = (ArrayList<String>) event.getAttendees();
        ArrayList<GeoPoint> locations = (ArrayList<GeoPoint>) event.getLocations();

        UserProfile mockUser = MockDataProvider.getMockUser();

        if (!checkIns.containsKey(mockUser.getUid())) {
            addUserEvent(mockUser.getUid(), eventID);
            checkIns.put(mockUser.getUid(), "1");
            event.setCheckIns(checkIns);

            // Check and update user location, respecting the geolocation preference.
            //checkAndUpdateUserLocation(eventID);
        }
        if (!attendees.contains(mockUser.getUid())) {
            attendees.add(mockUser.getUid());
            event.setAttendees(attendees);
        }

        setTestEvent(event);

    }

    private void handleTestCode(String eventID) {
        if (eventID.contains("promo")) {
            String strippedContents = eventID.replace("promo", "");
            goToPromoDisplay(eventID);
        }
        else {
            goToEventDisplay(eventID);
        }
    }

    protected static void enableTestMode() {
        testMode = true;
    }

    protected static void disableTestMode() {
        testMode = false;
    }

    protected void setTestEvent(Event event) {
        this.testEvent = event;
    }




}

