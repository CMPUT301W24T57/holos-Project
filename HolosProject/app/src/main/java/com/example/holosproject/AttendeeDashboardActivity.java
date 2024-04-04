package com.example.holosproject;

import android.annotation.SuppressLint;
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
import java.util.Map;
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

public class AttendeeDashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "TestScreen";

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    // Using a RecyclerView to display all of the Events our user is currently enrolled in
    private RecyclerView eventsRecyclerView;
    private AttendeeDashboardEventsAdapter eventsAdapter;
    private List<Event> eventList = new ArrayList<>(); // This is the data source

    // References to The drawer menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    // References for attendee QR scan:
    private FloatingActionButton scanButton;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference eventsRef = database.collection("events");
    private CollectionReference customRef = database.collection("Custom QR Data");
    private CollectionReference usersRef = database.collection("userProfiles");

    private boolean geolocation;
    private ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> { //basic popup after scanning to test things
        if (result.getContents() != null) {
            String scanContents = result.getContents();
            handleScan(scanContents);
        }
    });
    private ListenerRegistration eventsListener;
    private FusedLocationProviderClient fusedLocationClient;
    private String locationID;


    @Override
    protected void onResume() {
        super.onResume();

        // Updating the drawer header with potentially new name/profile image
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        NavigationDrawerUtils.updateNavigationHeader(navigationView);

        // If there is a change continue on with the code
        eventsRef.addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                Log.e(TAG, "Listen failed.", error);
                return;
            } else {
                fetchEvents();
                eventsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (eventsListener != null) {
            eventsListener.remove();
        }
    }


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

        // calling fetchUserProfile to see if the user is an admin, if they are, allow them to access the Admin Dashboard through the Drawer.
        fetchUserProfile(currentUser.getUid());

        // Setting up the RecyclerView
        // Most of the code for this is found within the AttendeeDashboardEventsActivity file
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new AttendeeDashboardEventsAdapter(eventList);
        eventsRecyclerView.setAdapter(eventsAdapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        scanButton = findViewById(R.id.fabQRCode);
        scanButton.setOnClickListener(v -> {
            scanQRCode();
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String eventID = bundle.getString("title");
            DocumentReference docRef = eventsRef.document(eventID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            rsvpEvent(eventID, document);
                            fetchEvents();
                            eventsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

        DocumentReference currentUserRef = usersRef.document(currentUser.getUid());

        currentUserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        geolocation = document.getBoolean("geolocationEnabled");
                    }
                }
            }
        });
    }


    // Grabs users role. Used to determine if user can access the admin dashboard or not.
    private void fetchUserProfile(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userProfiles").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                if ("admin".equals(role)) {
                    // Show the admin dashboard option in the navigation drawer.
                    NavigationView navigationView = findViewById(R.id.nav_drawer_view);
                    Menu menu = navigationView.getMenu();
                    MenuItem adminDashboardMenuItem = menu.findItem(R.id.nav_admin_dashboard);
                    adminDashboardMenuItem.setVisible(true);
                }
            }
        }).addOnFailureListener(e -> {
            // Handle error, e.g., show a message to the user.
        });
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Fetches all events from the database, and if the user is attending them, displays them.
     * Inefficient (?) but the other way was way too buggy for some reason...
     */

    private void fetchEvents() {
        // Fetches events from database, and does manual serialization :-(
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
                            String imageUrl = document.getString("imageUrl");
                            ArrayList<String> attendees = (ArrayList<String>) document.get("attendees");

                            Event event = new Event(name, date, time, address, creator);
                            event.setEventId(eventId);
                            event.setImageUrl(imageUrl);
                            event.setAttendees(attendees);
                            if (attendees.contains(currentUser.getUid())) {
                                eventList.add(event);
                            }
                        }
                        eventsAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                        // Handle the error properly
                    }
                });
    }

    /**
     * Sends user to a QR code scan screen when QR scan floating action button is tapped.
     */
    private void scanQRCode() { // basic QR code scan
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * Sends user to a (barebones) event details display screen where they can check in.
     *
     * @param scanContents: the contents of a QR code scan, should be the ID of a valid event
     */

    private void goToEventDisplay(String scanContents) {
        Intent intent = new Intent(this, EventDisplay.class);
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
    private void rsvpEvent(String eventID, DocumentSnapshot document) {
        //Toast.makeText(this, "You have successfully checked in.", Toast.LENGTH_SHORT).show();
        // add user to event:
        locationID = eventID;
        HashMap<String, String> checkIns = (HashMap<String, String>) document.get("checkIns");
        ArrayList<String> attendees = (ArrayList<String>) document.get("attendees");
        ArrayList<GeoPoint> locations = (ArrayList<GeoPoint>) document.get("locations");
        if (!checkIns.containsKey(currentUser.getUid())) {
            addUserEvent(currentUser.getUid(), eventID);
            DocumentReference eventRef = database.collection("events").document(eventID);
            checkIns.put(currentUser.getUid(), "1");
            eventRef.update("checkIns", checkIns)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to checkins"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding user", e));

            // Check and update user location, respecting the geolocation preference.
            checkAndUpdateUserLocation(eventID);
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
//            } else {
//                fusedLocationClient.getLastLocation()
//                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                            @Override
//                            public void onSuccess(Location location) {
//                                // Got last known location. In some rare situations this can be null.
//                                if (location != null) {
//                                    double latitude = location.getLatitude();
//                                    double longitude = location.getLongitude();
//                                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
//                                    eventRef.update("locations", FieldValue.arrayUnion(geoPoint));
//                                }
//                            }
//                        });
//
//            }
//        }
        }
    else if (checkIns.containsKey(currentUser.getUid())) {
            DocumentReference eventRef = database.collection("events").document(eventID);
            Integer parsedInt = Integer.valueOf(checkIns.get(currentUser.getUid()));
            parsedInt++;
            checkIns.put(currentUser.getUid(), String.valueOf(parsedInt));
            eventRef.update("checkIns", checkIns)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to checkins"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding user", e));
        }
        if (!attendees.contains(currentUser.getUid())) {
            DocumentReference eventRef = database.collection("events").document(eventID);
            eventRef.update("attendees", FieldValue.arrayUnion(currentUser.getUid()))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "User added to attendees"))
                    .addOnFailureListener(e -> Log.e(TAG, "Error adding user", e));
        }
    }

    /**
     * Handles what happens when a valid QR code is scanned.
     * Given the text contained in the QR code, attempts to match the text with the title of a document
     * In the events collection in the database. Given it finds a match, it takes the title of the event
     * and its date and adds it to the user's visible event list. (Does not add to anything in database yet)
     *
     * @param scanContents: a string containing the contents of the scanned QR code
     */
    private void handleScan(String scanContents) {
        // if this is a promo QR,
        if (scanContents.contains("promo")) {
            String strippedContents = scanContents.replace("promo", "");
            DocumentReference docRef = eventsRef.document(strippedContents);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            goToPromoDisplay(scanContents);
                        }
                    } else {
                        Log.d("Firestore", "Database Error");
                    }
                }
            });
        }
        // if this is just a check-in QR,
        else {
            DocumentReference qrRef = customRef.document(Integer.toString(Objects.hashCode(scanContents)));
            qrRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            goToEventDisplay(document.getString("linkedEvent"));
                        } else {
                            DocumentReference docRef = eventsRef.document(scanContents);
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            goToEventDisplay(scanContents);
                                        }
                                    } else {
                                        Log.d("Firestore", "Database Error");
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    double latitude = location.getLatitude();
                                    double longitude = location.getLongitude();
                                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                                    DocumentReference eventRef = database.collection("events").document(locationID);
                                    eventRef.update("locations", FieldValue.arrayUnion(geoPoint));
                                }
                            }
                        });
            }
        }
    }

    private void checkAndUpdateUserLocation(String eventID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DocumentReference userProfileRef = db.collection("userProfiles").document(currentUser.getUid());
            userProfileRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Boolean isGeolocationEnabled = documentSnapshot.getBoolean("geolocationEnabled");
                    if (isGeolocationEnabled != null && isGeolocationEnabled) {
                        updateUserLocation(eventID);
                    } else {
                        Log.d(TAG, "Geolocation is disabled by the user. Not adding Location data.");
                    }
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error fetching user profile", e));
        }
    }

    private void updateUserLocation(String eventID) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                    DocumentReference eventRef = database.collection("events").document(eventID);
                    eventRef.update("locations", FieldValue.arrayUnion(geoPoint));
                }
            });
        }
    }
}
