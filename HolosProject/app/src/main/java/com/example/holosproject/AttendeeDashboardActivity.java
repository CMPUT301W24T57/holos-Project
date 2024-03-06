package com.example.holosproject;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: AttendeeDashboardActivity
 * Description: This is the logic for the AttendeeDashboard activity. It contains the logic for displaying the RecyclerView, which is the current implemented view for the
 * list of events. This is where the click listeners for the different sections of this screen will go. (The QR Code, Each Event, the drawer pop out menu, etc.)

 * This dashboard shows all of the events the user is currently enrolled in. There is a different activity for all open events.

 * This file also contains first implementation of the drawer menu. This shit was really hard to set up to be honest, lots of different parts.
 * The XML files associated with the drawer are: hamburger_menu.xml, hamburger_menu_header.xml, and activity_attendee_dashboard.xml.

 * Associated with the item_attendee_dashboard.xml layout, and the activity_attendee_dashboard.xml layout.
 **/

public class AttendeeDashboardActivity extends AppCompatActivity
                                       implements NavigationView.OnNavigationItemSelectedListener {

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

    private CollectionReference eventRef = database.collection("eventTestNW");

    private ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{ //basic popup after scanning to test things
        if(result.getContents() != null) {
            String scanContents = result.getContents();
            handleScan(scanContents);
        }
    });

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

        }
        else if (id == R.id.nav_view_registered_events) {   // If we want to navigate to the view we are already in, just close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * scanQRCode
     * Description: Sends user to a QR code scan screen when QR scan floating action button is tapped.
     */
    private void scanQRCode() { // basic QR code scan
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * handleScan
     * Description: Handles the results of a QR scan. Barebones for now, need to figure out event
     * implementation in the firebase before handling things in a more complicated manner.
     *
     */
    private void handleScan(String scanContents) {
        DocumentReference docRef = eventRef.document(scanContents);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttendeeDashboardActivity.this);
                        builder.setTitle("Result");
                        builder.setMessage(scanContents);
                        eventList.add(new Event(scanContents, (String) document.get("Date")));
                        eventsAdapter.notifyItemInserted(eventList.size());
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // dismisses the popup
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AttendeeDashboardActivity.this);
                        builder.setTitle("Result");
                        builder.setMessage("Event Not Found in Firebase");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // dismisses the popup
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                } else {
                    Log.d("Firestore", "Database Error");
                }
            }
    });
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_dashboard);

        NavigationView navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Sample data
        eventList.add(new Event("Event 1", "January 1, 2024"));
        eventList.add(new Event("Event 2", "November 3rd, 2024"));

        // Toolbar is the section at the top of screen.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Setting up the RecyclerView
        // Most of the code for this is found within the AttendeeDashboardEventsActivity file
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsAdapter = new AttendeeDashboardEventsAdapter(eventList);
        eventsRecyclerView.setAdapter(eventsAdapter);

        // TODO: Create click listener for QR Code Button, change the icon to a QR code instead of a camera.
        scanButton = findViewById(R.id.fabQRCode);
        scanButton.setOnClickListener(v-> {
            scanQRCode();
        });


    // TODO: Create the Hamburger Menu pop out on the top right (refer to UI Mockups)
}
}
