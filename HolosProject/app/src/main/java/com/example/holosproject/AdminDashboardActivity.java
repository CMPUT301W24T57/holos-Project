package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar; // Correct import

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

/**
 * FileName: AdminDashboardActivity
 * Description: This is the Admin Dashboard, contains buttons that lead to viewing profiles, events, images. Also contains the drawer menu so we can attend events.

 * AdminDashboardActivity is associated with the activity_admin_dashboard.xml layout file.
 **/



public class AdminDashboardActivity extends AppCompatActivity {

    // References to the toolbar and drawer
    private Toolbar adminToolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize the Toolbar
        adminToolbar = findViewById(R.id.toolbar); // Make sure you have a Toolbar with this ID in your layout
        setSupportActionBar(adminToolbar); // Use the Toolbar as ActionBar
        setTitle("");

        // Initialize the DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.admin_drawer_layout); // Correct ID required
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, adminToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize the NavigationView
        NavigationView navigationView = findViewById(R.id.admin_nav_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Initialize the buttons
        Button btnViewProfiles = findViewById(R.id.btnViewProfiles);
        Button btnViewEvents = findViewById(R.id.btnViewEvents);
        Button btnViewImages = findViewById(R.id.btnViewImages);

        // Set onClickListeners for each button
        btnViewProfiles.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewProfilesActivity.class);
            startActivity(intent);
            // Not using finish();, because we need to be able to get back to this screen.
            // Maybe we add a back button to the view profiles screen?
        });

        btnViewEvents.setOnClickListener(v -> {
            // Navigate to the View Events Activity
        });

        btnViewImages.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewImagesActivity.class);
            startActivity(intent);
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        // Handle different item clicks here

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
