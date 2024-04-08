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
 * Description: This is the Admin Dashboard activity, which provides access to various functionalities for an admin user,
 * such as viewing profiles, events, and images, as well as navigating to other dashboard screens. This activity includes
 * a drawer menu for easy navigation.
 **/
public class AdminDashboardActivity extends AppCompatActivity {

    // References to the toolbar and drawer
    private Toolbar adminToolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    /**
     * This method is called when the activity is first created. It initializes the layout,
     * toolbar, navigation drawer, navigation view, and buttons for various administrative actions.
     * It also sets onClickListeners for each button to navigate to their respective activities.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state, if any.
     */

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
            Intent intent = new Intent(this, AdminViewEventsActivity.class);
            startActivity(intent);
        });

        btnViewImages.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminViewImagesActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Called when the activity will start interacting with the user.
     * It updates the navigation header in the navigation view when the activity resumes.
     */

    protected void onResume() {
        super.onResume();

        NavigationView navigationView = findViewById(R.id.admin_nav_view);
        NavigationDrawerUtils.updateNavigationHeader(navigationView);

    }

    /**
     * Called when a menu item in the options menu is selected.
     * It handles the selection of items in the ActionBarDrawerToggle.
     *
     * @param item The selected menu item.
     * @return True if the selection was handled successfully, otherwise false.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles navigation item clicks.
     *
     * @param item The selected menu item.
     * @return True if the item is handled successfully, false otherwise.
     */
    private boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_edit_profile) {
            // Navigate to Edit your profile
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_view_all_events) {
            // Navigate to Viewing all Events
            Intent intent = new Intent(this, ViewAllEventsActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_view_registered_events) {
            // Navigate to Viewing your signed up evetns
            Intent intent = new Intent(this, AttendeeDashboardActivity.class);
            startActivity(intent);
            finish();
        }  else if (id == R.id.nav_view_organizer_dashboard) {
            // Navigate to Organizer Dashboard
            Intent intent = new Intent(this, OrganizerDashboardActivity.class);
            startActivity(intent);
            finish();
        }
        // Add more navigation items if needed

        DrawerLayout drawer = findViewById(R.id.admin_drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}
