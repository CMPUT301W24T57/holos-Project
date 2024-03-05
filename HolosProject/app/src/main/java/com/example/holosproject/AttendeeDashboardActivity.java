package com.example.holosproject;
import androidx.appcompat.widget.Toolbar;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

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
    }

    // TODO: Create click listener for QR Code Button, change the icon to a QR code instead of a camera.

    // TODO: Create the Hamburger Menu pop out on the top right (refer to UI Mockups)

}
