package com.example.holosproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize the buttons
        Button btnViewProfiles = findViewById(R.id.btnViewProfiles);
        Button btnViewEvents = findViewById(R.id.btnViewEvents);
        Button btnViewImages = findViewById(R.id.btnViewImages);

        // Set onClickListeners for each button
        btnViewProfiles.setOnClickListener(v -> {
            // Navigate to the View Profiles Activity
        });

        btnViewEvents.setOnClickListener(v -> {
            // Navigate to the View Events Activity
        });

        btnViewImages.setOnClickListener(v -> {
            // Navigate to the View Images Activity
        });

        // Setup the navigation drawer
        setupDrawer();
    }

    private void setupDrawer() {
        // Setup the DrawerLayout and NavigationView
        DrawerLayout drawerLayout = findViewById(R.id.admin_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.admin_nav_view);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // Handle navigation view item clicks here.
                    return true;
                });
    }
}
