package com.example.holosproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: AttendeeDashboardActivity
 * Description: This is the logic for the AttendeeDashboard activity. It contains the logic for displaying the RecyclerView, which is the current implemented view for the
 * list of events. This is where the click listeners for the different sections of this screen will go. (The QR Code, Each Event, the Hamburger pop out menu, etc.)

 * Associated with the item_attendee_dashboard.xml layout, and the activity_attendee_dashboard.xml layout.
 **/

public class AttendeeDashboardActivity extends AppCompatActivity {

    // Using a RecyclerView to display all of the Events our user is currently enrolled in
    private RecyclerView eventsRecyclerView;
    private AttendeeDashboardEventsAdapter eventsAdapter;
    private List<Event> eventList = new ArrayList<>(); // This is the data source

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendee_dashboard);

        // Sample data
        eventList.add(new Event("Event 1", "January 1, 2024"));
        eventList.add(new Event("Event 2", "November 3rd, 2024"));

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
