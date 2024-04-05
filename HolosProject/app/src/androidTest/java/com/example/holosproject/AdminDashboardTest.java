package com.example.holosproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) // Added dependency for this AndroidJUnit4
@LargeTest

/**
 * FileName: AdminDashboardTest
 * Description: Runs tests relating to the admin dashboard. Viewing/Deleting events, profiles and images.
 *
 * How does the test work? It first enables "Test Mode" for the AdminViewProfiles, Events, and Images activity.
 * When test mode is enabled in those activites, it will load mock data from MockDataProvider, instead of data from
 * firebase. We then view and delete this mock data through this test.
 **/

public class AdminDashboardTest {

    @Rule
    public ActivityScenarioRule<AdminDashboardActivity> scenario = new
            ActivityScenarioRule<AdminDashboardActivity>(AdminDashboardActivity.class);

    @Before
    public void setUp() {
        // Enable test mode before starting the activity
        // Enabling test mode loads these activities with mock data, instead of data from firebase.
        AdminViewProfilesActivity.enableTestMode();
        AdminViewEventsActivity.enableTestMode();
        AdminViewImagesActivity.enableTestMode();
        AdminViewImagesAdapter.enableTestMode();
    }

    @Test
    // Test for Viewing/Deleting profiles as an admin
    public void ViewAndDeleteProfileTest() throws InterruptedException {

        // Tap on the View Profiles Button
        onView(withId(R.id.btnViewProfiles)).perform(click());

        // Tap the profile at the top using RecyclerViewActions
        onView(withId(R.id.recyclerViewProfiles))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Click on the 'Yes' deletion confirmation dialog
        onView(ViewMatchers.withText("Yes")).perform(click());
    }

    @Test
    // Test for Viewing/Deleting events as an admin
    public void ViewAndDeleteEventTest()  {

        // Tap on the View Events Button
        onView(withId(R.id.btnViewEvents)).perform(click());

        // Tap the event at the top using RecyclerViewActions
        onView(withId(R.id.recyclerViewEvents))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Click on the 'Yes' deletion confirmation dialog
        onView(ViewMatchers.withText("OK")).perform(click());
    }

    @Test
    public void ViewAndDeleteImagesTest() throws InterruptedException {
        onView(withId(R.id.btnViewImages)).perform(click());

        try {
            Thread.sleep(5000); // Sleep for 4 seconds while images load
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Tap first image at the using RecyclerViewActions
        onView(withId(R.id.imagesRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Click on the 'Yes' deletion confirmation dialog
        onView(ViewMatchers.withText("OK")).perform(click());

    }

    @After
    public void tearDown() {
        // Disable test mode after each test
        AdminViewProfilesActivity.disableTestMode();
        AdminViewEventsActivity.disableTestMode();
        AdminViewImagesActivity.disableTestMode();
        AdminViewImagesAdapter.disableTestMode();
    }

}
