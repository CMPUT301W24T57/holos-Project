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

    // References to The drawer menu
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    // OnNavigationItemSelected: When a user selects an item from the nav drawer menu, what should happen?
    public boolean onNavigationItemSelected(MenuItem item) {
        // If the user selects one of the items in the drawer, what happens? (navigate to that respective activity)
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_view_registered_events) {
            Intent intent = new Intent(this, AttendeeDashboardActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_view_all_events) {    // if we try to navigate to current view, close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_view_all_events);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_drawer_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        // TODO: Fetch all events from Firestore and update the RecyclerView
    }

}
