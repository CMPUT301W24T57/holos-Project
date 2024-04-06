package com.example.holosproject;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FileName: ViewAllEventsActivity
 * Description: Activity for viewing all events within the app, as an attendee
 * Brought to this screen when a user selects the "View All Events" option from the drawer menu.

 * XML Files associated with this are: activity_attendee_view_all_events.xml
 **/

public class ViewAllEventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Using a RecyclerView to display all of the Events that exist within our app
    private RecyclerView allEventsRecyclerView;
    private AttendeeDashboardEventsAdapter eventsAdapter;
    private List<Event> allEventsList = new ArrayList<>(); // This is the data source
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser(); // The current user

    // References to The drawer menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    // OnNavigationItemSelected: When a user selects an item from the nav drawer menu, what should happen?
    /**
     * When a user selects an item from the nav drawer menu, this method is called.
     * @param item The selected menu item.
     * @return True if the event was handled successfully, false otherwise.
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // If the user selects one of the items in the drawer, what happens? (navigate to that respective activity)
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_view_registered_events) {
            Intent intent = new Intent(this, AttendeeDashboardActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_view_all_events) {    // if we try to navigate to current view, close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_view_organizer_dashboard) {
            Intent intent = new Intent(this, OrganizerDashboardActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.admin_login) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }

        else if (id == R.id.nav_admin_dashboard) {
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_view_all_events);

        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Update the navigation drawer header with user info
        NavigationDrawerUtils.updateNavigationHeader(navigationView);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fetchUserProfile(currentUser.getUid());

        // Set up the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        allEventsRecyclerView = findViewById(R.id.allEventsRecyclerView);
        allEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new AttendeeDashboardEventsAdapter(allEventsList);
        allEventsRecyclerView.setAdapter(eventsAdapter);

        fetchEvents();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String promoID = bundle.getString("promo");
            String eventID = promoID.replace("promo", "");
            // NEED TO DELAY BEFORE TRYING TO HANDLE PROMO,
            // AS FETCHEVENTS AND HANDLEPROMO OCCUR ASYNCHRONOUSLY, HANDLEPROMO MAY RUN INTO AN EMPTY LIST
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handlePromo(eventID);
                }
            }, 1000);
        }
        // TODO: Fetch all events from Firestore and update the RecyclerView
    }

    @Override
    protected void onResume() {
        // After resuming, updates user profile and username (needed for the case we came back from editprofile)
        super.onResume();
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        NavigationDrawerUtils.updateNavigationHeader(navigationView);
    }

    /**
     * Fetches events from the database and updates the RecyclerView.
     */
    private void fetchEvents() {
        // Fetches events from database, and does manual serialization :-(
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        allEventsList.clear(); // Clear the list before adding new items
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Manual deserialization
                            String name = document.getString("name");
                            String date = document.getString("date");
                            String time = document.getString("time");
                            String address = document.getString("address");
                            String creator = document.getString("creator");
                            String eventId = document.getId();
                            String imageUrl = document.getString("imageUrl");

                            int limit = Integer.MAX_VALUE;
                            Long limitLong = document.getLong("limit");
                            if (limitLong != null) {
                                limit = limitLong.intValue();
                            }
                            ArrayList<String> attendees = (ArrayList<String>) document.get("attendees");

                            Event event = new Event(name, date, time, address, creator);
                            event.setEventId(eventId);
                            event.setImageUrl(imageUrl);
                            event.setAttendees(attendees); // Assuming you have a setter for attendees
                            Log.d("SetLimit", "Limit: " + limit);
                            event.setLimit(limit);
                            Log.d("EventLimit","Limit: "+event.getLimit());
                            allEventsList.add(event);
                        }
                        eventsAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                        // Handle the error properly
                    }
                });
    }

    private void handlePromo(String eventID) {
        System.out.println("Trying to find " + eventID);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventsRef = db.collection("events");
        DocumentReference docRef = eventsRef.document(eventID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Integer position = 0;
                        for (Event event : allEventsList) {
                            System.out.println("Trying to find " + eventID + "testing with " + event.getEventId());
                            if (eventID.equals(event.getEventId())) {
                                showEventDetailsDialog(ViewAllEventsActivity.this, event);
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * For the sake of time, took this directly from the adapter to call it here,
     * As I could not figure out a way to programatically click on the right item using the adapter itself.
     * Shows the details of an event to the user.
     * @param context
     * @param event
     */
    private void showEventDetailsDialog(Context context, Event event) {
        AlertDialog.Builder dispbuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View diagView = inflater.inflate(R.layout.event_info, null);
        dispbuilder.setView(diagView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        Switch switchPlanToAttend = diagView.findViewById(R.id.plan_to_attend_list);
        switchPlanToAttend.setChecked(event.getAttendees().contains(currentUserId));
        TextView textViewEventName = diagView.findViewById(R.id.textViewEventNameDiag);
        TextView textViewEventDate = diagView.findViewById(R.id.textViewEventDateDiag);
        //TextView textViewEventTime = diagView.findViewById(R.id.textViewEventTimeDiag);
        TextView textViewEventLocation = diagView.findViewById(R.id.textViewEventLocationDiag);
        //TextView textViewEventAttendeeList = diagView.findViewById(R.id.event_attendee_list);
        ImageView eventPoster = diagView.findViewById(R.id.event_poster);

        textViewEventName.setText("EVENT NAME: " + event.getName());
        textViewEventDate.setText("EVENT DATE: " + event.getDate());
        //textViewEventTime.setText("EVENT TIME: " + event.getTime());
        textViewEventLocation.setText("EVENT LOCATION: " + event.getAddress());
        System.out.println(event.getImageUrl());
        Picasso.get().load(event.getImageUrl()).into(eventPoster);

        List<String> attendeeIds1 = event.getAttendees();
        //displayAttendeeNames(attendeeIds1, textViewEventAttendeeList, db);

        // Made the updating of the switch use firestore transactions.
        // This way even if users open the dialog at the same time the limit will never be surpassed.
        switchPlanToAttend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final DocumentReference eventRef = db.collection("events").document(event.getEventId());
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                        List<String> currentAttendees = (List<String>) eventSnapshot.get("attendees");
                        if (currentAttendees == null) {
                            currentAttendees = new ArrayList<>();
                        }
                        if (isChecked) {
                            // Trying to add the current user
                            if (!currentAttendees.contains(currentUserId) && currentAttendees.size() < eventLimit) {
                                currentAttendees.add(currentUserId);
                                transaction.update(eventRef, "attendees", currentAttendees);
                                addUserEvent(currentUserId, event.getEventId());
                            } else {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(context, "Error: The Event is Full", Toast.LENGTH_SHORT).show());
                                return null;
                            }
                        } else {
                            // Removing current User
                            if (currentAttendees.contains(currentUserId)) {
                                currentAttendees.remove(currentUserId);
                                transaction.update(eventRef, "attendees", currentAttendees);
                                removeUserEvent(currentUserId, event.getEventId());
                            }
                        }
                        return null; // Transaction must return null if void
                    }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction successfully completed"))
                    .addOnFailureListener(e -> Log.e(TAG, "Transaction failed: ", e));
        });

        dispbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface diagdisp, int i) {
                diagdisp.dismiss();
            }
        });

        AlertDialog diag = dispbuilder.create();
        diag.show();
    }

    /**
     * Adds an event to a user's list of events.
     * @param userId: user ID to be added to
     * @param eventId: event ID to be added
     */

    private void addUserEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("userProfiles").document(userId);

        userRef.update("myEvents", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's list"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding event to user's list", e));
    }

    /**
     * Removes an event from a user's myEvents list
     * @param userId: the ID of the user we are deleting from
     * @param eventId: the event ID to delete
     */

    private void removeUserEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("userProfiles").document(userId);

        userRef.update("myEvents", FieldValue.arrayRemove(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event removed"))
                .addOnFailureListener(e -> Log.e(TAG, "Error removing event", e));
    }

    /**
     * Displays the names of attendees for an event.
     * @param attendeeIds: a list of attendee IDs to display
     * @param textViewEventAttendeeList: where to display the attendees
     * @param db: a firebase database to grab attendees from
     */
    private void displayAttendeeNames(List<String> attendeeIds, TextView textViewEventAttendeeList, FirebaseFirestore db) {
        List<String> attendeeNames = new ArrayList<>();

        // Since the counter decrements it ensures that we find all attendees
        AtomicInteger fetchCounter = new AtomicInteger(attendeeIds.size());

        for (String attendeeId : attendeeIds) {
            db.collection("userProfiles").document(attendeeId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Find the name
                            String name = documentSnapshot.getString("name");
                            if (name != null) {
                                attendeeNames.add(name);
                            }
                        }
                        // Decrement the counter and check if all fetches are done
                        if (fetchCounter.decrementAndGet() == 0) {
                            String namesStr = String.join(", ", attendeeNames);
                            textViewEventAttendeeList.setText("Attendees: " + namesStr);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching user profile", e);
                        if (fetchCounter.decrementAndGet() == 0) {
                            String namesStr = String.join(", ", attendeeNames);
                            textViewEventAttendeeList.setText("Attendees: " + namesStr);
                        }
                    });
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

}
