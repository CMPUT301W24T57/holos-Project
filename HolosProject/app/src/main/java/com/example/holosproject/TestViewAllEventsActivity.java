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

public class TestViewAllEventsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Using a RecyclerView to display all of the Events that exist within our app
    private RecyclerView allEventsRecyclerView;
    private AttendeeDashboardEventsAdapter eventsAdapter;
    private final List<Event> allEventsList = new ArrayList<>(); // This is the data source

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


    /**
     * Called when the activity is starting. Sets up the layout and initializes necessary components.
     * If a promo event ID is provided in the intent extras, handles the promo event after a delay to ensure
     * proper initialization of events data.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */

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

        fetchTestProfile();

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

    /**
     * Called after the activity has been paused, resumed, or is returning from another activity.
     * Updates the user profile and username in the navigation drawer header.
     */

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
        List<Event> mockEvents = MockDataProvider.getMockEvents();
        allEventsList.addAll(mockEvents);
        eventsAdapter.notifyDataSetChanged();
    }

    /**
     * Handles the promotional event identified by the given event ID.
     * If the event ID matches an event in the list of all events, it shows the event details dialog.
     *
     * @param eventID The ID of the promotional event to handle.
     */

    private void handlePromo(String eventID) {
        System.out.println("Trying to find " + eventID);
        for (Event event : allEventsList) {
            System.out.println("Trying to find " + eventID + "testing with " + event.getEventId());
            if (eventID.equals(event.getEventId())) {
                showEventDetailsDialog(TestViewAllEventsActivity.this, event);
            }
        }
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

        Switch switchPlanToAttend = diagView.findViewById(R.id.plan_to_attend_list);
        switchPlanToAttend.setChecked(event.getAttendees().contains(MockDataProvider.getMockUser().getUid()));
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
     * Fetches the test profile and updates the navigation drawer menu accordingly.
     * It sets the visibility of the admin login menu item to true.
     */

    private void fetchTestProfile() {
        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        Menu menu = navigationView.getMenu();
        MenuItem adminLoginMenuItem = menu.findItem(R.id.admin_login);
        adminLoginMenuItem.setVisible(true);
    }

}
